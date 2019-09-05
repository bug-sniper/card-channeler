package cardchanneler.actions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.Field;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;

public class UseChanneledCardAction extends UseCardAction {
	    
	public UseChanneledCardAction(AbstractCard targetCard) {
		super(targetCard);
	}
	
	public UseChanneledCardAction(AbstractCard targetCard, AbstractMonster target) {
		super(targetCard, target);
	}

	@Override
    public void update() {
        //targetCard is private but needs to be reset
        Field f1 = UseCardAction.class.getDeclaredField("targetCard");
        f1.setAccessible(true);
        AbstractCard targetCard = (AbstractCard) f1.get(this);
		
        if (this.duration == 0.15f) {
            for (final AbstractPower p : AbstractDungeon.player.powers) {
                if (!targetCard.dontTriggerOnUseCard) {
                    p.onAfterUseCard(targetCard, this);
                }
            }
            for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                for (final AbstractPower p2 : m.powers) {
                    if (!targetCard.dontTriggerOnUseCard) {
                        p2.onAfterUseCard(targetCard, this);
                    }
                }
            }
            targetCard.freeToPlayOnce = false;
            if (targetCard.purgeOnUse) {
                AbstractDungeon.actionManager.addToTop(new ShowCardAndPoofAction(this.targetCard));
                this.isDone = true;
                AbstractDungeon.player.cardInUse = null;
                return;
            }
            if (targetCard.type == AbstractCard.CardType.POWER) {
                AbstractDungeon.actionManager.addToTop(new ShowCardAction(targetCard));
                if (Settings.FAST_MODE) {
                    AbstractDungeon.actionManager.addToTop(new WaitAction(0.1f));
                }
                else {
                    AbstractDungeon.actionManager.addToTop(new WaitAction(0.7f));
                }
                AbstractDungeon.player.hand.empower(targetCard);
                this.isDone = true;
                AbstractDungeon.player.hand.applyPowers();
                AbstractDungeon.player.hand.glowCheck();
                AbstractDungeon.player.cardInUse = null;
                return;
            }
            AbstractDungeon.player.cardInUse = null;
            if (!this.exhaustCard) {
                if (this.reboundCard) {
                    AbstractDungeon.player.hand.moveToDeck(targetCard, false);
                }
                else {
                    AbstractDungeon.player.hand.moveToDiscardPile(targetCard);
                }
            }
            else {
                targetCard.exhaustOnUseOnce = false;
                if (AbstractDungeon.player.hasRelic("Strange Spoon") && targetCard.type != AbstractCard.CardType.POWER) {
                    if (AbstractDungeon.cardRandomRng.randomBoolean()) {
                        AbstractDungeon.player.getRelic("Strange Spoon").flash();
                        AbstractDungeon.player.hand.moveToDiscardPile(targetCard);
                    }
                    else {
                        AbstractDungeon.player.hand.moveToExhaustPile(targetCard);
                        CardCrawlGame.dungeon.checkForPactAchievement();
                    }
                }
                else {
                    AbstractDungeon.player.hand.moveToExhaustPile(targetCard);
                    CardCrawlGame.dungeon.checkForPactAchievement();
                }
            }
            if (targetCard.dontTriggerOnUseCard) {
                targetCard.dontTriggerOnUseCard = false;
            }
        }
        this.tickDuration();
    }
}
