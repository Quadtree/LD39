package info.quadtree.ld39;

public class SolarPlant extends Building {

	@Override
	public int getCost() {
		return 300;
	}

	@Override
	public String getGraphic() {
		return "solar";
	}

	@Override
	public double getNetPower() {
		return LD39.s.gs.isDay ? 3.0 : 0;
	}

	@Override
	public double getRetained() {
		return 0.9;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(3, 3);
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		isPowered = LD39.s.gs.isDay ? 1 : 0;
	}

}
