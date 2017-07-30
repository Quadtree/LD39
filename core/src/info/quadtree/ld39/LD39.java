package info.quadtree.ld39;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LD39 extends ApplicationAdapter {

	public static boolean CHEATS = true;

	public final static int DAY_TICKS = 1000;
	public final static int GROSS_INCOME_TICKS = 60 * 60;
	public final static int GROSS_INCOME_TO_WIN = 2500;
	public final static float POWER_PRICE = .14f;
	public final static float REFUND_PCT = 0.5f;
	public static LD39 s;
	public final static int START_MONEY = 750;
	public final static int THUNDERCLOUD_MTTH = 60 * 30;
	public final static int TILE_SIZE = 16;

	public TextureAtlas atlas;

	public SpriteBatch batch;

	public TextButtonStyle defaultButtonStyle;
	public WindowStyle defaultDialogStyle;
	public LabelStyle defaultLabelStyle;

	public GameState gs;

	public Texture img;

	public BitmapFont mainFont;

	InputMultiplexer multiplexer;

	public ShapeRenderer shapeRnd;

	public Stage uiStage;

	public Table uiTable;

	public long updatesDone = 0;

	@Override
	public void create() {
		LD39.s = this;

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		shapeRnd = new ShapeRenderer();

		mainFont = new BitmapFont();

		defaultLabelStyle = new LabelStyle(mainFont, Color.WHITE);
		defaultDialogStyle = new WindowStyle(mainFont, Color.WHITE, new NinePatchDrawable(atlas.createPatch("dialog1")));
		defaultButtonStyle = new TextButtonStyle(new NinePatchDrawable(atlas.createPatch("dialog1")), new NinePatchDrawable(atlas.createPatch("dialog1")), new NinePatchDrawable(atlas.createPatch("dialog1")), mainFont);

		gs = new GameState();

		uiStage = new Stage();

		/*
		 * Util.createDialog("TEST", Util.createButton("Click", new
		 * ChangeListener() {
		 *
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * System.out.println("CLICK"); Util.getParentDialog(actor).remove(); }
		 * })).setPosition(100, 100);
		 */

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(uiStage);
		multiplexer.addProcessor(gs);

		Gdx.input.setInputProcessor(multiplexer);

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

		// Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Matrix4 m4 = new Matrix4();
		// m4.setToOrtho(0, 1024, 768, 0, 0, 1);
		// batch.setProjectionMatrix(m4);
		// batch.begin();

		gs.render();

		uiStage.draw();

		// batch.end();

	}

	public void restart() {
		multiplexer.removeProcessor(gs);
		gs = new GameState();
		multiplexer.addProcessor(gs);
	}
}
