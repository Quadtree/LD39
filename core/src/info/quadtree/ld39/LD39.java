package info.quadtree.ld39;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LD39 extends ApplicationAdapter {

	public static boolean CHEATS = true;

	public static LD39 s;
	public final static int TILE_SIZE = 16;

	public TextureAtlas atlas;

	public SpriteBatch batch;

	public GameState gs;

	public Texture img;

	public ShapeRenderer shapeRnd;

	public long updatesDone = 0;

	@Override
	public void create() {
		LD39.s = this;

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		shapeRnd = new ShapeRenderer();

		gs = new GameState();

		updatesDone = System.currentTimeMillis() / 16;
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void render() {

		while (updatesDone * 16 < System.currentTimeMillis()) {
			gs.update();
			updatesDone++;
		}

		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Matrix4 m4 = new Matrix4();
		// m4.setToOrtho(0, 1024, 768, 0, 0, 1);
		// batch.setProjectionMatrix(m4);
		batch.begin();

		gs.render();

		batch.end();

	}
}
