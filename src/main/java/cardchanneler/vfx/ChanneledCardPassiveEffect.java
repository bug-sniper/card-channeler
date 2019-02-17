package cardchanneler.vfx;

import com.megacrit.cardcrawl.vfx.*;

import cardchanneler.orbs.ChanneledCard;

import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

public class ChanneledCardPassiveEffect extends AbstractGameEffect
{
    private float effectDuration;
    private float x;
    private float y;
    private float vY;
    private float vX;
    private float alpha;
    private float orbWidth;
    private float orbHeight;
    private TextureAtlas.AtlasRegion img;
    
    public ChanneledCardPassiveEffect(final float x, final float y) {
        img = ImageMaster.ROOM_SHINE_1;
        effectDuration = MathUtils.random(0.4f, 0.8f);
        duration = effectDuration;
        startingDuration = effectDuration;
        orbWidth = ChanneledCard.scale*AbstractCard.IMG_WIDTH * 0.8f;
        float offsetX = MathUtils.random(-orbWidth/2, orbWidth/2);
        offsetX -= img.packedWidth / 2;
        this.x = x + offsetX;
        vX = MathUtils.random(-3.0f, 3.0f) * Settings.scale;
        orbHeight = ChanneledCard.scale*AbstractCard.IMG_HEIGHT * 0.7f;
        float offsetY = MathUtils.random(-orbHeight/2, orbHeight/2);
        offsetY -= img.packedHeight / 2;
        this.y = y + offsetY;
        vY = MathUtils.random(-3.0f, 3.0f) * Settings.scale;
        alpha = MathUtils.random(0.5f, 1.0f);
        color = new Color(MathUtils.random(0.6f, 0.9f), 1.0f, 1.0f, alpha);
        scale = MathUtils.random(0.5f, 1.2f) * Settings.scale;
    }
    
    @Override
    public void update() {
        if (vY != 0.0f) {
            y += vY * Gdx.graphics.getDeltaTime();
        }
        if (vX != 0.0f) {
            x += vX * Gdx.graphics.getDeltaTime();
        }
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0f) {
            isDone = true;
        }
        else if (duration < effectDuration / 2.0f) {
            color.a = Interpolation.exp5In.apply(0.0f, alpha, duration / (effectDuration / 2.0f));
        }
    }
    
    @Override
    public void render(final SpriteBatch sb) {
        sb.setColor(color);
        sb.setBlendFunction(770, 1);
        sb.draw(img, x, y, img.packedWidth / 2.0f, img.packedHeight / 2.0f, img.packedWidth, img.packedHeight, scale * MathUtils.random(0.75f, 1.25f), scale * MathUtils.random(0.75f, 1.25f), rotation);
        sb.setBlendFunction(770, 771);
    }
    
    @Override
    public void dispose() {
    }
}

