package cardchanneler.orbs;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.combat.*;

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
    
    public ChanneledCard(AbstractCard card) {
    	super();
        ID = ORB_ID;
        name = orbString.NAME;
        
        this.card = card;
        updateDescription();
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
			Field field = card.getClass().getField(key);

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

    // Set the on-hover description of the orb
    @Override
    public void updateDescription() { 
    	description = orbString.DESCRIPTION[0];
    	card.initializeDescription();
        String descriptionFragment = "";
        for (int i=0; i<card.description.size(); i++){
        	descriptionFragment += card.description.get(i).text;
            for (String tmp : descriptionFragment.split(" ")) {
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
    	beingEvoked = true;
    	AbstractMonster monster = AbstractDungeon.getRandomMonster();
    	card.calculateCardDamage(monster);
    	card.use(AbstractDungeon.player, monster);
    	AbstractDungeon.actionManager.addToTop(new UseCardAction(card, monster));
    }

    @Override
    public void onStartOfTurn() {
    	//No passive effect
    }

    @Override
    public void updateAnimation() {
        super.updateAnimation();
        angle += Gdx.graphics.getDeltaTime() * 45.0f;
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
        //InputHelper.getCardSelectedByHotkey();
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
