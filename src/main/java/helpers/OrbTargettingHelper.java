package helpers;

import java.util.Collections;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import cardchanneler.orbs.ChanneledCard;

public class OrbTargettingHelper {
    static ChanneledCard draggedOrb = null;
    //static TargettingArrowFromOrb arrow = null;

    public static void update() {
    	
    	if (AbstractDungeon.player == null){
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
        
        // update drag
        if (draggedOrb != null) {
        	System.out.println("Dragging org: " + draggedOrb.card.name);
        	//arrow = new TargettingArrowFromOrb(orb);
        	if (InputHelper.justReleasedClickLeft) {
        		//arrow.onRelease();
                AbstractMonster hoveredMonster = null;
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if ((m.hb.hovered) && (!m.isDying)) {
                        hoveredMonster = m;
                        break;
                    }
                }
                if (hoveredMonster != null){
                	draggedOrb.monsterTarget = hoveredMonster;
                	draggedOrb = null;
                }
        	}
        }
    }

}