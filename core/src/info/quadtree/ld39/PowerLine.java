package info.quadtree.ld39;

public class PowerLine extends Building {

	@Override
	public String getGraphic() {
		return "wire";
	}

	@Override
	public TilePos getSize() {
		return new TilePos(1, 1);
	}

}
