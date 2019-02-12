package cardchanneler.orbs;

import java.lang.reflect.Field;

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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
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
    
    private String getDynamicValue(final String key) {
    	if (key.length() == 1){
	        switch (key.charAt(0)) {
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
	    else {
			Object value = null;
			try {
			Field field = card.getClass().getField("someField");

				value = field.get(card);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return (String) value;
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
                	String key = "";
                	for (int j=1; j<tmp.length(); j++){
                		if (tmp.charAt(j) == '!'){
                			description += getDynamicValue(key);
                			description += tmp.substring(j+1);
                			description += ' ';
                		}
                		else {
                		key += tmp.charAt(j);
                		}
                	}
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
    public void onEvoke() {
    	//The dontTriggerOnUseCard is to prevent interactions with
    	//relics, powers, and cards that happen when you to play a card
    	AbstractMonster monster = AbstractDungeon.getRandomMonster();
    	card.calculateCardDamage(monster);
    	boolean oldDontTriggerValue = card.dontTriggerOnUseCard; 
    	card.dontTriggerOnUseCard = true;
        card.use(AbstractDungeon.player, AbstractDungeon.getRandomMonster());
        card.dontTriggerOnUseCard = oldDontTriggerValue;
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
