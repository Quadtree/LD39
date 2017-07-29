package info.quadtree.ld39;

import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Building {

	public TilePos pos = new TilePos(0, 0);

	double power;

	Sprite sprite;

	public abstract String getGraphic();

	public double getMaxPower() {
		return 100;
	}

	public double getNetPower() {
		return 0;
	}

	public abstract TilePos getSize();

	public boolean keep() {
		return true;
	}

	public void render() {
		if (sprite == null)
			sprite = LD39.s.atlas.createSprite(getGraphic());

		sprite.setX(pos.x * LD39.TILE_SIZE);
		sprite.setY(pos.y * LD39.TILE_SIZE);
		sprite.setColor((float) (power / getMaxPower()), (float) (power / getMaxPower()), (float) (power / getMaxPower()), 1);
		sprite.draw(LD39.s.batch);
	}

	public void update() {
	}
}
