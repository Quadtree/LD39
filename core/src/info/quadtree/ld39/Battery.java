package info.quadtree.ld39;

public class Battery extends Building {

	@Override
	public String getGraphic() {
		return "battery";
	}

	@Override
	public double getMaxPower() {
		return 200;
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

		sprite.setX(pos.x * LD39.TILE_SIZE);
		sprite.setY(pos.y * LD39.TILE_SIZE);

		sprite.setColor((float) (power / getMaxPower()),
				(float) (power / getMaxPower()), (float) (power / getMaxPower()), 1);

		sprite.setSize(getSize().x * 16, getSize().y * 16);

		sprite.draw(LD39.s.batch);
	}

	@Override
	public void update() {
	}
}
