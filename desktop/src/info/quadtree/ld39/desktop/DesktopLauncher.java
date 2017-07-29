package info.quadtree.ld39.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import info.quadtree.ld39.LD39;

public class DesktopLauncher {
	public static void main(String[] arg) {

		TexturePacker.processIfModified("../../raw_assets", ".", "./main");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new LD39(), config);
	}
}
