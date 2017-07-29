package info.quadtree.ld39;

import com.badlogic.gdx.math.MathUtils;

public class Foundry extends Building {
	public final static String NAME = "Foundry";

	int runTime = 0;

	@Override
	public String getGraphic() {
		return "foundry";
	}

	@Override
	public double getNetPower() {
		return runTime > 0 ? -5 : -0.5;
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

	@Override
	public void update() {
		super.update();

		if (MathUtils.random() < 1 / 60.f / 7.f) {
			runTime = 60;
		}

		runTime--;
	}
}
