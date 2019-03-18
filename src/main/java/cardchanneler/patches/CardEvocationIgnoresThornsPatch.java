package cardchanneler.patches;    

 import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;    
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;    
import com.megacrit.cardcrawl.cards.DamageInfo;    
import com.megacrit.cardcrawl.powers.ThornsPower;    

 import cardchanneler.orbs.ChanneledCard;    

 @SpirePatch(    
        clz=ThornsPower.class,    
        method="onAttacked"    
)    
public class CardEvocationIgnoresThornsPatch    
{    
    public static SpireReturn<Integer> Prefix(ThornsPower __instance, DamageInfo damage, int damageAmount) {    
        if (ChanneledCard.beingEvoked){    
            return SpireReturn.Return(damageAmount);    
        }else{    
            return SpireReturn.Continue();    
        }    
    }    
} 