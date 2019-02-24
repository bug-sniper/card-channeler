package cardchanneler.actions;

// Adjusted version of NightmareAction. Thanks, Megacrit.

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

import cardchanneler.orbs.ChanneledCard;
import cardchanneler.patches.XCostEvokePatch;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class ChannelCardAction
        extends AbstractGameAction
{
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CardChanneler:ChannelCardAction");
    public static final String[] TEXT = uiStrings.TEXT;
    private AbstractPlayer player;
    private static final float DURATION = Settings.ACTION_DUR_XFAST;

    public ChannelCardAction()
    {
        actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        duration = DURATION;
        player = AbstractDungeon.player;
    }

    public void update()
    {
        if (duration == DURATION)
        {
            if (player.hand.isEmpty())
            {
                isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, true, true);
            tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved)
        {
            for (final AbstractCard card : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
            	AbstractMonster monster = AbstractDungeon.getRandomMonster();
                if (card.canUse(player, monster)) {
                    final AbstractOrb orb = new ChanneledCard(card);
                    AbstractDungeon.actionManager.addToTop(new ChannelAction(orb));
                    if (card.cost == -1){
                    	//X-cost card
                    	XCostEvokePatch.CostAtChannelField.costAtChannel.set(card, EnergyPanel.totalCount);
                    	player.energy.use(EnergyPanel.totalCount);
                    }
                    if (card.costForTurn > 0 && !card.freeToPlayOnce && (!player.hasPower("Corruption") || card.type != AbstractCard.CardType.SKILL)) {
                        player.energy.use(card.costForTurn);
                    }
                    if (!player.hand.canUseAnyCard() && !player.endTurnQueued) {
                        AbstractDungeon.overlayMenu.endTurnButton.isGlowing = true;
                    }
                }
                else {
                    AbstractDungeon.effectList.add(new ThoughtBubble(player.dialogX, player.dialogY, 3.0f, card.cantUseMessage, true));
                    player.hand.addToTop(card);
                }

            }

            AbstractDungeon.handCardSelectScreen.selectedCards.clear();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}