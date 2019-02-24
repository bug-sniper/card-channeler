package cardchanneler.patches;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import cardchanneler.orbs.ChanneledCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class XCostEvokePatch {	
	private static final Logger logger = LogManager.getLogger(XCostEvokePatch.class.getName());
	public static Integer DEFAULT_COST = -999;
	public static AbstractCard cardBeingEvoked = null;
	
	@SpirePatch(
	        clz=AbstractCard.class,
	        method=SpirePatch.CLASS
	)
	public static class CostAtChannelField
	{
	    public static SpireField<Integer> costAtChannel = new SpireField<>(() -> DEFAULT_COST);
	}
	
//The code below here didn't work with the patcher and may not be needed.	
//	@SpirePatch(
//	        clz=AbstractCard.class,
//	        method=SpirePatch.CLASS
//	)
//	public static class BeingEvokedField
//	{
//	    public static SpireField<Boolean> beingEvoked = new SpireField<>(() -> false);
//	}
//	
//    @SpirePatch(
//            clz=AbstractPlayer.class,
//            method="useCard"
//    )
//    public static class StashEnergy
//    {
//        public static ExprEditor Instrument()
//        {
//            return new ExprEditor() {
//                @Override
//                public void edit(MethodCall m) throws CannotCompileException
//                {
//                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
//                        m.replace(StashEnergy.class.getName() + ".use($0, $$, energyOnUse);");
//                    }
//                }
//            };
//        }
//        
//    	public static void use(AbstractCard c, AbstractPlayer player, AbstractMonster monster, int energyOnUse){
//    		logger.info("XCost use method invoked");
//    		if (BeingEvokedField.beingEvoked.get(c) && 
//    			c.cost == -1){
//    			assert cardBeingEvoked == null;
//    			int costAtChannel = CostAtChannelField.costAtChannel.get(c);
//    			oldEnergyValue = EnergyPanel.getCurrentEnergy();
//    			assert oldEnergyValue != DEFAULT_ENERGY_VALUE;
//    			logger.info("setting energy before using card to " + costAtChannel);
//    			EnergyPanel.setEnergy(costAtChannel);
//    			cardBeingEvoked = c;
//    			c.use(player, monster);
//    		} else{
//    			logger.info("Doing the normal card use");
//    			//do what the unmodded code would do
//    			c.use(player, monster);
//    		}
//    	}
//    }
//    
//    @SpirePatch(
//            clz=AbstractPlayer.class,
//            method="useCard"    
//    )
//    public static class ResetEnergy{
//	    public static void Postfix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse)
//	    {   
//	    	logger.info("XCost postfix method invoked");
//			if (oldEnergyValue != DEFAULT_ENERGY_VALUE){
//				EnergyPanel.setEnergy(oldEnergyValue);
//				oldEnergyValue = DEFAULT_ENERGY_VALUE;
//				CostAtChannelField.costAtChannel.set(cardBeingEvoked, DEFAULT_COST);
//				logger.info("resetting energy before after card to " + oldEnergyValue);
//				BeingEvokedField.beingEvoked.set(cardBeingEvoked, false);
//				cardBeingEvoked = null;
//			}    
//	    }
//    }
	
//	@SpirePatch(
//	    clz=AbstractPlayer.class,
//	    method="useCard"
//	)
//	public static class StashAwayEnergyWhileUsingCard {
//	    public static ExprEditor Instrument() {
//	        return new ExprEditor() {
//	        	public int counter = 0;
//	            @Override
//	            public void edit(MethodCall m) throws CannotCompileException {
//	            	String methodName = m.getMethodName();
//	                if (methodName.equals("use") &&
//	                	counter == 0) {
//	                	logger.info("Patching card use");
//	                    m.replace(XCostEvokePatch.class.getName() +
//	                    		".modifiedUseCardMethod($0, $1, $2);");
//	                }
//	                
//	                if (methodName.equals("use") &&
//		                	counter == 1) {
//	                		logger.info("Patching energy use");
//		                    m.replace(XCostEvokePatch.class.getName() +
//		                    		".modifiedUseEnergyMethod($0, $1);");
//		            }
//	                if (methodName.equals("use")){
//	                	counter += 1;
//	                }
//	            }
//	        };
//	    }
//	}
	
//	@SpirePatch(
//	        clz=AbstractPlayer.class,
//	        method="useCard"
//	)	
//	public static class temporarilySetEnergy {
//	    public static void Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse)
//	    {
//	    	if (c.cost == -1 && CostAtChannelField.costAtChannel.get(c) != DEFAULT_COST){
//	    		saveEnergy(EnergyPanel.getCurrentEnergy());
//	    		EnergyPanel.setEnergy(CostAtChannelField.costAtChannel.get(c));
//	    	}
//	    }
//	    public static void Postfix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse)
//	    {
//	    	if (c.cost == -1 && CostAtChannelField.costAtChannel.get(c) != DEFAULT_COST){
//	    		restoreEnergy();
//	    	}
//	    }
//	}
	
}
