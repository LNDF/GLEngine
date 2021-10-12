package com.lndf.glengine.engine;

import java.util.HashSet;

import com.lndf.glengine.scene.Scene;

public class SceneManager {
	
	public static HashSet<Scene> scenes = new HashSet<Scene>();
	
	public static void updateScenes() {
		for (Scene scene : SceneManager.scenes) {
			scene.update();
		}
	}
	
}
