
package cardchanneler;

import basemod.BaseMod;
import basemod.DevConsole;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostDungeonUpdateSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import cardchanneler.orbs.ChanneledCard;
import cardchanneler.relics.CardChannelerRelic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.GameActionManager.Phase;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class CardChannelerMod implements PostDungeonInitializeSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostUpdateSubscriber, PostDungeonUpdateSubscriber {
    private static final Logger logger = LogManager.getLogger(CardChannelerMod.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new CardChannelerMod());
    }

    @Override
    public void receiveEditStrings() {
    	logger.info("Editing strings");
        final String relicStrings = Gdx.files.internal("localization/RelicStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        final String orbStrings = Gdx.files.internal("localization/OrbStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(OrbStrings.class, orbStrings);
        final String uiStrings = Gdx.files.internal("localization/UIStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        logger.info("Done editing strings");
    }

    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");
        BaseMod.addRelic(new CardChannelerRelic(), RelicType.SHARED);
        logger.info("Done adding relics");
    }

    @Override
    public void receivePostDungeonInitialize() {
        RelicLibrary.getRelic(CardChannelerRelic.ID).makeCopy().instantObtain();
    }

	@Override
	public void receivePostUpdate() {
		if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(CardChannelerRelic.ID)) {
			if (Gdx.input.isKeyJustPressed(Keys.C) && !DevConsole.visible) {
				((CardChannelerRelic) AbstractDungeon.player.getRelic(CardChannelerRelic.ID)).invoke();
			}
		}
	}

	@Override
	public void receivePostDungeonUpdate() {
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
            if (((AbstractOrb)AbstractDungeon.player.orbs.get(i)).ID == ChanneledCard.ORB_ID){
            	ChanneledCard orb = (ChanneledCard) AbstractDungeon.player.orbs.get(i);
            	orb.card.applyPowers();
            	orb.updateDescription();
            }
        }
        if (AbstractDungeon.actionManager.phase == Phase.WAITING_ON_USER){
        	ChanneledCard.beingEvoked = false;
        }
	}
}
