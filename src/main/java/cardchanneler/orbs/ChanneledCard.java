package cardchanneler.orbs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.combat.*;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import cardchanneler.helpers.OrbTargettingStraightArrow;
import cardchanneler.vfx.ChanneledCardPassiveEffect;

public class ChanneledCard extends AbstractOrb {
	private static final Logger logger = LogManager.getLogger(ChanneledCard.class.getName());
	
    // Standard ID/Description
    public static final String ORB_ID = "CardChanneler:ChanneledCard";
    private static final OrbStrings orbString = CardCrawlGame.languagePack.getOrbString(ORB_ID);

    // Animation Rendering Numbers - You can leave these at default, or play around with them and see what they change.
	public static final float scale = 0.2f;
    private float vfxTimer = 1.0f;
    private float vfxIntervalMin = 0.1f;
    private float vfxIntervalMax = 0.4f;
    
    public AbstractCard card = null;
    public static boolean beingEvoked = false;
    public static boolean orbBeingLost = false;
    public AbstractMonster monsterTarget;
    
    public ChanneledCard(AbstractCard card) {
    	super();
        ID = ORB_ID;
        this.card = card;
        monsterTarget = AbstractDungeon.getRandomMonster();
        name = orbString.NAME + " " + card.name;
        updateDescription();
    }
    
	private String getDynamicValue(final String key) {
		String value = null;
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
			DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
			if (dv != null) {
				if (dv.isModified(card)) {
					if (dv.value(card) >= dv.baseValue(card)) {
						value = "[#" + dv.getIncreasedValueColor().toString() + "]" + Integer.toString(dv.value(card)) + "[]";
					} else {
						value = "[#" + dv.getDecreasedValueColor().toString() + "]" + Integer.toString(dv.value(card)) + "[]";
					}
				} else {
					value = Integer.toString(dv.baseValue(card));
				}
			}
			logger.info(key + " is " + value);
			return (String) value;
        }
    }

    // Set the on-hover description of the orb
    @Override
    public void updateDescription() { 
    	description = orbString.DESCRIPTION[0];
    	boolean firstWord = false;
    	card.initializeDescription();
        String descriptionFragment = "";
        for (int i=0; i<card.description.size(); i++){
        	descriptionFragment = card.description.get(i).text;
            for (String word : descriptionFragment.split(" ")) {
            	if (firstWord){
            		firstWord = false;
            	}else{
            		description += " ";
            	}
                if (word.length() > 0 && word.charAt(0) == '*') {
                    word = word.substring(1);
                    String punctuation = "";
                    if (word.length() > 1 && !Character.isLetter(word.charAt(word.length() - 2))) {
                        punctuation += word.charAt(word.length() - 2);
                        word = word.substring(0, word.length() - 2);
                        punctuation += ' ';
                    }
                    description += word;
                    description += punctuation;
                }
                else if (word.length() > 0 && word.charAt(0) == '!') {
                	String key = "";
                	for (int j=1; j<word.length(); j++){
                		if (word.charAt(j) == '!'){
                			description += getDynamicValue(key);
                			description += word.substring(j+1);
                		}
                		else {
                		key += word.charAt(j);
                		}
                	}
                }
                else{
                	description += word;
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
    	beingEvoked = true;
    	card.calculateCardDamage(monsterTarget);
    	card.use(AbstractDungeon.player, monsterTarget);
    	AbstractDungeon.actionManager.addToTop(new UseCardAction(card, monsterTarget));
    }

    @Override
    public void onStartOfTurn() {
    	//No passive effect
    }

    @Override
    public void updateAnimation() {
        super.updateAnimation();
        vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0f) {
            AbstractDungeon.effectList.add(new ChanneledCardPassiveEffect(this.cX, this.cY));
            this.vfxTimer = MathUtils.random(this.vfxIntervalMin, this.vfxIntervalMax);
        }
    }

    //Related to the Disciple mod's switch card tip rendering:
    //https://github.com/Tempus/The-Disciple/blob/master/src/main/java/cards/switchCards/AbstractSelfSwitchCard.java
    @Override
    public void render(SpriteBatch sb) {
	    card.current_x = cX;
	    card.current_y = cY;
	    card.drawScale = scale;
    	card.render(sb);
        hb.render(sb);
        if (card.target == CardTarget.ENEMY ||
        	card.target == CardTarget.SELF_AND_ENEMY){
	        sb.end();
	        OrbTargettingStraightArrow.drawArrow(this, monsterTarget);
	        sb.begin();
        }
    }

    @Override
    public void triggerEvokeAnimation() {
    	AbstractDungeon.effectsQueue.add(new DarkOrbActivateEffect(this.cX, this.cY));
    }

    @Override
    public void playChannelSFX() {
    	//Just use the card's SFX
    }

    @Override
    public AbstractOrb makeCopy() {
        return new ChanneledCard(this.card);
    }
}
