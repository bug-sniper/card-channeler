package cardchanneler.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import cardchanneler.orbs.ChanneledCard;

//Just an action to toggle ChanneledCard.beingEvoked in a the proper order
public class ToggleChanneledCardBeingEvokedAction extends AbstractGameAction {
    
    @Override
    public void update() {
        ChanneledCard.beingEvoked = !ChanneledCard.beingEvoked;
        isDone = true;
    }
}
