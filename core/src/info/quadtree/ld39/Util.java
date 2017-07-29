package info.quadtree.ld39;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Util {
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
		wnd.setX(400);
		wnd.setY(400);
		wnd.getContentTable().add(Util.createLabel(text)).pad(8);
		for (Button bt : buttons)
			wnd.button(bt).pad(8);
		wnd.pack();
		LD39.s.uiStage.addActor(wnd);

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