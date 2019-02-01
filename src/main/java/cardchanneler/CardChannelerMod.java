
package cardchanneler;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import cardchanneler.relics.CardChannelerRelic;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.RelicStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class CardChannelerMod implements PostDungeonInitializeSubscriber, EditRelicsSubscriber, EditStringsSubscriber {
    private static final Logger logger = LogManager.getLogger(CardChannelerMod.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new CardChannelerMod());
    }

    public void receiveEditStrings() {
        final String relicStrings = Gdx.files.internal("localization/RelicStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
    }

    public void receiveEditRelics() {
        logger.info("Adding relics");
        BaseMod.addRelic(new CardChannelerRelic(), RelicType.SHARED);
        logger.info("Done adding relics");
    }

    public void receivePostDungeonInitialize() {
        RelicLibrary.getRelic(CardChannelerRelic.ID).makeCopy().instantObtain();
    }
}
