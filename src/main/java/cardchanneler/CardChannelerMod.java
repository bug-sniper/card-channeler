package cardchanneler;

import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostDungeonUpdateSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import cardchanneler.helpers.DottedArrowFromOrb;
import cardchanneler.helpers.OrbTargettingHelper;
import cardchanneler.orbs.ChanneledCard;
import cardchanneler.patches.XCostEvokePatch;
import cardchanneler.relics.CardChannelerRelic;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.GameActionManager.Phase;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class CardChannelerMod implements PostDungeonInitializeSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostUpdateSubscriber, PostDungeonUpdateSubscriber {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CardChannelerMod.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new CardChannelerMod());
        OrbTargettingHelper.setArrow(new DottedArrowFromOrb());
    }

    @Override
    public void receiveEditStrings() {
        logger.info("Editing strings");
        final String relicStrings = Gdx.files.internal("localization/RelicStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        final String orbStrings = Gdx.files.internal("localization/OrbStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(OrbStrings.class, orbStrings);
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
    }

    @Override
    public void receivePostDungeonUpdate() {
        
        if (AbstractDungeon.screen != CurrentScreen.NONE){
            return;
        }
        if (AbstractDungeon.currMapNode.room.phase != RoomPhase.COMBAT){
            return;
        }
        //If the above return conditions don't take effect, we are now
        //updating from within the combat phase.
        for (int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
            if (((AbstractOrb)AbstractDungeon.player.orbs.get(i)).ID == ChanneledCard.ORB_ID){
                ChanneledCard orb = (ChanneledCard) AbstractDungeon.player.orbs.get(i);
                orb.card.setAngle(0, true);
                orb.card.applyPowers();
                orb.updateDescription();
                if (orb.monsterTarget != null && orb.monsterTarget.isDeadOrEscaped()){
                    orb.monsterTarget = AbstractDungeon.getRandomMonster();
                }
            }
        }
        if (AbstractDungeon.actionManager.phase == Phase.WAITING_ON_USER){
            OrbTargettingHelper.update();
            if (XCostEvokePatch.oldEnergyValue != XCostEvokePatch.DEFAULT_ENERGY_VALUE) {
            	logger.info("Resetting energy to " + XCostEvokePatch.oldEnergyValue);
            	EnergyPanel.setEnergy(XCostEvokePatch.oldEnergyValue);
            	XCostEvokePatch.oldEnergyValue = XCostEvokePatch.DEFAULT_ENERGY_VALUE;
            }
        }
    }
}