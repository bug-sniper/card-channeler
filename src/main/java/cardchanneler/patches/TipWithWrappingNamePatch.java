package cardchanneler.patches;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.powers.ThornsPower;

import basemod.ReflectionHacks;
import cardchanneler.orbs.ChanneledCard;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class TipWithWrappingNamePatch {
	//Working with the value here instead of internally because
	//internally, a static final variable is involved, which is harder to
	//affect through patches
	public static float headerHeight;
	
    @SpirePatch(
            clz=TipHelper.class,
            method="renderTipBox"
    )
    public static class PushDownTipBoxBody {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontLeftTopAligned")) {
	                    m.replace(FontHelper.class.getName() + ".renderSmartText($1, " +
	                    	FontHelper.class.getName() +
	                    	".tipHeaderFont, $3, $4, $5, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, " +
	                    	Settings.class.getName() + ".GOLD_COLOR);"	                    
	                   );
                    } else if(m.getMethodName().equals("renderSmartText")) {
                    	m.replace(
                    	"$_ = $proceed($1, $2, $3, $4, y + BODY_OFFSET_Y - " +
                    	TipWithWrappingNamePatch.class.getName() + ".headerHeight, $6, $7, $8);System.out.println(\"got \" + cardchanneler.patches.TipWithWrappingNamePatch.headerHeight);");
                    } else if(m.getMethodName().equals("draw")){
                    	m.replace("if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_TOP)" +
                    		"{$_ = $proceed($$);}" +
                    		"else if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_BODY)" +
                    		"{$_ = $proceed($1, $2, $3 - " + TipWithWrappingNamePatch.class.getName() + ".headerHeight, $4, $5+" + TipWithWrappingNamePatch.class.getName() + ".headerHeight);}" +
                    		"else if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_BOT)" +
                    		"{$_ = $proceed($1, $2, $3 - " + TipWithWrappingNamePatch.class.getName() + ".headerHeight, $4, $5);}");
                    }
                }
            };
        }
    }
    
    @SpirePatch(
            clz=TipHelper.class,
            method="renderBox"
    )
    public static class PushDownPowerBoxBody {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontLeftTopAligned")) {
	                    m.replace(FontHelper.class.getName() + ".renderSmartText($1, " +
	                    FontHelper.class.getName() +
	                    ".tipHeaderFont, $3, $4, $5, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, " +
	                    Settings.class.getName() + ".GOLD_COLOR);"	                    
	                   );
                    } else if(m.getMethodName().equals("renderSmartText")) {
                    	m.replace(
                    	"$_ = $proceed($1, $2, $3, $4, y + BODY_OFFSET_Y - " +
                    	TipWithWrappingNamePatch.class.getName() + ".headerHeight, $6, $7, $8);System.out.println(\"got \" + cardchanneler.patches.TipWithWrappingNamePatch.headerHeight);");
                    } else if(m.getMethodName().equals("draw")){
                    	m.replace("if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_TOP)" +
                    		"{$_ = $proceed($$);}" +
                    		"else if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_BODY)" +
                    		"{$_ = $proceed($1, $2, $3 - " + TipWithWrappingNamePatch.class.getName() + ".headerHeight, $4, $5+" + TipWithWrappingNamePatch.class.getName() + ".headerHeight);}" +
                    		"else if ($1 == " + 
                    		ImageMaster.class.getName()+".KEYWORD_BOT)" +
                    		"{$_ = $proceed($1, $2, $3 - " + TipWithWrappingNamePatch.class.getName() + ".headerHeight, $4, $5);}");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz=TipHelper.class,
            method="render"
    )
    public static class AddHeaderHeightAtRender {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                	if (m.getMethodName().equals("getSmartHeight")) {
                		m.replace("$_ = $proceed($$); "+ 
	                    TipWithWrappingNamePatch.class.getName() + ".headerHeight = -" + 
	                    FontHelper.class.getName() + ".getSmartHeight(" +
	                    FontHelper.class.getName() + ".tipHeaderFont, HEADER, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING+"+Settings.class.getName()+".scale); " ); 
                	}
                }
            };
        }
    }
    
    @SpirePatch(
	  clz=TipHelper.class,
	  method="renderKeywords"
    )
    public static class AddHeaderHeightAtRenderKeyWords {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                	if (m.getMethodName().equals("getSmartHeight")) {
                		m.replace("$_ = $proceed($$); "+ 
	                    TipWithWrappingNamePatch.class.getName() + ".headerHeight = -" + 
	                    FontHelper.class.getName() + ".getSmartHeight(" +
	                    FontHelper.class.getName() + ".tipHeaderFont, s, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING+"+Settings.class.getName()+".scale); " ); 
                	}
                }
            };
        }
    }
    
    @SpirePatch(
	  clz=TipHelper.class,
	  method="renderPowerTips"
    )
    public static class AddHeaderHeightAtRenderKeyPowerTips {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                	if (m.getMethodName().equals("getSmartHeight")) {
                		m.replace("$_ = $proceed($$); "+ 
	                    TipWithWrappingNamePatch.class.getName() + ".headerHeight = -" + 
	                    FontHelper.class.getName() + ".getSmartHeight(" +
	                    FontHelper.class.getName() + ".tipHeaderFont, tip.header, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING+"+Settings.class.getName()+".scale); " ); 
                	}
                }
            };
        }
    }
}
