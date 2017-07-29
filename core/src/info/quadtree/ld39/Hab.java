package info.quadtree.ld39;

public class Hab extends Building {
	public final static String NAME = "Hab";

	@Override
	public String getGraphic() {
		return "hab";
	}

	@Override
	public double getNetPower() {
		return -1;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(2, 2);
	}

}
