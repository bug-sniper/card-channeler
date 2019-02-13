package cardchanneler.relics;

import basemod.abstracts.CustomRelic;
import cardchanneler.actions.ChannelCardAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CardChannelerRelic  extends CustomRelic implements ClickableRelic {
	private static final Logger logger = LogManager.getLogger(CardChannelerRelic.class.getName());
	public static final String ID = "CardChanneler:CardChanneler";
	private static final String IMG = "relics/CardChanneler.png";
    
    public CardChannelerRelic() {
        super(ID, new Texture(IMG), RelicTier.SPECIAL, LandingSound.FLAT);
    }
    
    public void invoke(){
    	boolean outsideCombat = AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT;
		if (outsideCombat){
			logger.info("You cannot use this relic outside combat");
			return;
		}
		AbstractDungeon.actionManager.addToBottom(new ChannelCardAction());
    }
    
    @Override
    public void onRightClick() {
    	invoke();
    }
    
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
