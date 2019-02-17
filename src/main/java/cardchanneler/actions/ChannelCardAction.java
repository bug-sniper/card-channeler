package cardchanneler.actions;

// Adjusted version of NightmareAction. Thanks, Megacrit.

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import cardchanneler.orbs.ChanneledCard;

public class ChannelCardAction
        extends AbstractGameAction
{
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CardChanneler:ChannelCardAction");
    public static final String[] TEXT = uiStrings.TEXT;
    private AbstractPlayer p;
    private static final float DURATION = Settings.ACTION_DUR_XFAST;

    public ChannelCardAction()
    {
        actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        duration = DURATION;
        p = AbstractDungeon.player;
    }

    public void update()
    {
        if (duration == DURATION)
        {
            if (p.hand.isEmpty())
            {
                isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, true);
            tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved)
        {
            for (final AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                final AbstractOrb orb = new ChanneledCard(c);
                AbstractDungeon.actionManager.addToTop(new ChannelAction(orb));
            }

            AbstractDungeon.handCardSelectScreen.selectedCards.clear();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}