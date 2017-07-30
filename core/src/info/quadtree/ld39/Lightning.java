package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Lightning extends VisualEffect {

	static Sprite sp;
	Vector2 end;

	public float endWidth = 2;

	public float fade = 1;
	Vector2 start;

	public float startWidth = 2;

	float[] vertArr = null;

	public Vector2 getEnd() {
		return new Vector2(end);
	}

	public Vector2 getStart() {
		return new Vector2(start);
	}

	@Override
	public void render() {
		super.render();

		if (sp == null)
			sp = LD39.s.atlas.createSprite("lightning1");

		if (vertArr == null) {
			Vector2 curPos = new Vector2(start);
			Vector2 trgPos = new Vector2(end);

			List<Float> verts = new ArrayList<Float>();

			while (curPos.dst2(trgPos) > 6 * 6) {
				verts.add(curPos.x);
				verts.add(curPos.y);

				float dx = trgPos.x - curPos.x;
				float dy = trgPos.y - curPos.y;
				float mag = trgPos.dst(curPos);
				dx /= mag;
				dy /= mag;

				curPos.add(dx * 4, dy * 4);
				curPos.add(MathUtils.random(-2, 2), MathUtils.random(-2, 2));
			}

			verts.add(trgPos.x);
			verts.add(trgPos.y);

			if (verts.size() < 4) {
				System.out.println(verts);
				return;
			}

			vertArr = new float[verts.size()];
			for (int i = 0; i < verts.size(); ++i)
				vertArr[i] = verts.get(i);
		}

		if (vertArr != null) {
			for (int i = 2; i < vertArr.length; i += 2) {

				float dist = Vector2.dst(vertArr[i - 2], vertArr[i - 1], vertArr[i], vertArr[i + 1]);
				float pct = (float) i / vertArr.length;
				float width = (pct * endWidth) + ((1 - pct) * startWidth);

				sp.setColor(1, 1, 1, fade);
				sp.setPosition((vertArr[i - 2] + vertArr[i]) / 2 - 16, (vertArr[i - 1] + vertArr[i + 1]) / 2 - 16);
				sp.setRotation(MathUtils.atan2(vertArr[i + 1] - vertArr[i - 1], vertArr[i] - vertArr[i - 2]) * 180 / MathUtils.PI);
				sp.setOrigin(16, 16);
				sp.setScale(dist / 16, width / 16 * 3);
				sp.draw(LD39.s.batch);

				// LD39.s.batch.draw(sp, vertArr[i - 2] + vertArr[i], vertArr[i
				// - 1] + vertArr[i + 1], 16, 16, 32, 32, dist / 16, width / 16
				// * 3,
				// MathUtils.atan2(vertArr[i + 1] - vertArr[i - 1], vertArr[i] -
				// vertArr[i - 2]) * 180 / MathUtils.PI);
			}
		}

		/*
		 * LD39.s.shapeRnd.begin(ShapeType.Line); LD39.s.shapeRnd.setColor(0.3f,
		 * 0.3f, 1.f, fade); LD39.s.shapeRnd.polyline(vertArr);
		 * LD39.s.shapeRnd.end();
		 */
	}

	public void setEnd(Vector2 end) {
		this.end = new Vector2(end);
	}

	public void setStart(Vector2 start) {
		this.start = new Vector2(start);
	}

	@Override
	public void update() {
		super.update();

		fade -= 1 / 30.f;

		if (fade < 0)
			keep = false;
	}

}
