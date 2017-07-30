package info.quadtree.ld39;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Thundercloud {
	static Sprite sprite;

	Vector2 pos;

	public Thundercloud() {
		pos = new Vector2(MathUtils.random(-2000, -600), MathUtils.random(-100, Gdx.graphics.getHeight() + 100));
	}

	public boolean keep() {
		return pos.x < 1600;
	}

	public void render() {
		if (sprite == null) {
			sprite = LD39.s.atlas.createSprite("thundercloud");
		}

		sprite.setColor(1, 1, 1, 0.25f);
		sprite.setPosition(pos.x - 128, pos.y - 128);
		sprite.draw(LD39.s.batch);
	}

	public void update() {
		pos.x += 3;

		if (MathUtils.random(60 * 5) == 0) {
			TilePos target = TilePos.create((int) ((pos.x + MathUtils.random(-64, 64)) / 16), (int) ((pos.y + MathUtils.random(-64, 64)) / 16));

			Lightning lg = new Lightning();
			lg.end = new Vector2(target.x * LD39.TILE_SIZE, target.y * LD39.TILE_SIZE);
			lg.start = pos.cpy().add(new Vector2(0, 1200));
			lg.startWidth = 20;
			LD39.s.gs.addVisualEffect(lg);

			LD39.s.gs.powerSurge(target);
		}
	}
}
