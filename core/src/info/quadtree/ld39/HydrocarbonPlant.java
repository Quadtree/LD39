package info.quadtree.ld39;

public class HydrocarbonPlant extends Building {

	@Override
	public int getCost() {
		return 400;
	}

	@Override
	public String getGraphic() {
		return "hydro";
	}

	@Override
	public double getNetPower() {
		return 2.0;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(2, 2);
	}

	@Override
	public boolean requiresGeo() {
		return true;
	}

}
