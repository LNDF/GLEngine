package com.lndf.glengine.tests.lights;

import org.joml.Vector3f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.gl.texture.TextureImage2D;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.lighting.PointLight;
import com.lndf.glengine.scene.components.lighting.Spotlight;

public class LightsMain {
	
	public static void main(String[] args) {
		Engine.createWindow("Lights test", 800, 600, true);
		TextureImage2D boxTexture = new TextureImage2D(new Asset("resource:/lights/box.png"));
		TextureImage2D boxTextureSpecular = new TextureImage2D(new Asset("resource:/lights/box_specular.png"));
		DefaultMaterial material = new DefaultMaterial();
		material.setAlbedoTexture(boxTexture);
		material.setRoughnessTexture(boxTextureSpecular);
		material.setMetalness(1f);
		FPCamera cam = new FPCamera((float) (Math.PI / 2), 100.0f);
		Scene scene = new Scene();
		Spotlight pl = new Spotlight(new Vector3f(1,1,1), 1.0f, 0.35f, 0.44f, (float) Math.toRadians(25), (float) Math.toRadians(35));
		PointLight pl1 = new PointLight(new Vector3f(1, 0, 0), 1.0f, 0.07f, 0.017f);
		DirectionalLight pl2 = new DirectionalLight(new Vector3f(0, 0.3f, 0)) {
			@Override
			public void update() {
				GameObject obj = this.getGameObject();
				obj.getTransform().getRotation().rotateAxis((float) DeltaTime.get(), 1, 0, 0);
			}
		};
		GameObject camObj = new GameObject();
		GameObject pl1Obj = new GameObject();
		GameObject pl2Obj = new GameObject();
		GameObject pl1CObj = new GameObject();
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < 10; k++) {
				for (int i = 0; i < 10; i++) {
					Cube boxObj = new Cube(material);
					boxObj.getTransform().setPosition(new Vector3f(i * 3, k * 4, j * 3));
					boxObj.getTransform().rotateEuler(new Vector3f((float) Math.toRadians(i * 10), (float) Math.toRadians(j * k), (float) Math.toRadians(k * i)));
					scene.addObject(boxObj);
				}
			}
		}
		camObj.addComponent(cam);
		camObj.addComponent(pl);
		pl1CObj.addChild(pl1Obj);
		pl1CObj.addComponent(new Component() {
			
			@Override
			public void update() {
				GameObject obj = this.getGameObject();
				obj.getTransform().setRotation(obj.getTransform().getRotation().rotateAxis((float) DeltaTime.get(), 1, 0, 0));
			}
			
		});
		pl1CObj.getTransform().setPosition(new Vector3f(10, 10, 10));
		pl1Obj.addComponent(pl1);
		pl1Obj.getTransform().setPosition(new Vector3f(3, 3, 3));
		pl2Obj.addComponent(pl2);
		pl2Obj.getTransform().rotateEuler(new Vector3f((float) Math.PI / 6, (float) Math.PI / 3, 0));
		camObj.getTransform().setPosition(new Vector3f(0, 0, 1));
		scene.subscribeToUpdates();
		scene.addObject(pl1Obj);
		scene.addObject(pl2Obj);
		scene.addObject(camObj);
		scene.addObject(pl1CObj);
		Engine.addDrawable(cam);
		Engine.mainLoop();
		material.destroy();
		boxTexture.destroy();
		boxTextureSpecular.destroy();
		scene.destroy();
		Engine.terminate();
	}
	
}
