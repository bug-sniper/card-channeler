package cardchanneler.relics;

import basemod.abstracts.CustomRelic;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Lightning;

public class CardChannelerRelic  extends CustomRelic implements ClickableRelic {
	public static final String ID = "Card Channeler";
	private static final String IMG = "relics/CardChanneler.png";
    
    public CardChannelerRelic() {
        super(ID, new Texture(IMG), RelicTier.SPECIAL, LandingSound.FLAT);
    }
    
    public void onRightClick() {
        final AbstractOrb orb = new Lightning();
        AbstractDungeon.actionManager.addToBottom(new ChannelAction(orb));
    }
}
