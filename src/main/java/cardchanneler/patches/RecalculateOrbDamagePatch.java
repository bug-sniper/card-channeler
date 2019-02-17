package cardchanneler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import cardchanneler.orbs.ChanneledCard;

@SpirePatch(
        clz=ApplyPowerAction.class,
        method="update"
)
public class RecalculateOrbDamagePatch {
	public static void Postfix(ApplyPowerAction __instance){
        for (int i = 1; i < AbstractDungeon.player.orbs.size(); ++i) {
            if (((AbstractOrb)AbstractDungeon.player.orbs.get(i)).ID == ChanneledCard.ORB_ID){
            	ChanneledCard orb = (ChanneledCard) AbstractDungeon.player.orbs.get(i);
            	System.out.println("applying powers");
            	orb.card.applyPowers();
            	orb.updateDescription();
            }
        }
	}
}