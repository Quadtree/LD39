package info.quadtree.ld39;

public class Foundry extends Building {
	public final static String NAME = "Foundry";

	@Override
	public String getGraphic() {
		return "foundry";
	}

	@Override
	public double getNetPower() {
		return -1;
	}

	@Override
	public double getRetained() {
		return 0.8;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(2, 2);
	}

	@Override
	public boolean isColonyBuilding() {
		return true;
	}
}
