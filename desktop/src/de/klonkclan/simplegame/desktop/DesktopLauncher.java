package de.klonkclan.simplegame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.klonkclan.simplegame.SecondImpact;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "2nd Impact";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new SecondImpact(), config);
	}
}
