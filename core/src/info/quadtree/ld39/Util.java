package info.quadtree.ld39;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Util {
	public static Button addButtonEvent(Button b, ChangeListener list) {
		b.addListener(list);
		return b;
	}
}
