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
import com.badlogic.gdx.math.MathUtils;

public class GameState implements InputProcessor {
	TilePos buildingDragStart = null;

	List<Building> buildings = new ArrayList<Building>();
	public int colonyGrowthTimer = 0;
	private List<Connection> connections = new ArrayList<Connection>();

	boolean connectionsNeedsSort = false;

	int dayTicks = 0;

	public List<Float> grossIncome = new ArrayList<Float>();

	Building heldBuilding = null;

	public boolean isDay = true;

	float money = 1000;
	int mx, my;

	List<VisualEffect> visualEffects = new ArrayList<VisualEffect>();

	public GameState() {
		/*
		 * for (int i = 0; i < 3; ++i) { for (int j = 0; j < 1; ++j) { Hab nh =
		 * new Hab(); nh.pos = new TilePos(16 + j * 2, 16 + i * 2);
		 *
		 * buildings.add(nh); } }
		 */

		spawnColonyBuilding();

		Gdx.input.setInputProcessor(this);
	}

	public void addConnections(Collection<Connection> conns) {
		connections.addAll(conns);
		connectionsNeedsSort = true;
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
				if (this.getBuildingOnTile(TilePos.create(x, y)) != null)
					return false;
			}
		}

		return true;
	}

	public boolean isAreaClearFor(Building b) {
		return isAreaClear(b.pos, b.getSize());
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
		for (Building b : buildings)
			b.render();

		for (Building b : getBuildingsToPlace()) {
			b.render();
		}
		LD39.s.batch.end();

		for (VisualEffect ve : visualEffects)
			ve.render();

		LD39.s.batch.begin();
		LD39.s.mainFont.draw(LD39.s.batch, "Credits: " + (int) money, 20, Gdx.graphics.getHeight() - 40);
		LD39.s.mainFont.draw(LD39.s.batch, "Income/minute: " + (int) getGrossIncome() + "/" + LD39.GROSS_INCOME_TO_WIN, 20, Gdx.graphics.getHeight() - 20);
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

		buildings.add(nb);
	}

	public void topologyChanged() {
		for (Building b : buildings)
			b.updateTopology();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		mx = screenX;
		my = screenY;

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

			for (Building b : buildingsToPlace) {
				if (isAreaClearFor(b))
					buildings.add(b);
			}

			heldBuilding = null;
			buildingDragStart = null;

			topologyChanged();
		}

		return false;
	}

	public void update() {

		long startTime = System.currentTimeMillis();

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

		if (getGrossIncome() >= LD39.GROSS_INCOME_TO_WIN) {

		}

		long endTime = System.currentTimeMillis();

		if (endTime - startTime > 16) {
			System.out.println("Frame took " + (endTime - startTime) + "ms!");
		}
	}
}
