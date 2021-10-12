package com.lndf.glengine.tests.game;

import com.lndf.glengine.engine.Window;

public class TestMain {
	
	public static void run() {
		if (GameState.running) return;
		GameState.running = true;
		GameState.cargarPuntos("puntos.bin");
		GameState.reset();
		Window win = Window.createWindow("TEST", 500, 500, true);
		Level level = new Level(win);
		win.addDrawable(level.getCamera());
		level.subscribeToUpdates();
		win.mainLoop();
		level.destroy();
		GameState.guardarPuntos("puntos.bin");
		Window.terminate();
		GameState.running = false;
	}
	
	public static void main(String[] args) {
		run();
	}
	
}
