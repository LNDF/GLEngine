package com.lndf.glengine.tests.lightspbr;

import org.joml.Vector3f;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.PointLight;

public class LightsPBRMain {
	
	public static void main(String[] args) {
		Engine.createWindow("Lights test", 800, 600, true);
		DefaultMaterial cubeMat = new DefaultMaterial();
		DefaultMaterial lightMat = new DefaultMaterial();
		cubeMat.setMetalness(0.4f);
		cubeMat.setRoughness(0.4f);
		lightMat.setAlbedoColor(new Vector3f(0));
		lightMat.setEmissiveColor(new Vector3f(1));
		FPCamera cam = new FPCamera((float) (Math.PI / 2), 100.0f);
		Scene scene = new Scene();
		PointLight pl1 = new PointLight(new Vector3f(1), 8.0f, 2f);
		Cube light = new Cube(lightMat);
		Cube cube = new Cube(cubeMat);
		GameObject camObj = new GameObject();
		camObj.addComponent(cam);
		cube.getTransform().setPosition(new Vector3f(0, -9, 0));
		cube.getTransform().setScale(new Vector3f(15));
		light.addComponent(pl1);
		scene.addObject(cube);
		scene.addObject(light);
		scene.addObject(camObj);
		scene.subscribeToUpdates();
		scene.setAmbientLight(0);
		Engine.addDrawable(cam);
		Engine.mainLoop();
		scene.destroy();
		lightMat.destroy();
		cubeMat.destroy();
		Engine.terminate();
	}
	
}
