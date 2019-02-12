package cardchanneler.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import cardchanneler.orbs.ChanneledCard;

public class ChannelCardAction extends AbstractGameAction{
	    private static final UIStrings uiStrings;
	    public static final String[] TEXT;
	    
	    public ChannelCardAction() {
	        actionType = ActionType.CARD_MANIPULATION;
	        duration = 0.5f;
	    }
	    
	    @Override
	    public void update() {
	        if (duration == 0.5f) {	        	
	            AbstractDungeon.handCardSelectScreen.open(ChannelCardAction.TEXT[0], 1, false, true, false, false, true);
	            AbstractDungeon.actionManager.addToBottom(new WaitAction(0.25f));
	            tickDuration();
	            return;
	        }
	        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
	            for (final AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
	            	//TODO: Copy stats defined by mods too
	                final AbstractOrb orb = new ChanneledCard(c.makeStatEquivalentCopy());
	                AbstractDungeon.actionManager.addToBottom(new ChannelAction(orb));
	            }
	            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
	        }
	        tickDuration();
	    }
	    
	    static {
	        uiStrings = CardCrawlGame.languagePack.getUIString("CardChanneler:ChannelCardAction");
	        TEXT = ChannelCardAction.uiStrings.TEXT;
	    }

}
