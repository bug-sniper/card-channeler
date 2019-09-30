package cardchanneler.patches;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.unique.TempestAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

//This class was used for printing the log while debugging
@SpirePatch(    
        clz=TempestAction.class,    
        method="update"    
)    
public class TraceTempestEffect {
    public static final boolean DEBUG = false;
    private static Logger logger = LogManager.getLogger(TraceEnergyChanges.class.getName());
    public static SpireReturn<?> Prefix(TempestAction __instance) {    
        if (!DEBUG){
            return SpireReturn.Continue();
        }
        //energyOnUse is private but needs to be seen
        Field f2 = null;
		try {
			f2 = TempestAction.class.getDeclaredField("energyOnUse");
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        f2.setAccessible(true);
        int energy = -1;
		try {
			energy = f2.getInt(__instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.info("tempest interacting with energy with " + energy);
        Thread.dumpStack();
        return SpireReturn.Continue();        
    }    
}
