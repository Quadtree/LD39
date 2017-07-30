package info.quadtree.ld39;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class LD39 extends ApplicationAdapter {

	interface BuildingFactory {
		Building create();
	}

	public static boolean CHEATS = true;
	public final static int DAY_TICKS = 1000;
	public final static int GROSS_INCOME_TICKS = 60 * 60;
	public final static int GROSS_INCOME_TO_WIN = 2500;
	public final static float POWER_PRICE = .2f;
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

	Label infoGrossIncome;

	Label infoLastFramePowerGenerated;

	Label infoLastFramePowerSold;

	Label infoLastFramePowerWasted;

	Label infoLastFrameTotalPowerStored;

	Label infoMoney;

	public BitmapFont mainFont;
	InputMultiplexer multiplexer;
	public ShapeRenderer shapeRnd;
	Map<String, Sound> soundMap = new HashMap<String, Sound>();
	public Stage uiStage;
	public Table uiTable;

	public long updatesDone = 0;

	@Override
	public void create() {
		LD39.s = this;

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		shapeRnd = new ShapeRenderer();

		mainFont = new BitmapFont(Gdx.files.internal("in14.fnt"));

		defaultLabelStyle = new LabelStyle(mainFont, Color.WHITE);
		defaultDialogStyle = new WindowStyle(mainFont, Color.WHITE, new NinePatchDrawable(atlas.createPatch("dialog1")));
		defaultButtonStyle = new TextButtonStyle(new NinePatchDrawable(atlas.createPatch("dialog3")), new NinePatchDrawable(atlas.createPatch("dialog2")), new NinePatchDrawable(atlas.createPatch("dialog3")), mainFont);

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

		Table rightPaneTable = new Table();
		rightPaneTable.setWidth(150);
		rightPaneTable.setHeight(Gdx.graphics.getHeight());
		rightPaneTable.setBackground(new NinePatchDrawable(atlas.createPatch("dialog1")));
		rightPaneTable.setX(Gdx.graphics.getWidth() - 150);

		Table infoLabels = new Table();
		infoLabels.add(Util.createLabel("$")).pad(4);
		infoLabels.add(infoMoney = Util.createLabel("")).pad(4).row();

		infoLabels.add(Util.createLabel("$/min")).pad(4);
		infoLabels.add(infoGrossIncome = Util.createLabel("")).pad(4).row();

		infoLabels.add(Util.createLabel("Pwr/s Gner")).pad(4);
		infoLabels.add(infoLastFramePowerGenerated = Util.createLabel("")).pad(4).row();

		infoLabels.add(Util.createLabel("Pwr/s Lost")).pad(4);
		infoLabels.add(infoLastFramePowerWasted = Util.createLabel("")).pad(4).row();

		infoLabels.add(Util.createLabel("Pwr/s Sold")).pad(4);
		infoLabels.add(infoLastFramePowerSold = Util.createLabel("")).pad(4).row();

		infoLabels.add(Util.createLabel("Pwr Stored")).pad(4);
		infoLabels.add(infoLastFrameTotalPowerStored = Util.createLabel("")).pad(4).row();

		rightPaneTable.add(infoLabels).align(Align.top).fill().top().row();

		rightPaneTable.add().height(120).row();

		Table buyButtons = new Table();

		buyButtons.add(createBuyButton("Wire", new BuildingFactory() {
			@Override
			public Building create() {
				return new PowerLine();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Battery", new BuildingFactory() {
			@Override
			public Building create() {
				return new Battery();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Surge Protector", new BuildingFactory() {
			@Override
			public Building create() {
				return new SurgeProtector();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Solar Plant", new BuildingFactory() {
			@Override
			public Building create() {
				return new SolarPlant();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Geothermal Plant", new BuildingFactory() {
			@Override
			public Building create() {
				return new HydrocarbonPlant();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Fusion Plant", new BuildingFactory() {
			@Override
			public Building create() {
				return new FusionPlant();
			}
		})).pad(6).fill().row();

		rightPaneTable.add(buyButtons);

		uiStage.addActor(rightPaneTable);

		updatesDone = System.currentTimeMillis() / 16;
	}

	Button createBuyButton(String text, final BuildingFactory fact) {
		Button buyButton1 = new Button(defaultButtonStyle);
		buyButton1.add(Util.createLabel(text)).row();
		buyButton1.add(new Image(new TextureRegionDrawable(atlas.createSprite(fact.create().getGraphic())))).width(32).height(32).row();
		buyButton1.add(Util.createLabel("$" + fact.create().getCost())).row();
		buyButton1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				LD39.s.gs.heldBuilding = fact.create();
			}
		});

		return buyButton1;
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	public void playSound(String sound) {
		this.playSound(sound, 1);
	}

	public void playSound(String sound, float volume) {
		if (!soundMap.containsKey(sound))
			soundMap.put(sound, Gdx.audio.newSound(Gdx.files.internal("sound/" + sound)));

		soundMap.get(sound).play(volume);
	}

	@Override
	public void render() {

		while (updatesDone * 16 < System.currentTimeMillis()) {
			gs.update();
			updatesDone++;
		}

		infoMoney.setText("" + (int) gs.money);
		infoGrossIncome.setText((int) gs.getGrossIncome() + "/" + LD39.GROSS_INCOME_TO_WIN);
		infoLastFramePowerGenerated.setText("" + (int) (gs.lastFramePowerGenerated * LD39.POWER_PRICE * 60));
		infoLastFramePowerSold.setText("" + (int) (gs.lastFramePowerSold * LD39.POWER_PRICE * 60));
		infoLastFramePowerWasted.setText("" + (int) (gs.lastFramePowerWasted * LD39.POWER_PRICE * 60));
		infoLastFrameTotalPowerStored.setText("" + (int) (gs.lastFrameTotalPowerStored * LD39.POWER_PRICE * 60));

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
