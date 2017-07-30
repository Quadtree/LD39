package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameState implements InputProcessor {
	enum TerrainType {
		Geothermal, Ground, Rock
	}

	TilePos buildingDragStart = null;
	List<Building> buildings = new ArrayList<Building>();
	List<Thundercloud> clouds = new ArrayList<Thundercloud>();

	public int colonyGrowthTimer = 0;

	private List<Connection> connections = new ArrayList<Connection>();

	boolean connectionsNeedsSort = false;

	int dayTicks = 0;

	Sprite geoSprite;

	public List<Float> grossIncome = new ArrayList<Float>();
	boolean hasWonYet = false;

	Building heldBuilding = null;

	public boolean isDay = true;

	public float lastFramePowerGenerated = 0;

	public float lastFramePowerSold = 0;

	public float lastFramePowerWasted = 0;

	public float lastFrameTotalPowerStored = 0;

	float money = LD39.START_MONEY;
	int mx, my;
	Sprite rockSprite;
	TerrainType[][] terrainTypes;

	int ticksSinceLastSpawn = 0;

	List<VisualEffect> visualEffects = new ArrayList<VisualEffect>();

	public GameState() {
		/*
		 * for (int i = 0; i < 3; ++i) { for (int j = 0; j < 1; ++j) { Hab nh =
		 * new Hab(); nh.pos = new TilePos(16 + j * 2, 16 + i * 2);
		 *
		 * buildings.add(nh); } }
		 */

		geoSprite = LD39.s.atlas.createSprite("geo");
		rockSprite = LD39.s.atlas.createSprite("rock");

		terrainTypes = new TerrainType[75][];
		for (int i = 0; i < terrainTypes.length; ++i)
			terrainTypes[i] = new TerrainType[75];

		for (int x = 0; x < 75; ++x) {
			for (int y = 0; y < 75; ++y) {
				terrainTypes[x][y] = TerrainType.Ground;

				if (MathUtils.randomBoolean(0.005f))
					terrainTypes[x][y] = TerrainType.Rock;
				if (MathUtils.randomBoolean(0.001f))
					terrainTypes[x][y] = TerrainType.Geothermal;
			}
		}

		spawnColonyBuilding();
	}

	public void addConnections(Collection<Connection> conns) {
		connections.addAll(conns);
		connectionsNeedsSort = true;
	}

	public void addVisualEffect(VisualEffect vf) {
		visualEffects.add(vf);
	}

	public Building getBuildingOnTile(TilePos pos) {
		for (Building b : buildings) {
			int dx = pos.x - b.pos.x;
			int dy = pos.y - b.pos.y;

			if (dx >= 0 && dy >= 0 && dx < b.getSize().x && dy < b.getSize().y) {
				return b;
			}
		}

		return null;
	}

	protected List<Building> getBuildingsToPlace() {
		List<Building> buildingsToPlace = new ArrayList<Building>();

		if (heldBuilding == null)
			return buildingsToPlace;

		buildingsToPlace.add(heldBuilding);

		if (buildingDragStart != null && (heldBuilding instanceof PowerLine || heldBuilding instanceof Battery)) {
			TilePos ctp = buildingDragStart;

			while (!ctp.equals(heldBuilding.pos)) {
				Building nb = null;

				if (heldBuilding instanceof PowerLine)
					nb = new PowerLine();
				if (heldBuilding instanceof Battery)
					nb = new Battery();

				if (nb == null)
					break;

				nb.pos = ctp;
				buildingsToPlace.add(nb);

				if (ctp.x != heldBuilding.pos.x) {
					if (ctp.x > heldBuilding.pos.x)
						ctp = TilePos.create(ctp.x - 1, ctp.y);
					else
						ctp = TilePos.create(ctp.x + 1, ctp.y);
				} else {
					if (ctp.y > heldBuilding.pos.y)
						ctp = TilePos.create(ctp.x, ctp.y - 1);
					else
						ctp = TilePos.create(ctp.x, ctp.y + 1);
				}

				// System.out.println(ctp);
			}
		}
		return buildingsToPlace;
	}

	public float getGrossIncome() {
		float ret = 0;

		for (Float f : grossIncome)
			ret += f;

		return ret;
	}

	public TilePos getMouseTilePos() {
		return new TilePos(mx / LD39.TILE_SIZE, (768 - my) / LD39.TILE_SIZE);
	}

	public boolean isAreaClear(TilePos area, TilePos size) {
		for (int x = area.x; x < area.x + size.x; x++) {
			for (int y = area.y; y < area.y + size.y; y++) {
				if (x >= 0 && y >= 0 && x < 75 && y < 75 && terrainTypes[x][y] == TerrainType.Rock)
					return false;
				Building bldg = this.getBuildingOnTile(TilePos.create(x, y));
				if (bldg != null && !(bldg instanceof PowerLine) && !(bldg instanceof SurgeProtector))
					return false;
			}
		}

		return true;
	}

	public boolean isAreaClearFor(Building b) {
		boolean ret = isAreaClear(b.pos, b.getSize());

		if (ret && b.requiresGeo()) {
			for (int x = b.pos.x; x < b.pos.x + b.getSize().x; x++) {
				for (int y = b.pos.y; y < b.pos.y + b.getSize().y; y++) {
					if (x >= 0 && y >= 0 && x < 75 && y < 75 && terrainTypes[x][y] == TerrainType.Geothermal)
						return true;
				}
			}

			return false;
		}

		return ret;
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Input.Keys.NUM_1)
			heldBuilding = new PowerLine();

		if (keycode == Input.Keys.NUM_2)
			heldBuilding = new SolarPlant();

		if (keycode == Input.Keys.NUM_3)
			heldBuilding = new SurgeProtector();

		if (keycode == Input.Keys.NUM_4)
			heldBuilding = new FusionPlant();

		if (keycode == Input.Keys.NUM_5)
			heldBuilding = new HydrocarbonPlant();

		if (keycode == Input.Keys.NUM_6)
			heldBuilding = new Battery();

		if (LD39.CHEATS) {
			if (keycode == Input.Keys.S) {
				powerSurge(getMouseTilePos());
			}

			if (keycode == Input.Keys.EQUALS)
				money += 10000;
			if (keycode == Input.Keys.MINUS)
				money -= 10000;

			if (keycode == Input.Keys.D) {
				isDay = !isDay;
				dayTicks = 0;
			}

			if (keycode == Input.Keys.B)
				spawnColonyBuilding();

			if (keycode == Input.Keys.R)
				topologyChanged();

			if (keycode == Input.Keys.L)
				startStorm();
		}

		setHeldBuildingLoc();

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mx = screenX;
		my = screenY;

		if (heldBuilding != null)
			setHeldBuildingLoc();
		return false;
	}

	public void powerSurge(TilePos pos) {
		Building start = this.getBuildingOnTile(pos);

		if (start != null) {
			Set<Building> closed = new HashSet<Building>();
			Set<Building> open = new HashSet<Building>();
			open.add(start);

			while (open.size() > 0) {
				Building next = open.iterator().next();

				closed.add(next);
				open.remove(next);

				next.hitByPowerSurge();

				if (!next.isSurgeStopper()) {
					for (Building b : next.getAdjacentBuildings()) {
						if (!closed.contains(b) && !open.contains(b)) {
							open.add(b);

							Lightning lg = new Lightning();
							lg.setStart(next.getCenter());
							lg.setEnd(b.getCenter());
							visualEffects.add(lg);
						}
					}
				}

			}
		}
	}

	public void removeConnections(Collection<Connection> conns) {
		connections.removeAll(conns);
	}

	public void render() {
		if (isDay)
			Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		else
			Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		LD39.s.batch.begin();

		for (int x = 0; x < 75; ++x) {
			for (int y = 0; y < 75; ++y) {
				if (terrainTypes[x][y] == TerrainType.Geothermal)
					LD39.s.batch.draw(geoSprite, x * LD39.TILE_SIZE, y * LD39.TILE_SIZE);
				if (terrainTypes[x][y] == TerrainType.Rock)
					LD39.s.batch.draw(rockSprite, x * LD39.TILE_SIZE, y * LD39.TILE_SIZE);
			}
		}

		for (Building b : buildings)
			b.render();

		for (Building b : getBuildingsToPlace()) {
			b.render();
		}

		for (Thundercloud tc : clouds) {
			tc.render();
		}

		LD39.s.batch.end();

		for (VisualEffect ve : visualEffects)
			ve.render();

		LD39.s.batch.begin();
		LD39.s.mainFont.draw(LD39.s.batch, "Credits: " + (int) money, 20, Gdx.graphics.getHeight() - 40);
		LD39.s.mainFont.draw(LD39.s.batch, "Income/minute: " + (int) getGrossIncome() + "/" + LD39.GROSS_INCOME_TO_WIN, 20, Gdx.graphics.getHeight() - 20);

		LD39.s.mainFont.draw(LD39.s.batch, "== Power ==", Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 20);
		LD39.s.mainFont.draw(LD39.s.batch, "Generated: " + (int) (this.lastFramePowerGenerated * LD39.POWER_PRICE * 60), Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 40);
		LD39.s.mainFont.draw(LD39.s.batch, "In Storage: " + (int) (this.lastFrameTotalPowerStored * LD39.POWER_PRICE * 60), Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 60);
		LD39.s.mainFont.draw(LD39.s.batch, "Sold: " + (int) (this.lastFramePowerSold * LD39.POWER_PRICE * 60), Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 80);
		LD39.s.mainFont.draw(LD39.s.batch, "Wasted: " + (int) (this.lastFramePowerWasted * LD39.POWER_PRICE * 60), Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 100);
		LD39.s.batch.end();
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setHeldBuildingLoc() {
		// System.out.println(mx + " " + my);
		// System.out.println(getMouseTilePos());

		if (heldBuilding != null) {
			heldBuilding.pos = getMouseTilePos();
		}
	}

	public void spawnColonyBuilding() {
		Building nb = null;
		ticksSinceLastSpawn = 0;

		int totalColonyBuildings = 0;

		for (Building cb : buildings)
			if (cb.isColonyBuilding())
				totalColonyBuildings++;

		if (totalColonyBuildings < 4) {
			nb = new Hab();
		} else if (totalColonyBuildings < 10) {
			if (MathUtils.random() < 0.6f)
				nb = new Hab();
			else
				nb = new Foundry();
		} else {
			if (MathUtils.random() < 0.4f)
				nb = new Hab();
			else {
				if (MathUtils.randomBoolean())
					nb = new Foundry();
				else
					nb = new Labratory();
			}

		}

		nb.pos = TilePos.create(Gdx.graphics.getWidth() / LD39.TILE_SIZE / 2, Gdx.graphics.getHeight() / LD39.TILE_SIZE / 2);

		int loops = 0;

		while (!isAreaClear(TilePos.create(nb.pos.x - 1, nb.pos.y - 1), TilePos.create(nb.getSize().x + 2, nb.getSize().y + 2))) {
			nb.pos = TilePos.create(nb.pos.x + MathUtils.random(-4, 4), nb.pos.y + MathUtils.random(-4, 4));

			if (++loops > 10000)
				return;
		}

		if (nb instanceof Labratory)
			Util.createHelpText("Labratories use lots of power, which is good\nbut the dangerous experiments tend to\ngenerate power surges. Add surge protectors\nto this circuit.", nb.pos.toVector2().add(40, 0));

		if (nb instanceof Foundry)
			Util.createHelpText("Foundries need power in short bursts.\nAdd batteries to this circuit.", nb.pos.toVector2().add(40, 0));

		buildings.add(nb);
	}

	public void startStorm() {
		for (int i = 0; i < 20; ++i) {
			clouds.add(new Thundercloud());
		}
	}

	public void topologyChanged() {
		for (Building b : buildings)
			b.updateTopology();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		mx = screenX;
		my = screenY;

		if (button == Input.Buttons.RIGHT) {
			if (heldBuilding != null) {
				heldBuilding = null;
			} else {
				Building bldg = getBuildingOnTile(getMouseTilePos());

				if (bldg != null && !bldg.isColonyBuilding()) {
					money += bldg.getCost() * LD39.REFUND_PCT;
					buildings.remove(bldg);
				}
			}
			return true;
		}

		if (heldBuilding != null)
			buildingDragStart = getMouseTilePos();

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mx = screenX;
		my = screenY;
		setHeldBuildingLoc();
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (heldBuilding != null) {
			List<Building> buildingsToPlace = getBuildingsToPlace();

			int totalCost = 0;

			for (Building b : buildingsToPlace) {
				if (isAreaClearFor(b))
					totalCost += b.getCost();
			}

			if (totalCost > 0 && totalCost <= money) {
				for (Building b : buildingsToPlace) {
					if (isAreaClearFor(b)) {
						for (int x = 0; x < b.getSize().x; ++x) {
							for (int y = 0; y < b.getSize().y; ++y) {
								Building bldg = getBuildingOnTile(TilePos.create(x + b.pos.x, y + b.pos.y));

								if (bldg != null) {
									money += bldg.getCost() * LD39.REFUND_PCT;
									buildings.remove(bldg);
								}
							}
						}

						buildings.add(b);
					}
				}

				heldBuilding = null;
				buildingDragStart = null;

				topologyChanged();
				money -= totalCost;
			}

		}

		return false;
	}

	public void update() {

		Util.createHelpText("Click on a solar plant on the right sidebar\nand place it next to the colony in the middle", new Vector2(800, 300));

		int solarCount = 0;
		int batteryCount = 0;

		for (Building b : buildings) {
			if (b instanceof SolarPlant)
				solarCount++;
			if (b instanceof Battery)
				batteryCount++;
		}

		if (!isDay && solarCount > 0 && batteryCount == 0)
			Util.createHelpText("Solar plants don't work at night.\nConsider adding some batteries.", new Vector2(500, 500));

		ticksSinceLastSpawn++;

		if (ticksSinceLastSpawn > 60 * 30)
			Util.createHelpText("All buildings must be constantly powered\nin order for the colony to expand.", new Vector2(500, 500));

		long startTime = System.currentTimeMillis();

		this.lastFramePowerGenerated = 0;
		this.lastFramePowerSold = 0;
		this.lastFramePowerWasted = 0;
		this.lastFrameTotalPowerStored = 0;

		grossIncome.add(0, 0.f);

		while (grossIncome.size() > LD39.GROSS_INCOME_TICKS)
			grossIncome.remove(LD39.GROSS_INCOME_TICKS);

		for (Building b : buildings)
			b.updateConnections();

		if (connectionsNeedsSort) {
			Collections.sort(connections);
			// System.out.println(connections);
			connectionsNeedsSort = false;
		}

		for (Connection conn : connections)
			conn.execute();

		for (int i = 0; i < buildings.size(); ++i) {
			if (buildings.get(i).keep())
				buildings.get(i).update();
			else {
				buildings.remove(i--);
				topologyChanged();
			}
		}

		for (int i = 0; i < visualEffects.size(); ++i) {
			if (visualEffects.get(i).keep)
				visualEffects.get(i).update();
			else
				visualEffects.remove(i--);
		}

		for (int i = 0; i < clouds.size(); ++i) {
			if (clouds.get(i).keep())
				clouds.get(i).update();
			else
				clouds.remove(i--);
		}

		dayTicks++;

		if (dayTicks >= LD39.DAY_TICKS) {
			dayTicks = 0;
			isDay = !isDay;
		}

		colonyGrowthTimer++;

		if (colonyGrowthTimer >= 60 * 8) {
			spawnColonyBuilding();
			colonyGrowthTimer = 0;
		}

		if (MathUtils.random(LD39.THUNDERCLOUD_MTTH - 1) == 0)
			startStorm();

		if (!hasWonYet && getGrossIncome() >= LD39.GROSS_INCOME_TO_WIN) {
			hasWonYet = true;
			Util.createDialog("You have met your quota for income! Your advancement is assured.\nYou can continue playing if you like, or you can restart and play again!",
					Util.createButton("Continue", new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							Util.getParentDialog(actor).remove();
						}
					}),
					Util.createButton("Restart", new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							Util.getParentDialog(actor).remove();
							LD39.s.restart();
						}
					}));
		}

		long endTime = System.currentTimeMillis();

		if (endTime - startTime > 16) {
			System.out.println("Frame took " + (endTime - startTime) + "ms!");
		}
	}
}
