package helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import cardchanneler.orbs.ChanneledCard;

public class OrbTargettingHelper {
    private static ChanneledCard draggedOrb = null;
    private static DottedArrowFromOrb arrow = null;
    
    public static void setArrow(DottedArrowFromOrb arrow){
    	assert OrbTargettingHelper.arrow == null;
    	OrbTargettingHelper.arrow = arrow;
    }

    public static void update() {
    	
//    	if (AbstractDungeon.player == null){
//    		return;
//    	}
//    	if (arrow == null){
//			return;
//		}
    	if (AbstractDungeon.getMonsters() == null){
    		return;
    	}
    	
        // start dragging
        if (draggedOrb == null && InputHelper.justClickedLeft) {
            for (AbstractOrb orb : AbstractDungeon.player.orbs) {
                if (orb.hb.hovered && orb.ID == ChanneledCard.ORB_ID) {
                	AbstractCard card = ((ChanneledCard)orb).card;
                	if (card.target == CardTarget.ENEMY ||
                		card.target == CardTarget.ENEMY){
	                    draggedOrb = (ChanneledCard) orb;
	                    break;
                	}
                }
            }
        }
        
        arrow.hoveredCreature = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if ((m.hb.hovered) && (!m.isDying)) {
                arrow.hoveredCreature = m;
                break;
            }
        }
        
        // update drag
        if (draggedOrb != null) {
        	arrow.setOrb(draggedOrb);
        	arrow.isHidden = false;
        	System.out.println("Dragging org: " + draggedOrb.card.name);
        	if (InputHelper.justReleasedClickLeft) {
                if (arrow.hoveredCreature != null){
                	draggedOrb.monsterTarget = (AbstractMonster) arrow.hoveredCreature;
                	draggedOrb = null;
                	arrow.isHidden = true;
                }
        	}
        }
    }

}