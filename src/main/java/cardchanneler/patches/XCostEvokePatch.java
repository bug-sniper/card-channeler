package cardchanneler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class XCostEvokePatch {	
	public static Integer DEFAULT_COST = -999;
	public static int DEFAULT_ENERGY_VALUE;
	public static int oldEnergyValue;
	
	@SpirePatch(
	        clz=AbstractCard.class,
	        method=SpirePatch.CLASS
	)
	public static class CostAtChannelField
	{
	    public static SpireField<Integer> costAtChannel = new SpireField<>(() -> DEFAULT_COST);
	}
}
