package info.quadtree.ld39;

public class FusionPlant extends Building {

	boolean hasFuel = false;

	public double getBaseNetPower() {
		return 20;
	}

	@Override
	public int getCost() {
		return 1500;
	}

	public float getFuelCost() {
		return LD39.POWER_PRICE * 4;
	}

	@Override
	public String getGraphic() {
		return "fusion";
	}

	@Override
	public double getMaxPower() {
		return getBaseNetPower();
	}

	@Override
	public double getNetPower() {
		return hasFuel ? this.getBaseNetPower() : 0;
	}

	@Override
	public double getRetained() {
		return 0.9;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(5, 5);
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public void update() {
		if (LD39.s.gs.money >= 250 && power < getMaxPower() - 1) {
			LD39.s.gs.money -= getFuelCost();
			hasFuel = true;
			isPowered = 1;
		} else {
			hasFuel = false;
			isPowered = 0;
		}

		super.update();
	}

}
