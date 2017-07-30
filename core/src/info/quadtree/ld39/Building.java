package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Building {
	class SearchNode implements Comparable<SearchNode> {
		public final Building building;
		public final double retained;

		public SearchNode(Building building, double retained) {
			super();
			this.building = building;
			this.retained = retained;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) (this.retained * 10000 - o.retained * 10000);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SearchNode)
				return (int) (((SearchNode) (obj)).retained * 10000) == (int) (this.retained * 10000);
			return super.equals(obj);
		}

	}

	static Sprite lowPowerSprite;
	static Sprite noPowerSprite;

	boolean alive = true;

	List<Connection> connections;

	List<Building> extraNeighbors = new ArrayList<Building>();

	boolean hasBuildingToEast = false;

	boolean hasBuildingToNorth = false;

	boolean hasBuildingToSouth = false;

	boolean hasBuildingToWest = false;

	public double isPowered = 0;

	Collection<Building> neighbors;
	public TilePos pos = new TilePos(0, 0);
	double power;

	Sprite sprite;

	public void addNeighbor(Building b) {
		if (extraNeighbors != null) {
			extraNeighbors.add(b);
		}
	}

	public Collection<Building> getAdjacentBuildings() {

		if (neighbors != null)
			return neighbors;

		hasBuildingToEast = false;
		hasBuildingToNorth = false;
		hasBuildingToSouth = false;
		hasBuildingToWest = false;

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

				Building bldg = LD39.s.gs.getBuildingOnTile(cp);

				if (bldg != null && x == 0)
					hasBuildingToWest = true;
				if (bldg != null && x == 1)
					hasBuildingToEast = true;

				if (bldg != null)
					ret.add(bldg);

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

				Building bldg = LD39.s.gs.getBuildingOnTile(cp);

				if (bldg != null && y == 0)
					hasBuildingToSouth = true;
				if (bldg != null && y == 1)
					hasBuildingToNorth = true;

				if (bldg != null)
					ret.add(bldg);

				if (ret.contains(this))
					throw new RuntimeException("Invalid " + cp);
			}
		}

		// System.out.println(this + " " + ret);

		this.neighbors = ret;

		return ret;
	}

	public Vector2 getCenter() {
		return new Vector2(pos.x * LD39.TILE_SIZE + getSize().x * LD39.TILE_SIZE / 2, pos.y * LD39.TILE_SIZE + getSize().y * LD39.TILE_SIZE / 2);
	}

	public int getCost() {
		return 0;
	}

	public abstract String getGraphic();

	public double getMaxPower() {
		return Math.abs(getNetPower());
	}

	public double getNetPower() {
		return 0;
	}

	public double getRetained() {
		return 1;
	}

	public int getSinkPriority() {
		return 0;
	}

	public abstract TilePos getSize();

	public double getSurgeDestructionOdds() {
		return 0.25;
	}

	public boolean hasEnoughPower() {
		if (isSink())
			return power < getMaxPower();
		return true;
	}

	public void hitByPowerSurge() {
		System.out.println(this + " hit by surge");

		if (Math.random() < getSurgeDestructionOdds()) {
			alive = false;

			for (int x = 0; x < getSize().x; ++x) {
				for (int y = 0; y < getSize().y; ++y) {
					TilePos tp = TilePos.create(pos.x + x, pos.y + y);

					for (int i = 0; i < 10; ++i) {
						ExplosionParticle ep = new ExplosionParticle(0, tp.toVector2().add(MathUtils.random(-10, 10), MathUtils.random(-10, 10)), new Vector2(MathUtils.random(-20, 20), MathUtils.random(-20, 20)));
						LD39.s.gs.addVisualEffect(ep);
					}
				}
			}

			int surgeProtectors = 0;

			for (Building b : LD39.s.gs.buildings) {
				if (b instanceof SurgeProtector) {
					surgeProtectors++;
				}
			}

			if (surgeProtectors == 0)
				Util.createHelpText("Add surge protectors to stop the propegation of power surges", new Vector2(500, 500));
		}
	}

	public boolean isColonyBuilding() {
		return false;
	}

	public boolean isSink() {
		return getNetPower() < 0;
	}

	public boolean isSource() {
		return getNetPower() > 0;
	}

	public boolean isSurgeStopper() {
		return false;
	}

	public boolean keep() {
		return alive;
	}

	public void removeExtraNeighbors() {
		extraNeighbors.clear();
	}

	public void render() {
		if (sprite == null)
			sprite = LD39.s.atlas.createSprite(getGraphic());

		if (lowPowerSprite == null)
			lowPowerSprite = LD39.s.atlas.createSprite("low_power");
		if (noPowerSprite == null)
			noPowerSprite = LD39.s.atlas.createSprite("no_power");

		sprite.setX(pos.x * LD39.TILE_SIZE);
		sprite.setY(pos.y * LD39.TILE_SIZE);
		/*
		 * if (power > 0.1) { sprite.setColor((float) (power / getMaxPower()),
		 * (float) (power / getMaxPower()), (float) (power / getMaxPower()), 1);
		 * } else { sprite.setColor(1, 0, 0, 1); }
		 */

		/*
		 * if (isPowered > 0.99f) { sprite.setColor(Color.WHITE); } else if
		 * (isPowered > 0.01f) { sprite.setColor(Color.YELLOW); } else
		 * sprite.setColor(Color.RED);
		 */

		sprite.setSize(getSize().x * 16, getSize().y * 16);

		sprite.draw(LD39.s.batch);

		if (isPowered > 0.99f) {
		} else if (isPowered > 0.01f) {
			LD39.s.batch.draw(lowPowerSprite, pos.toVector2().x, pos.toVector2().y + (getSize().y - 1) * LD39.TILE_SIZE);
		} else
			LD39.s.batch.draw(noPowerSprite, pos.toVector2().x, pos.toVector2().y + (getSize().y - 1) * LD39.TILE_SIZE);
	}

	public boolean requiresGeo() {
		return false;
	}

	public void update() {

		isPowered = 1;

		if (getNetPower() > 0.1) {
			power += getNetPower();
			LD39.s.gs.lastFramePowerGenerated += getNetPower();
		}

		if (getNetPower() < -0.1) {
			double powerToConsume = -getNetPower();

			if (powerToConsume > power) {
				isPowered = power / powerToConsume;
				powerToConsume = power;
			}

			LD39.s.gs.lastFramePowerSold += powerToConsume;
			power -= powerToConsume;

			double income = powerToConsume * LD39.POWER_PRICE;
			LD39.s.gs.money += income;
			LD39.s.gs.grossIncome.set(0, (float) (LD39.s.gs.grossIncome.get(0) + income));
		}

		if (isColonyBuilding() && isPowered < 0.95f) {
			LD39.s.gs.colonyGrowthTimer = 0;
		}

		if (power < 0)
			power = 0;
		if (power > getMaxPower())
			power = getMaxPower();
	}

	public void updateConnections() {
		if (connections == null && isSink()) {
			connections = new ArrayList<Connection>();

			Set<Building> closed = new HashSet<Building>();
			PriorityQueue<SearchNode> open = new PriorityQueue<SearchNode>();
			open.add(new SearchNode(this, 1));

			while (open.size() > 0) {
				SearchNode topNode = open.poll();
				closed.add(topNode.building);

				if (topNode.building.isSource()) {
					if (!isSource() || topNode.building.getSinkPriority() < this.getSinkPriority()) {
						connections.add(new Connection(topNode.retained, topNode.building, this, getSinkPriority()));
					}
				}

				for (Building b : topNode.building.getAdjacentBuildings()) {
					if (!closed.contains(b)) {
						open.add(new SearchNode(b, topNode.retained * b.getRetained()));
					}
				}
			}

			LD39.s.gs.addConnections(connections);
		}
	}

	public void updateTopology() {
		if (connections != null)
			LD39.s.gs.removeConnections(connections);

		connections = null;
		neighbors = null;
		extraNeighbors = null;
	}
}
