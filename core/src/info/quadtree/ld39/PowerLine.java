package info.quadtree.ld39;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class PowerLine extends Building {

	Sprite isoSprite = null;

	@Override
	public int getCost() {
		return 25;
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

		LD39.s.batch.draw(isoSprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, LD39.TILE_SIZE, LD39.TILE_SIZE);

		if (neighbors != null) {
			for (Building b : neighbors) {
				if (b.pos.x > pos.x)
					LD39.s.batch.draw(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 8, 8, 16, 16, 1, 1, 0);

				if (b.pos.y > pos.y)
					LD39.s.batch.draw(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 8, 8, 16, 16, 1, 1, 90);

				if (b.pos.x < pos.x)
					LD39.s.batch.draw(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 8, 8, 16, 16, 1, 1, 180);

				if (b.pos.y < pos.y)
					LD39.s.batch.draw(sprite, pos.x * LD39.TILE_SIZE, pos.y * LD39.TILE_SIZE, 8, 8, 16, 16, 1, 1, 270);
			}
		}
	}
}
