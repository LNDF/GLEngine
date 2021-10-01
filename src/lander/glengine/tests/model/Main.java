package lander.glengine.tests.model;

import org.joml.Vector3f;

import lander.glengine.asset.Asset;
import lander.glengine.engine.Window;
import lander.glengine.model.Model;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.Scene;
import lander.glengine.scene.components.FPCamera;
import lander.glengine.scene.components.lighting.DirectionalLight;
import lander.glengine.scene.components.lighting.Spotlight;

public class Main {
	
	public static void main(String[] args) {
		Window win = Window.createWindow("AAA", 800, 600, true);
		Scene scene = new Scene();
		Model model = new Model(new Asset("resource:/model/model.obj"));
		GameObject camObj = new GameObject();
		FPCamera cam = new FPCamera((float) Math.PI / 2.0f, 100.0f);
		DirectionalLight light = new DirectionalLight(new Vector3f(1,1,1));
		camObj.addComponent(cam);
		camObj.addComponent(light);
		camObj.setFront(new Vector3f(0, 0, -1));
		camObj.addComponent(light);
		GameObject persona = model.createGameObject();
		scene.addObject(persona);
		scene.addObject(camObj);
		win.addDrawable(cam);
		scene.subscribeToUpdates();
		win.mainLoop();
		Window.terminate();
	}
	
}
