package cardchanneler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import cardchanneler.orbs.ChanneledCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//This patch makes channeled cards go to your discard pile if they are removed or evoked

public class ChanneledCardDiscardPatch {
	
	@SpirePatch(
	        clz=AbstractPlayer.class,
	        method="removeNextOrb"
	)
	@SpirePatch(
	        clz=AbstractPlayer.class,
	        method="evokeOrb"
	)
	public static class SetStaticValue {
		
	    public static void Prefix(AbstractPlayer __instance)
	    {
	        AbstractOrb orb = (AbstractOrb)__instance.orbs.get(0);
	        if (orb.ID == ChanneledCard.ORB_ID){
	        	ChanneledCard.orbBeingLost = true;
	        }
	    }
	}
	
    @SpirePatch(
            clz=UseCardAction.class,
            method="update"
    )
    public static class DecideWhetherToDiscard {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("moveToDeck") ||
                    	m.getMethodName().equals("moveToDiscardPile")) {
                        m.replace("if(" + ChanneledCard.class.getName() + 
                        		".orbBeingLost){$_ = $proceed($$);}");
                    }
                }
            };
        }
    }
}
