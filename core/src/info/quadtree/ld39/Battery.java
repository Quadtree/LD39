package info.quadtree.ld39;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Battery extends Building {

	static Sprite levelSprite;

	@Override
	public int getCost() {
		return 150;
	}

	@Override
	public String getGraphic() {
		return "battery";
	}

	@Override
	public double getMaxPower() {
		return 500;
	}

	@Override
	public double getRetained() {
		return 0.8;
	}

	@Override
	public int getSinkPriority() {
		return 1;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(1, 1);
	}

	@Override
	public boolean isSink() {
		return true;
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public void render() {
		if (sprite == null)
			sprite = LD39.s.atlas.createSprite(getGraphic());

		if (levelSprite == null)
			levelSprite = LD39.s.atlas.createSprite("battery_power");

		sprite.setX(pos.x * LD39.TILE_SIZE);
		sprite.setY(pos.y * LD39.TILE_SIZE);

		// sprite.setColor((float) (power / getMaxPower()),
		// (float) (power / getMaxPower()), (float) (power / getMaxPower()), 1);

		sprite.setSize(getSize().x * 16, getSize().y * 16);

		sprite.draw(LD39.s.batch);

		LD39.s.batch.draw(levelSprite, sprite.getX() + 4, sprite.getY() + 5, 8, (float) (power * 10 / getMaxPower()));
	}

	@Override
	public void update() {
		LD39.s.gs.lastFrameTotalPowerStored += power;
	}
}
