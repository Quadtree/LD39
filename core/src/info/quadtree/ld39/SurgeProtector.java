package info.quadtree.ld39;

public class SurgeProtector extends Building {

	@Override
	public String getGraphic() {
		return "wire_prot";
	}

	@Override
	public double getMaxPower() {
		return 10;
	}

	@Override
	public double getRetained() {
		return 0.8;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(1, 1);
	}

	@Override
	public double getSurgeDestructionOdds() {
		return 0;
	}

	@Override
	public boolean isSurgeStopper() {
		return true;
	}
}
