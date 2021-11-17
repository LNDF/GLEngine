package com.lndf.glengine.tests.game;

import com.lndf.glengine.engine.Engine;

public class TestMain {
	
	public static void run() {
		if (GameState.running) return;
		GameState.running = true;
		GameState.cargarPuntos("puntos.bin");
		GameState.reset();
		Engine.createWindow("TEST", 500, 500, true);
		Level level = new Level();
		Engine.addDrawable(level.getCamera());
		level.subscribeToUpdates();
		Engine.mainLoop();
		level.destroy();
		GameState.guardarPuntos("puntos.bin");
		Engine.terminate();
		GameState.running = false;
	}
	
	public static void main(String[] args) {
		run();
	}
	
}
