package info.quadtree.ld39;

import com.badlogic.gdx.math.MathUtils;

public class Labratory extends Building {
	public final static String NAME = "Lab";

	@Override
	public String getGraphic() {
		return "lab";
	}

	@Override
	public double getNetPower() {
		return -3;
	}

	@Override
	public TilePos getSize() {
		return new TilePos(3, 3);
	}

	@Override
	public double getSurgeDestructionOdds() {
		return 0;
	}

	@Override
	public boolean isColonyBuilding() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		if (isPowered > 0.0001) {
			if (MathUtils.random() < 1 / 60.f / 15.f) {
				LD39.s.gs.powerSurge(pos);
				LD39.s.playSound("electric" + MathUtils.random(0, 1) + ".wav");
			}
		}
	}
}
