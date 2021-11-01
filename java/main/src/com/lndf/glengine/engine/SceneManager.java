package com.lndf.glengine.engine;

import java.util.HashSet;

import com.lndf.glengine.scene.Scene;

public class SceneManager {
	
	public static HashSet<Scene> scenes = new HashSet<Scene>();
	
	static {
		Window.addTerminateRunnable(() -> SceneManager.scenes.clear());
	}
	
	public static void updateScenes() {
		for (Scene scene : SceneManager.scenes) {
			scene.update();
		}
	}
	
}
