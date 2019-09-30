package cardchanneler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import cardchanneler.orbs.ChanneledCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
        clz=EnergyPanel.class,
        method="render"
)
public class EnergyPanelDisplayPatch {
	
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException
            {
                if (f.getFieldName().equals("totalCount")) {
                    f.replace("if (" + ChanneledCard.class.getName() + ".beingEvoked) "
                    		+ "{$_ = " + XCostEvokePatch.class.getName() + ".oldEnergyValue;}"
                    		+ "else"
                    		+ "{$_ = $proceed($$);}");
                }
            }
        };
    }
}
