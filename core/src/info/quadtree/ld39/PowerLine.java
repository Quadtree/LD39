package info.quadtree.ld39;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class PowerLine extends Building {

	Sprite isoSprite = null;

	void drawAtRotation(Sprite sprite, float x, float y, float rotation) {
		sprite.setPosition(x, y);
		sprite.setRotation(rotation);
		sprite.setOrigin(8, 8);

		setupNightTint(sprite);

		sprite.draw(LD39.s.batch);
	}

	@Override
	public int getCost() {
		return 5;
	}

	@Override
	public String getGraphic() {
		return "wire";
	}

	@Override
	public double getMaxPower() {
		return 10;
	}

	@Override
	public double getRetained() {
		return 0.97;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(1, 1);
	}

	@Override
	public double getSurgeDestructionOdds() {
		return 0;
	}

	@Override
	public void render() {
		if (sprite == null)
			sprite = LD39.s.atlas.createSprite("wire");

		if (isoSprite == null)
			isoSprite = LD39.s.atlas.createSprite("wire_iso");

		drawAtRotation(isoSprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 0);

		getAdjacentBuildings();

		if (extraNeighbors != null) {
			for (Building b : extraNeighbors) {

				if (b.pos.x > pos.x)
					hasBuildingToEast = true;
				if (b.pos.x < pos.x)
					hasBuildingToWest = true;

				if (b.pos.y > pos.y)
					hasBuildingToNorth = true;
				if (b.pos.y < pos.y)
					hasBuildingToSouth = true;

			}
		}

		if (hasBuildingToEast)
			drawAtRotation(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 0);

		if (hasBuildingToNorth)
			drawAtRotation(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 90);

		if (hasBuildingToWest)
			drawAtRotation(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 180);

		if (hasBuildingToSouth)
			drawAtRotation(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 270);
	}
}
