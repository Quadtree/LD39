package info.quadtree.ld39;

public class Hab extends Building {
	@Override
	public String getGraphic() {
		return "hab";
	}

	@Override
	public TilePos getSize() {
		return new TilePos(2, 2);
	}
}
