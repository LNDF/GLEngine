package lander.glengine.engine;

import java.util.HashSet;

import lander.glengine.scene.Scene;

public class SceneManager {
	
	public static HashSet<Scene> scenes = new HashSet<Scene>();
	
	public static void updateScenes() {
		for (Scene scene : SceneManager.scenes) {
			scene.update();
		}
	}
	
}
