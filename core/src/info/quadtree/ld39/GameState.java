package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

public class GameState {
	List<Building> buildings = new ArrayList<Building>();

	public GameState() {
		for (int i = 0; i < 4; ++i) {
			Hab nh = new Hab();
			nh.pos = new TilePos(1, 3 + i * 2);

			buildings.add(nh);
		}
	}

	public void render() {
		for (Building b : buildings)
			b.render();
	}

	public void update() {

	}
}
