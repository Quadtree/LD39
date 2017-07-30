package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Lightning extends VisualEffect {

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

		LD39.s.shapeRnd.begin(ShapeType.Line);
		LD39.s.shapeRnd.setColor(0.3f, 0.3f, 1.f, fade);
		LD39.s.shapeRnd.polyline(vertArr);
		LD39.s.shapeRnd.end();
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
