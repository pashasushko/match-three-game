package com.pavelsuhko.matchthree.desktop;

import com.badlogic.gdx.Files;
import com.pavelsuhko.matchthree.MatchThree;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowIcon(Files.FileType.Internal, "icon.png");
		config.setTitle("Match Three");
		config.setWindowedMode(960, 720);
		new Lwjgl3Application(new MatchThree(), config);
	}

}
