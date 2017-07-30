package info.quadtree.ld39;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Util {
	private static final String NO_HINTS = "NO_HINTS";
	private static final String PREF_NAME = "helpText";

	public static Button addButtonEvent(Button b, ChangeListener list) {
		b.addListener(list);
		return b;
	}

	public static Button createButton(String text, ChangeListener list) {
		TextButton ret = new TextButton(text, LD39.s.defaultButtonStyle);
		ret.addListener(list);
		ret.getCells().get(0).padLeft(5).padRight(5).padTop(0).padBottom(0);
		return ret;
	}

	public static Dialog createDialog(String text, Button... buttons) {
		final Dialog wnd = new Dialog("", LD39.s.defaultDialogStyle);
		// wnd.setSize(300, 300);

		wnd.getContentTable().add(Util.createLabel(text)).pad(8);
		for (Button bt : buttons)
			wnd.button(bt).pad(8);
		wnd.pack();
		wnd.setPosition(Gdx.graphics.getWidth() / 2.f - (int) wnd.getWidth() / 2, Gdx.graphics.getHeight() / 2.f - (int) wnd.getHeight() / 2);
		LD39.s.uiStage.addActor(wnd);

		return wnd;
	}

	public static Dialog createHelpText(String text, Vector2 location) {
		final Dialog wnd = new Dialog("", LD39.s.defaultDialogStyle);
		// wnd.setSize(300, 300);

		if (Gdx.app.getPreferences(PREF_NAME).getInteger(text, 0) == 0 && Gdx.app.getPreferences(PREF_NAME).getInteger(NO_HINTS, 0) == 0) {
			Gdx.app.getPreferences(PREF_NAME).putInteger(text, 1);
			Gdx.app.getPreferences(PREF_NAME).flush();

			List<Button> buttons = new ArrayList<Button>();

			buttons.add(Util.createButton("Close", new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Util.getParentDialog(actor).remove();
				}
			}));

			buttons.add(Util.createButton("Hide All", new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Util.getParentDialog(actor).remove();
					Gdx.app.getPreferences(PREF_NAME).putInteger(NO_HINTS, 1);
				}
			}));

			wnd.getContentTable().add(Util.createLabel(text)).pad(8);
			for (Button bt : buttons)
				wnd.button(bt).pad(8);
			wnd.pack();
			wnd.setPosition(location.x, location.y);
			LD39.s.uiStage.addActor(wnd);
		}

		return wnd;
	}

	public static Label createLabel(String text) {
		Label ret = new Label(text, LD39.s.defaultLabelStyle);
		return ret;
	}

	public static Dialog getParentDialog(Actor actor) {
		Actor a = actor;
		while (a != null && !(a instanceof Dialog)) {
			a = a.getParent();
		}

		if (a instanceof Dialog)
			return (Dialog) a;
		return null;
	}
}
