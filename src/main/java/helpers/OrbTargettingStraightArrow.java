package helpers;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class OrbTargettingStraightArrow {
	private static ShapeRenderer renderer = new ShapeRenderer();
	static final float BARB_DIVERGENCE = (float) (Math.PI/18f); //10 degrees
	static final float INACTIVE_ALPHA = 0.2f;
	static final float OPAQUE = 1f;
	static final float BASE_BARB_LENGTH = 200f;

	public static void drawArrow(AbstractOrb orb, AbstractMonster monster){
		float barbLength = BASE_BARB_LENGTH * Settings.scale;
		Vector2 start = new Vector2(orb.cX, orb.cY);
		Vector2 end = new Vector2(monster.hb.cX, monster.hb.cY);
		float lineWidth = 2*Settings.scale;
		float alpha = INACTIVE_ALPHA;
		if (orb.hb.hovered){
			alpha = OPAQUE;
		}
		Color color = new Color(1f, 1f, 1f, alpha);
		
		drawLine(start, end, lineWidth, color);
		float mainAngle = start.sub(end).angleRad();
		
		float lowerAngle = mainAngle - BARB_DIVERGENCE;
		Vector2 barbStartLowerAngle = new Vector2(barbLength * (float)MathUtils.cos(lowerAngle),
												  barbLength * (float)MathUtils.sin(lowerAngle));
		barbStartLowerAngle.add(end);
		
		float higherAngle = mainAngle + BARB_DIVERGENCE;
		Vector2 barbStartHigherAngle = new Vector2(barbLength * (float)MathUtils.cos(higherAngle),
												   barbLength * (float)MathUtils.sin(higherAngle));
		barbStartHigherAngle.add(end);
		
		
		drawLine(barbStartLowerAngle, end, lineWidth, color);
		drawLine(barbStartHigherAngle, end, lineWidth, color);
		
	}
	
    public static void drawLine(Vector2 start, Vector2 end, float lineWidth, Color color)
    {
    	Gdx.gl.glEnable(GL11.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(lineWidth);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(color);
        renderer.line(start, end);
        renderer.end();
        Gdx.gl.glLineWidth(1);
        Gdx.gl.glDisable(GL11.GL_BLEND);
    }
}
