package cardchanneler.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import cardchanneler.orbs.ChanneledCard;

public class OrbTargettingHelper {
    private static ChanneledCard selectedOrb = null;
    private static DottedArrowFromOrb arrow = null;
    private static State state = State.START;
    
    private enum State {
            START,
            DRAGGING_ON_ORB,
            DRAGGING_OFF_ORB,
            NON_DRAG_TARGETTING,
    };
    
    /**
     * The required initialization for this class
     * @param arrow The object that renders arrows, which have some of their
     * values determined by the code in this class.
     */
    public static void setArrow(DottedArrowFromOrb arrow){
        assert OrbTargettingHelper.arrow == null;
        OrbTargettingHelper.arrow = arrow;
    }
    
    //sets the dragged orb private variable based on the mouse position
    private static void setSelectedOrb(){
        selectedOrb = null;
        for (AbstractOrb orb : AbstractDungeon.player.orbs) {
            if (orb.hb.hovered && orb.ID == ChanneledCard.ORB_ID) {
                AbstractCard card = ((ChanneledCard)orb).card;
                if (card.target == CardTarget.ENEMY ||
                    card.target == CardTarget.SELF_AND_ENEMY){
                    selectedOrb = (ChanneledCard) orb;
                    break;
                }
            }
        }
    }
    
    //sets arrow.hoveredCreature mouse position
    private static void setHoveredCreatureFromMouse(){
        arrow.hoveredCreature = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if ((m.hb.hovered) && (!m.isDying)) {
                arrow.hoveredCreature = m;
                break;
            }
        }
    }
    
    private static boolean pressedCancelKey(){
        if (InputActionSet.cancel.isJustPressed() || 
            CInputActionSet.cancel.isJustPressed()) {
                return true;
            }
        for (int i = 0; i < 10; i++) {
            if (InputActionSet.selectCardActions[i].isJustPressed()){
                return true;
            }
        }
        return false;
    }

    public static void update() {
        if (AbstractDungeon.getMonsters() == null){
            //You've already won the battle.
            return;
        }
        
        switch (state){
            case START:
                if (InputHelper.justClickedLeft){
                    setSelectedOrb();
                    if (selectedOrb != null){
                        state = State.DRAGGING_ON_ORB;
                    }
                }
                break;
            case DRAGGING_ON_ORB:
                if (!selectedOrb.hb.hovered){
                    arrow.setOrb(selectedOrb);
                    arrow.isHidden = false;
                    state = State.DRAGGING_OFF_ORB;
                } else if (InputHelper.justReleasedClickLeft){
                    arrow.setOrb(selectedOrb);
                    arrow.isHidden = false;
                    state = State.NON_DRAG_TARGETTING;
                }
                break;
            case DRAGGING_OFF_ORB:
                setHoveredCreatureFromMouse();
                if (InputHelper.justReleasedClickLeft || pressedCancelKey()){
                    if (arrow.hoveredCreature == null){
                        //The played cancelled targetting
                    } else{
                        //The player chose a monster to target
                        selectedOrb.monsterTarget = (AbstractMonster) arrow.hoveredCreature;
                    }
                    arrow.isHidden = true; 
                    state = State.START;
                }
                break;
            case NON_DRAG_TARGETTING:
                setHoveredCreatureFromMouse();
                if (pressedCancelKey()){
                    arrow.isHidden = true;
                    state = State.START;
                } else if (InputHelper.justReleasedClickLeft){
                    if (arrow.hoveredCreature == null){
                        //The played cancelled targetting
                        setSelectedOrb();
                        if (selectedOrb == null){
                            //The play did not pick another orb to target with
                            arrow.isHidden = true;
                            state = State.START;
                        } else {
                            //The play picked another orb to target with
                            arrow.setOrb(selectedOrb);
                        }
                    } else{
                        //The player chose a monster to target
                        selectedOrb.monsterTarget = (AbstractMonster) arrow.hoveredCreature;
                        arrow.isHidden = true;
                        state = State.START;
                    }
                }
                break;
        }
    }

}