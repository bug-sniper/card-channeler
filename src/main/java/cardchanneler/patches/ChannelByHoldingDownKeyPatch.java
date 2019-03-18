package cardchanneler.patches;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

import cardchanneler.orbs.ChanneledCard;
import cardchanneler.relics.CardChannelerRelic;

@SpirePatch(    
        clz=AbstractPlayer.class,    
        method="playCard"    
)
public class ChannelByHoldingDownKeyPatch {
    
    //the same as what's in the base game code, but for this class because
    //it's private there
    private static boolean queueContains(final AbstractCard card) {
        for (final CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
            if (i.card == card) {
                return true;
            }
        }
        return false;
    }
    
    public static SpireReturn<?> Prefix(AbstractPlayer __instance) {
        if ((AbstractDungeon.player != null &&
             AbstractDungeon.player.hasRelic(CardChannelerRelic.ID)) &&
                (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)||
                 Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))){
            InputHelper.justClickedLeft = false;
            __instance.hoverEnemyWaitTimer = 1.0f;
            AbstractCard card = __instance.hoveredCard;
            card.unhover();
            
            try{
                if (!queueContains(__instance.hoveredCard)) {
                    Field f1 = AbstractPlayer.class.getDeclaredField("hoveredMonster");
                    f1.setAccessible(true);
                    AbstractMonster target = (AbstractMonster) f1.get(__instance);
                    if (card.canUse(__instance, target)) {
                        final ChanneledCard orb = new ChanneledCard(card);
                        AbstractDungeon.actionManager.addToTop(new ChannelAction(orb));
                        if (card.cost == -1){
                            //X-cost card
                            XCostEvokePatch.CostAtChannelField.costAtChannel.set(card, EnergyPanel.totalCount);
                            __instance.energy.use(EnergyPanel.totalCount);
                        }
                        if (card.costForTurn > 0 && !card.freeToPlayOnce && (!__instance.hasPower("Corruption") || card.type != AbstractCard.CardType.SKILL)) {
                            __instance.energy.use(card.costForTurn);
                        }
                        if (!__instance.hand.canUseAnyCard() && !__instance.endTurnQueued) {
                            AbstractDungeon.overlayMenu.endTurnButton.isGlowing = true;
                        }
                        
                        if (card.target == AbstractCard.CardTarget.ENEMY || 
                            card.target == AbstractCard.CardTarget.SELF_AND_ENEMY){
                            orb.monsterTarget = target;
                        }
                    } else{
                        AbstractDungeon.effectList.add(new ThoughtBubble(__instance.dialogX, __instance.dialogY, 3.0f, card.cantUseMessage, true));
                    }
                }

                //isUsingClickDragControl is private but needs to be reset
                Field f2 = AbstractPlayer.class.getDeclaredField("isUsingClickDragControl");
                f2.setAccessible(true);
                f2.setBoolean(__instance, false);

            } catch (IllegalAccessException e){
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            
            __instance.hoveredCard = null;
            __instance.isDraggingCard = false;
            __instance.hand.removeCard(card);
            __instance.hand.refreshHandLayout();
            return SpireReturn.Return(null);
        }else{    
            return SpireReturn.Continue();    
        }    
    }    
}
