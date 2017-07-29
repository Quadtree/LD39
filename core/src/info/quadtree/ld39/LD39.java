package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class LD39 extends ApplicationAdapter {

	public static LD39 s;

	public final static int TILE_SIZE = 16;
	public TextureAtlas atlas;

	public SpriteBatch batch;

	List<Building> buildings = new ArrayList<Building>();

	public Texture img;

	@Override
	public void create() {
		LD39.s = this;

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		for (int i = 0; i < 4; ++i) {
			Hab nh = new Hab();
			nh.pos = new TilePos(1, 3 + i * 2);

			buildings.add(nh);
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		for (Building b : buildings)
			b.render();

		batch.end();
	}
}
