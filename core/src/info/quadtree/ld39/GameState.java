package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameState implements InputProcessor {
	TilePos buildingDragStart = null;

	List<Building> buildings = new ArrayList<Building>();
	Building heldBuilding = null;

	int mx, my;

	public GameState() {
		for (int i = 0; i < 4; ++i) {
			Hab nh = new Hab();
			nh.pos = new TilePos(1, 3 + i * 2);

			buildings.add(nh);
		}

		Gdx.input.setInputProcessor(this);
	}

	public TilePos getMouseTilePos() {
		return new TilePos(mx / LD39.TILE_SIZE, (768 - my) / LD39.TILE_SIZE);
	}

	public boolean isAreaClearFor(Building b) {
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Input.Keys.NUM_1)
			heldBuilding = new PowerLine();

		if (keycode == Input.Keys.NUM_2)
			heldBuilding = new SolarPlant();

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

	public void render() {
		for (Building b : buildings)
			b.render();

		if (heldBuilding != null) {
			heldBuilding.render();
		}
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (heldBuilding != null) {
			List<Building> buildingsToPlace = new ArrayList<Building>();

			buildingsToPlace.add(heldBuilding);

			if (heldBuilding.getSize().x == 1 && heldBuilding.getSize().y == 1) {

			}
		}

		return false;
	}

	public void update() {

	}
}
