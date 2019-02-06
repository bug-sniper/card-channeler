package cardchanneler.orbs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.combat.*;

import cardchanneler.CardChannelerMod;

public class ChanneledCard extends AbstractOrb {
	private static final Logger logger = LogManager.getLogger(ChanneledCard.class.getName());

    // Standard ID/Description
    public static final String ORB_ID = "CardChanneler:ChanneledCard";
    private static final OrbStrings orbString = CardCrawlGame.languagePack.getOrbString(ORB_ID);
    public static final String[] DESC = orbString.DESCRIPTION;

    // Animation Rendering Numbers - You can leave these at default, or play around with them and see what they change.
    private float vfxTimer = 1.0f;
    private float vfxIntervalMin = 0.1f;
    private float vfxIntervalMax = 0.4f;
    private static final float ORB_WAVY_DIST = 0.04f;
    private static final float PI_4 = 12.566371f;
    
    private AbstractCard card = null;
    
    public ChanneledCard(AbstractCard card) {

        ID = ORB_ID;
        name = orbString.NAME;
        img = ImageMaster.loadImage("orbs/default_orb.png");
        
        this.card = card;

        evokeAmount = this.baseEvokeAmount = 1;
        passiveAmount = this.basePassiveAmount = 3;

        this.updateDescription();

        angle = MathUtils.random(360.0f); // More Animation-related Numbers
        channelAnimTimer = 0.5f;
    }
    
//    /**
//     * 
//     * @param key The string between !s that is used to refer to a variable
//     * @return The number that the key represents in a card 
//     */
//    private int readDynamicVariable(final char key){
//        final StringBuilder stringBuilder = new StringBuilder();
//        int num = 0;
//        switch (key) {
//            case 'D': {
//                if (!card.isDamageModified) {
//                    num = card.baseDamage;
//                    break;
//                }
//                num = card.damage;
//                break;
//            }
//            case 'B': {
//                if (!card.isBlockModified) {
//                    num = card.baseBlock;
//                    break;
//                }
//                num = card.block;
//                break;
//            }
//            case 'M': {
//                if (!card.isMagicNumberModified) {
//                    num = card.baseMagicNumber;
//                    break;
//                }
//                num = card.magicNumber;
//                break;
//            }
//        }
//        return num;
//    }
    
    private String getDynamicValue(final char key) {
        switch (key) {
            case 'B': {
                if (!card.isBlockModified) {
                    return Integer.toString(card.baseBlock);
                }
                if (card.block >= card.baseBlock) {
                    return "[#7fff00]" + Integer.toString(card.block) + "[]";
                }
                return "[#ff6563]" + Integer.toString(card.block) + "[]";
            }
            case 'D': {
                if (!card.isDamageModified) {
                    return Integer.toString(card.baseDamage);
                }
                if (card.damage >= card.baseDamage) {
                    return "[#7fff00]" + Integer.toString(card.damage) + "[]";
                }
                return "[#ff6563]" + Integer.toString(card.damage) + "[]";
            }
            case 'M': {
                if (!card.isMagicNumberModified) {
                    return Integer.toString(card.baseMagicNumber);
                }
                if (card.magicNumber >= card.baseMagicNumber) {
                    return "[#7fff00]" + Integer.toString(card.magicNumber) + "[]";
                }
                return "[#ff6563]" + Integer.toString(card.magicNumber) + "[]";
            }
            default: {
                ChanneledCard.logger.info("KEY: " + key);
                return Integer.toString(-99);
            }
        }
    }

    @Override
    public void updateDescription() { // Set the on-hover description of the orb
    	card.initializeDescription();
        String rawDescription = "";
        for (int i=0; i<card.description.size(); i++){
        	rawDescription += card.description.get(i).text;
        	description = "";
            for (String tmp : rawDescription.split(" ")) {
                tmp += ' ';
                if (tmp.length() > 0 && tmp.charAt(0) == '*') {
                    tmp = tmp.substring(1);
                    String punctuation = "";
                    if (tmp.length() > 1 && !Character.isLetter(tmp.charAt(tmp.length() - 2))) {
                        punctuation += tmp.charAt(tmp.length() - 2);
                        tmp = tmp.substring(0, tmp.length() - 2);
                        punctuation += ' ';
                    }
                    description += tmp;
                    description += punctuation;
                }
                else if (tmp.length() > 0 && tmp.charAt(0) == '!') {
                    if (tmp.length() == 4) {
                        description += getDynamicValue(tmp.charAt(1));
                    }
                    else if (tmp.length() == 5) {
                        description += getDynamicValue(tmp.charAt(1));
                        description += tmp.charAt(3);
                    }
                    description += ' ';
                }
                else{
                	description += tmp;
                }
            }
        }
    }

    @Override
    public void applyFocus() {
        //Not affected by focus
    }

    @Override
    public void onEvoke() { // 1.On Orb Evoke

        AbstractDungeon.actionManager.addToBottom( // 2.Damage all enemies
                new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(this.evokeAmount, true, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
       // The damage matrix is how orb damage all enemies actions have to be assigned. For regular cards that do damage to everyone, check out cleave or whirlwind - they are a bit simpler.


        AbstractDungeon.actionManager.addToBottom(new SFXAction("TINGSHA")); // 3.And play a Jingle Sound.
        // For a list of sound effects you can use, look under com.megacrit.cardcrawl.audio.SoundMaster - you can see the list of keys you can use there. As far as previewing what they sound like, open desktop-1.0.jar with something like 7-Zip and go to audio. Reference the file names provided. (Thanks fiiiiilth)

    }

    @Override
    public void onStartOfTurn() {
    	//No passive effect
    }

    @Override
    public void updateAnimation() {// You can totally leave this as is.
        // If you want to create a whole new orb effect - take a look at conspire's Water Orb. It includes a custom sound, too!
        super.updateAnimation();
        angle += Gdx.graphics.getDeltaTime() * 45.0f;
        vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0f) {
            AbstractDungeon.effectList.add(new DarkOrbPassiveEffect(this.cX, this.cY)); // This is the purple-sparkles in the orb. You can change this to whatever fits your orb.
            this.vfxTimer = MathUtils.random(this.vfxIntervalMin, this.vfxIntervalMax);
        }
    }

    // Render the orb.
    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.c.a / 2.0f));
        sb.draw(this.img, this.cX - 48.0f, this.cY - 48.0f + this.bobEffect.y, 48.0f, 48.0f, 96.0f, 96.0f, this.scale + MathUtils.sin(this.angle / PI_4) * ORB_WAVY_DIST * Settings.scale, this.scale, this.angle, 0, 0, 96, 96, false, false);
        sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.c.a / 2.0f));
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.cX - 48.0f, this.cY - 48.0f + this.bobEffect.y, 48.0f, 48.0f, 96.0f, 96.0f, this.scale, this.scale + MathUtils.sin(this.angle / PI_4) * ORB_WAVY_DIST * Settings.scale, -this.angle, 0, 0, 96, 96, false, false);
        sb.setBlendFunction(770, 771);
        renderText(sb);
        hb.render(sb);
    }


    @Override
    public void triggerEvokeAnimation() { // The evoke animation of this orb is the dark-orb activation effect.
    	AbstractDungeon.effectsQueue.add(new DarkOrbActivateEffect(this.cX, this.cY));
    }

    @Override
    public void playChannelSFX() { // When you channel this orb, the ATTACK_FIRE effect plays ("Fwoom").
        CardCrawlGame.sound.play("ATTACK_FIRE", 0.1f);
    }

    @Override
    public AbstractOrb makeCopy() {
        return new ChanneledCard(this.card);
    }
}
