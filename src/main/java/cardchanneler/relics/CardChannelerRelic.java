package cardchanneler.relics;

import basemod.abstracts.CustomRelic;
import cardchanneler.orbs.ChanneledCard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CardChannelerRelic  extends CustomRelic implements ClickableRelic {
	private static final Logger logger = LogManager.getLogger(CardChannelerRelic.class.getName());
	public static final String ID = "CardChanneler:CardChanneler";
	private static final String IMG = "relics/CardChanneler.png";
    
    public CardChannelerRelic() {
        super(ID, new Texture(IMG), RelicTier.SPECIAL, LandingSound.FLAT);
    }
    
    @Override
    public void onRightClick() {
    	DamageType x = DamageInfo.DamageType.THORNS;
    	boolean outsideCombat = AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT;
    			if (outsideCombat){
    				logger.info("You cannot use this relic outside combat");
    			}
        final AbstractOrb orb = new ChanneledCard(new Strike_Blue());
        AbstractDungeon.actionManager.addToBottom(new ChannelAction(orb));
    }
    
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
