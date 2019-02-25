package cardchanneler.patches;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.actions.defect.RedoAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import cardchanneler.orbs.ChanneledCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//This patch makes channeled cards go to your discard pile if they are removed or evoked.
//We also reset some variables when we're done evoking cards.

public class ChanneledCardDiscardPatch {
	public static final Logger logger = LogManager.getLogger(ChanneledCardDiscardPatch.class.getName());
	
	@SpirePatch(
	        clz=AbstractCard.class,
	        method=SpirePatch.CLASS
	)
	public static class BeingRetainedAsOrbField
	{
	    public static SpireField<Boolean> beingRetainedAsOrb = new SpireField<>(() -> false);
	}
	
	@SpirePatch(
	        clz=AbstractPlayer.class,
	        method="evokeWithoutLosingOrb"
	)
	public static class SetOrbBeingLostValue {
		
	    public static void Prefix(AbstractPlayer __instance)
	    {
	        AbstractOrb orb = (AbstractOrb)__instance.orbs.get(0);
	        if (orb.ID == ChanneledCard.ORB_ID){
	        	AbstractCard card = ((ChanneledCard)orb).card;
	        	BeingRetainedAsOrbField.beingRetainedAsOrb.set(card, true);
	        }
	    }
	}
	
	@SpirePatch(
	        clz=RedoAction.class,
	        method="update"
	)
	public static class RecursionPreservesOrb {
		
	    public static void Prefix(RedoAction __instance)
	    {
	        AbstractOrb orb = AbstractDungeon.player.orbs.get(0);
	        if (orb.ID == ChanneledCard.ORB_ID){
	        	AbstractCard card = ((ChanneledCard)orb).card;
	        	BeingRetainedAsOrbField.beingRetainedAsOrb.set(card, true);
	        }
	    }
	}
	
    @SpirePatch(
            clz=UseCardAction.class,
            method="update"
    )
    public static class DecideWhetherToDiscard {
    	
	    public static void Postfix(UseCardAction __instance)
	    {   
	    	try{
	    		//targetCard is private but needs to be accessed
		        Field f = __instance.getClass().getDeclaredField("targetCard");
		        f.setAccessible(true);
		        AbstractCard card = null;
		        card = (AbstractCard) f.get(__instance);
				
		        BeingRetainedAsOrbField.beingRetainedAsOrb.set(
		        		card, false);
		        
	    	} catch (IllegalAccessException e){
	    		e.printStackTrace();
	    	} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
	    }
    	
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("moveToDeck") ||
                    	m.getMethodName().equals("moveToDiscardPile")) {
                        m.replace("if(!((Boolean)" +
                    	BeingRetainedAsOrbField.class.getName() + 
                    	".beingRetainedAsOrb.get(targetCard)).booleanValue())" +
                    	"{$_ = $proceed($$);}");
                    }
                }
            };
        }
    }
}
