package com.lndf.glengine.tests.model;

import org.joml.Vector3f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.model.Model;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;

public class Main {
	
	public static void main(String[] args) {
		Engine.createWindow("AAA", 800, 600, true);
		Scene scene = new Scene();
		Model model = new Model(new Asset("resource:/model/defaultmodel.fbx"));
		GameObject camObj = new GameObject();
		FPCamera cam = new FPCamera((float) Math.PI / 2.0f, 1000.0f);
		DirectionalLight light = new DirectionalLight(new Vector3f(1f,1f,1f));
		camObj.addComponent(cam);
		camObj.addComponent(light);
		camObj.addComponent(light);
		GameObject persona = model.createGameObject();
		//persona.getTransform().setScale(new Vector3f(0.01f, 0.01f, 0.01f));
		scene.addObject(persona);
		scene.addObject(camObj);
		Engine.addDrawable(cam);
		scene.subscribeToUpdates();
		Engine.mainLoop();
		scene.destroy();
		Engine.terminate();
	}
	
}