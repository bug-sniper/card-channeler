package helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import cardchanneler.orbs.ChanneledCard;

public class DottedArrowFromOrb implements RenderSubscriber{
	public boolean isHidden = true;
	private Vector2 start;
	private Vector2 controlPoint;
	AbstractCreature hoveredCreature = null;
	private float arrowScale;
	private float arrowScaleTimer;
	private Vector2[] points = new Vector2[20];
	
	public DottedArrowFromOrb(){
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = new Vector2();
        }
        BaseMod.subscribe(this);
	}
	
	public void setOrb(ChanneledCard orb){
		start = new Vector2(orb.cX, orb.cY);
	}
	
    public void render(SpriteBatch sb)
    {
        if (!isHidden) {
            renderTargetingUi(sb);
            if (hoveredCreature != null) {
                hoveredCreature.renderReticle(sb);
            }
        }
    }

    public void renderTargetingUi(SpriteBatch sb)
    {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        if (this.hoveredCreature == null) {
            this.arrowScale = Settings.scale;
            this.arrowScaleTimer = 0.0F;
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            this.arrowScaleTimer += com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (this.arrowScaleTimer > 1.0F) {
                this.arrowScaleTimer = 1.0F;
            }

            this.arrowScale = com.badlogic.gdx.math.Interpolation.elasticOut.apply(Settings.scale, Settings.scale * 1.2F, this.arrowScaleTimer);
            sb.setColor(new Color(1.0F, 0.2F, 0.3F, 1.0F));
        }
        Vector2 control = new Vector2(start.x, start.y+400f);
        controlPoint = new Vector2(start.x+(control.x-start.x), start.y+(control.y-start.y));
        Vector2 tmp = new Vector2(control.x - x, control.y - y);
        tmp.nor();

        drawCurvedLine(sb, start, new Vector2(x, y), control);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128.0F, y - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, this.arrowScale, this.arrowScale, tmp.angle() + 90.0F, 0, 0, 256, 256, false, false);

    }

    private void drawCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control)
    {
        float radius = 7.0F * Settings.scale;

        for (int i = 0; i < this.points.length - 1; i++) {
            this.points[i] = ((Vector2) com.badlogic.gdx.math.Bezier.quadratic(this.points[i], i / 20.0F, start, control, end, new Vector2()));
            radius += 0.4F * Settings.scale;

            float angle;

            if (i != 0) {
                Vector2 tmp = new Vector2(this.points[(i - 1)].x - this.points[i].x, this.points[(i - 1)].y - this.points[i].y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                Vector2 tmp = new Vector2(this.controlPoint.x - this.points[i].x, this.controlPoint.y - this.points[i].y);
                angle = tmp.nor().angle() + 270.0F;
            }

            sb.draw(ImageMaster.TARGET_UI_CIRCLE, this.points[i].x - 64.0F, this.points[i].y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle, 0, 0, 128, 128, false, false);
        }
    }

	@Override
	public void receiveRender(SpriteBatch sb) {
		render(sb);
	}
}
