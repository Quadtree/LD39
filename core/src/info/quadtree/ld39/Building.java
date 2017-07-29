package info.quadtree.ld39;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Building {

	public TilePos pos = new TilePos(0, 0);

	double power;

	Sprite sprite;

	public Collection<Building> getAdjacentBuildings() {
		Set<Building> ret = new HashSet<Building>();

		for (int x = 0; x < 2; ++x) {
			for (int y = 0; y < getSize().y; ++y) {
				TilePos cp = TilePos.create(pos.x - 1 + (x * (getSize().x + 1)), y + pos.y);

				/*
				 * LD39.s.batch.begin(); Sprite sp =
				 * LD39.s.atlas.createSprite("wire"); sp.setX(cp.x *
				 * LD39.TILE_SIZE); sp.setY(cp.y * LD39.TILE_SIZE);
				 * sp.draw(LD39.s.batch); LD39.s.batch.end();
				 */

				ret.add(LD39.s.gs.getBuildingOnTile(cp));

				if (ret.contains(this))
					throw new RuntimeException("Invalid " + cp);
			}
		}

		for (int y = 0; y < 2; ++y) {
			for (int x = 0; x < getSize().x; ++x) {
				TilePos cp = TilePos.create(x + pos.x, pos.y - 1 + (y * (getSize().y + 1)));

				/*
				 * LD39.s.batch.begin(); Sprite sp =
				 * LD39.s.atlas.createSprite("wire"); sp.setX(cp.x *
				 * LD39.TILE_SIZE); sp.setY(cp.y * LD39.TILE_SIZE);
				 * sp.draw(LD39.s.batch); LD39.s.batch.end();
				 */

				ret.add(LD39.s.gs.getBuildingOnTile(cp));

				if (ret.contains(this))
					throw new RuntimeException("Invalid " + cp);
			}
		}

		ret.remove(null);

		// System.out.println(ret);

		return ret;
	}

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
		if (power > 0.1) {
			sprite.setColor((float) (power / getMaxPower()), (float) (power / getMaxPower()), (float) (power / getMaxPower()), 1);
		} else {
			sprite.setColor(1, 0, 0, 1);
		}

		sprite.draw(LD39.s.batch);
	}

	public void update() {
		for (Building b : getAdjacentBuildings()) {
			if (b.power > this.power) {
				double transfer = (b.power - this.power) / 2;

				// don't allow transfers over the maximum
				transfer = Math.min(transfer, this.getMaxPower() - this.power);
				b.power -= transfer;
				this.power += transfer;
			}
		}

		power += getNetPower();

		if (power < 0)
			power = 0;
		if (power > getMaxPower())
			power = getMaxPower();
	}
}
