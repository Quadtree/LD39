package info.quadtree.ld39;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

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
		return new TilePos(3, 3);
	}

	@Override
	public boolean isColonyBuilding() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		if (MathUtils.random() < 1 / 60.f / 7.f && isPowered > .95) {
			runTime = 60;
		}

		if (runTime > 0 && runTime % 5 == 0) {
			ExplosionParticle ep = new ExplosionParticle(0, pos.toVector2().add(13 + MathUtils.random(0, 2) * 9, 37), new Vector2(MathUtils.random(-3.f, 3.f), 6));
			ep.drag = 1;
			LD39.s.gs.addVisualEffect(ep);
		}

		// 13,37
		// *9

		runTime--;
	}
}
