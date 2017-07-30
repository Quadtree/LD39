package info.quadtree.ld39;

public class FusionPlant extends Building {

	boolean hasFuel = false;

	@Override
	public int getCost() {
		return 1500;
	}

	public float getFuelCost() {
		return LD39.POWER_PRICE * 6;
	}

	@Override
	public String getGraphic() {
		return "fusion";
	}

	@Override
	public double getNetPower() {
		return hasFuel ? 12 : 0;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(5, 5);
	}

	@Override
	public void update() {
		super.update();

		if (LD39.s.gs.money >= getFuelCost()) {
			LD39.s.gs.money -= getFuelCost();
			hasFuel = true;
			isPowered = 1;
		} else {
			hasFuel = false;
			isPowered = 0;
		}

	}

}
