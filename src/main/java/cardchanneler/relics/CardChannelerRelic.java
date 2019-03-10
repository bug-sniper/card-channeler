package cardchanneler.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;

public class CardChannelerRelic  extends CustomRelic {
	public static final String ID = "CardChanneler:CardChanneler";
	private static final String IMG = "relics/CardChanneler.png";
    
    public CardChannelerRelic() {
        super(ID, new Texture(IMG), RelicTier.SPECIAL, LandingSound.FLAT);
    }
    
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
