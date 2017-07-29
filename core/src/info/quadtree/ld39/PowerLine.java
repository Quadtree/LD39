package info.quadtree.ld39;

public class PowerLine extends Building {

	@Override
	public String getGraphic() {
		return "wire";
	}

	@Override
	public double getMaxPower() {
		return 10;
	}

	@Override
	public double getRetained() {
		return 0.97;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(1, 1);
	}

}
