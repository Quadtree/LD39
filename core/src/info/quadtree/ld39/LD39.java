package info.quadtree.ld39;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
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
	public final static int GROSS_INCOME_TO_WIN = 12000;
	public final static float POWER_PRICE = .25f;
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

	public TextTooltipStyle defaultTooltipStyle;

	boolean gameStarted = false;

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
	Button titleButton;

	Image titleImage;

	public Stage uiStage;

	public Table uiTable;

	public long updatesDone = 0;

	@Override
	public void create() {
		LD39.s = this;

		Music mainMusic = Gdx.audio.newMusic(Gdx.files.internal("ld39.ogg"));
		mainMusic.setLooping(true);
		mainMusic.setVolume(0.25f);
		mainMusic.play();

		TooltipManager.getInstance().initialTime = 0.5f;
		TooltipManager.getInstance().hideAll();

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		shapeRnd = new ShapeRenderer();

		mainFont = new BitmapFont(Gdx.files.internal("teko18.fnt"));

		defaultLabelStyle = new LabelStyle(mainFont, Color.WHITE);
		defaultDialogStyle = new WindowStyle(mainFont, Color.WHITE, new NinePatchDrawable(atlas.createPatch("dialog1")));
		defaultButtonStyle = new TextButtonStyle(new NinePatchDrawable(atlas.createPatch("dialog3")), new NinePatchDrawable(atlas.createPatch("dialog2")), new NinePatchDrawable(atlas.createPatch("dialog3")), mainFont);
		defaultTooltipStyle = new TextTooltipStyle(defaultLabelStyle, new NinePatchDrawable(atlas.createPatch("dialog1")));

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

		final int rghColWidth = 75;

		Table infoLabels = new Table();
		String txt = null;
		txt = "Your current money.";
		infoLabels.add(Util.createLabel("$", txt)).pad(4);
		infoLabels.add(infoMoney = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		txt = "Your gross income per minute, compared\nto the amount needed to win.";
		infoLabels.add(Util.createLabel("$/min", txt)).pad(4);
		infoLabels.add(infoGrossIncome = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		txt = "Power generated per second by all\nyour generators.";
		infoLabels.add(Util.createLabel("P/s Genr", txt)).pad(4);
		infoLabels.add(infoLastFramePowerGenerated = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		txt = "Power lost in transit per second.\nConsider shortening wires.";
		infoLabels.add(Util.createLabel("P/s Lost", txt)).pad(4);
		infoLabels.add(infoLastFramePowerWasted = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		txt = "Power sold per second.";
		infoLabels.add(Util.createLabel("P/s Sold", txt)).pad(4);
		infoLabels.add(infoLastFramePowerSold = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		txt = "Total power stored in all your batteries.";
		infoLabels.add(Util.createLabel("P Stored", txt)).pad(4);
		infoLabels.add(infoLastFrameTotalPowerStored = Util.createLabel("", txt)).width(rghColWidth).pad(4).row();

		rightPaneTable.add(infoLabels).align(Align.top).fill().top().row();

		rightPaneTable.add().height(55).row();

		Table controlButtons = new Table();

		Button b2 = null;

		controlButtons.add(b2 = Util.createButton("Sell", new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gs.sellMode = !gs.sellMode;
			}
		})).pad(2);
		b2.addListener(new TextTooltip("Click this then mouseover a building to\nsell it.\nHotkey=S", defaultTooltipStyle));

		controlButtons.add(b2 = Util.createButton("Restart", new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				restart();
			}
		})).pad(2);
		b2.addListener(new TextTooltip("Restarts the level.", defaultTooltipStyle));

		rightPaneTable.add(controlButtons).row();

		Table buyButtons = new Table();

		buyButtons.add(createBuyButton("Wire", "1", "Transfers power between buildings, but there\nis a small amount lost in transit.", new BuildingFactory() {
			@Override
			public Building create() {
				return new PowerLine();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Battery", "2", "Stores " + (int) (new Battery().getMaxPower() * LD39.POWER_PRICE) + " power.", new BuildingFactory() {
			@Override
			public Building create() {
				return new Battery();
			}
		})).pad(6).fill().row();
		buyButtons.add(
				createBuyButton("Surge Protector", "3", "Power surges cannot cross one of these,\nbut " + (100 - (int) (new SurgeProtector().getRetained() * 100)) + "% of power passing through is lost.", new BuildingFactory() {
					@Override
					public Building create() {
						return new SurgeProtector();
					}
				})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Solar Plant", "4", "Generates " + (int) (new SolarPlant().getNetPower() * 60 * LD39.POWER_PRICE) + " power per\nsecond, but only during the day.", new BuildingFactory() {
			@Override
			public Building create() {
				return new SolarPlant();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Geothermal Plant", "5", "Generates " + (int) (new HydrocarbonPlant().getNetPower() * 60 * LD39.POWER_PRICE) + " power per\nsecond, but must be placed on a vent.", new BuildingFactory() {
			@Override
			public Building create() {
				return new HydrocarbonPlant();
			}
		})).pad(6).fill().row();
		buyButtons.add(createBuyButton("Fusion Plant", "6",
				"Generates " + (int) (new FusionPlant().getBaseNetPower() * 60 * LD39.POWER_PRICE) + " power per\nsecond, but costs " + (int) (new FusionPlant().getFuelCost() * 60) + " per second for fuel.",
				new BuildingFactory() {
					@Override
					public Building create() {
						return new FusionPlant();
					}
				})).pad(6).fill().row();

		rightPaneTable.add(buyButtons);

		uiStage.addActor(rightPaneTable);

		titleImage = new Image(new Texture(Gdx.files.internal("title.png")));
		titleButton = Util.createButton("Start", new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				titleImage.remove();
				titleButton.remove();

				Util.createHelpText(
						"Welcome to your new posting! Your job will be to provide power to this\n" +
								"off-world colony by building power plants and stringing wires to the buildings.\n" +
								"Don't let the colony run out of power!\n" +
								"You win when your gross income per minute reaches "
								+ LD39.GROSS_INCOME_TO_WIN + ".",
						new Vector2(150, 400), true).addListener(new EventListener() {

							@Override
							public boolean handle(Event event) {
								if (event instanceof ChangeEvent) {
									gameStarted = true;
									return false;
								}
								return false;
							}
						});
			}
		});
		titleButton.setSize(200, 40);
		titleButton.setPosition(Gdx.graphics.getWidth() / 2, 60, Align.center);
		uiStage.addActor(titleImage);
		uiStage.addActor(titleButton);

		updatesDone = System.currentTimeMillis() / 16;
	}

	Button createBuyButton(String text, String hotkey, String tooltip, final BuildingFactory fact) {
		Button buyButton1 = new Button(defaultButtonStyle);
		buyButton1.add(Util.createLabel(text + " (" + hotkey + ")")).row();
		Image img = new Image(new TextureRegionDrawable(atlas.createSprite(fact.create().getGraphic())));
		buyButton1.add(img).width(32).height(32).pad(2).row();
		buyButton1.add(Util.createLabel("$" + fact.create().getCost())).row();
		buyButton1.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				LD39.s.gs.setHeldBuilding(fact.create());
			}
		});
		buyButton1.addListener(new TextTooltip(tooltip + "\nHotkey=" + hotkey, defaultTooltipStyle));

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
			if (gameStarted)
				gs.update();
			updatesDone++;
		}

		infoMoney.setText("" + (int) gs.money);
		if (gs.getGrossIncome() < 1000) {
			infoGrossIncome.setText((int) gs.getGrossIncome() + "/" + LD39.GROSS_INCOME_TO_WIN);
		} else {
			infoGrossIncome.setText(((int) gs.getGrossIncome() / 1000) + "K/" + (LD39.GROSS_INCOME_TO_WIN / 1000) + "K");
		}

		infoLastFramePowerGenerated.setText("" + (int) (gs.lastFramePowerGenerated * LD39.POWER_PRICE * 60));
		infoLastFramePowerSold.setText("" + (int) (gs.lastFramePowerSold * LD39.POWER_PRICE * 60));
		infoLastFramePowerWasted.setText("" + (int) (gs.lastFramePowerWasted * LD39.POWER_PRICE * 60));

		int storedAmt = (int) (gs.lastFrameTotalPowerStored * LD39.POWER_PRICE);

		if (storedAmt < 1000)
			infoLastFrameTotalPowerStored.setText("" + storedAmt);
		else if (gs.lastFrameTotalPowerStored < 1000000)
			infoLastFrameTotalPowerStored.setText("" + (storedAmt / 1000) + "K");
		else
			infoLastFrameTotalPowerStored.setText("" + (storedAmt / 1000000) + "M");

		// Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Matrix4 m4 = new Matrix4();
		// m4.setToOrtho(0, 1024, 768, 0, 0, 1);
		// batch.setProjectionMatrix(m4);
		// batch.begin();

		gs.render();

		uiStage.act();
		uiStage.draw();

		// batch.end();

	}

	public void restart() {
		multiplexer.removeProcessor(gs);
		gs = new GameState();
		multiplexer.addProcessor(gs);
	}
}
