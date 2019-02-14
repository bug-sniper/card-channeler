package cardchanneler.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import cardchanneler.orbs.ChanneledCard;

//Just an action to reset ChanneledCard.beingLost in a the proper order
public class ResetChaneledCardBeingLostAction extends AbstractGameAction {
    
    @Override
    public void update() {
        ChanneledCard.orbBeingLost = false;
        isDone = true;
    }
}