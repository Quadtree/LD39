package info.quadtree.ld39;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

	public static Label createLabel(String text) {
		Label ret = new Label(text, LD39.s.defaultLabelStyle);
		return ret;
	}
}
