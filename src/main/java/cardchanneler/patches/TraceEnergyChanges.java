package cardchanneler.patches;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

@SpirePatch(	
        clz=EnergyPanel.class,	
        method="setEnergy"	
)	
@SpirePatch(	
        clz=EnergyPanel.class,	
        method="addEnergy"	
)
@SpirePatch(	
        clz=EnergyPanel.class,	
        method="useEnergy"	
)
public class TraceEnergyChanges {
	public static final boolean DEBUG = false;
	private static Logger logger = LogManager.getLogger(TraceEnergyChanges.class.getName());
    public static SpireReturn Prefix(int e) {	
    	if (!DEBUG){
    		return SpireReturn.Continue();
    	}
        logger.info("interacting with energy with " + e);
        Thread.dumpStack();
    	return SpireReturn.Continue();		
    }	
}
