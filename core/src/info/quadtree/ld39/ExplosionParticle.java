package info.quadtree.ld39;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class ExplosionParticle extends VisualEffect {

	static Sprite[] sprites = null;

	public float drag = .4f;

	float phase = 0;
	Vector2 pos;

	Vector2 vel;

	public ExplosionParticle(float phase, Vector2 pos, Vector2 vel) {
		super();
		this.phase = phase;
		this.pos = pos.cpy();
		this.vel = vel.cpy();
	}

	@Override
	public void render() {
		super.render();

		if (sprites == null) {
			sprites = new Sprite[4];
			for (int i = 0; i < 4; ++i) {
				sprites[i] = LD39.s.atlas.createSprite("exp" + (i + 1));
			}
		}

		Sprite sp = sprites[Math.min((int) (phase * 4), 3)];
		if (phase < 1.5) {
			sp.setColor(Color.WHITE);
		} else {
			sp.setColor(1, 1, 1, (float) (1 - ((phase - 1.5) * 2)));
		}

		sp.setPosition(pos.x - sp.getWidth() / 2, pos.y - sp.getHeight() / 2);
		sp.setScale(0.4f);
		sp.draw(LD39.s.batch);
	}

	@Override
	public void update() {
		super.update();

		phase += 1.f / 15.f;

		if (phase >= 0.99)
			keep = false;

		pos.add(vel);
		vel.scl(drag);
	}

}
