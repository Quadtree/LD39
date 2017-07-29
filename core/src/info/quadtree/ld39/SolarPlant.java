package info.quadtree.ld39;

public class SolarPlant extends Building {

	@Override
	public String getGraphic() {
		return "solar";
	}

	@Override
	public TilePos getSize() {
		return new TilePos(3, 3);
	}

}