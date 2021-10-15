package com.lndf.glengine.tests.physics;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Input;
import com.lndf.glengine.engine.Window;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.Cube;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.Plane;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;

public class Main {
	
	public static void main(String[] args) {
		Window win = Window.createWindow("Lights test", 800, 600, true);
		Texture2D boxTexture = new Texture2D(new Asset("resource:/lights/box.png"));
		Texture2D boxTextureSpecular = new Texture2D(new Asset("resource:/lights/box_specular.png"));
		DefaultMaterial material = new DefaultMaterial(boxTexture, boxTextureSpecular, 80);
		FPCamera cam = new FPCamera((float) (Math.PI / 2), 100.0f);
		Plane plane = new Plane(material);
		Cube cube = new Cube(material);
		Scene scene = new Scene();
		DirectionalLight dlight = new DirectionalLight();
		GameObject camObj = new GameObject();
		GameObject lowPlane = new GameObject();
		GameObject dirObj = new GameObject();
		GameObject cubeObj = new GameObject();
		camObj.addComponent(cam);
		lowPlane.addComponent(plane);
		dirObj.addComponent(dlight);
		cubeObj.addComponent(cube);
		cubeObj.addComponent(new RigidBody(10, new Vector3f(0, -1, 0)));
		lowPlane.getTransform().getRotation().rotateX(3 * (float) Math.PI / 2);
		lowPlane.getTransform().setPosition(new Vector3f(0, -1, 0));
		lowPlane.getTransform().setScale(new Vector3f(100, 100, 1));
		dirObj.getTransform().getRotation().rotateX(3 * (float) Math.PI / 2);
		cubeObj.getTransform().setPosition(new Vector3f(0, 10, -15));
		scene.addObject(camObj);
		scene.addObject(lowPlane);
		scene.addObject(dirObj);
		scene.addObject(cubeObj);
		scene.subscribeToUpdates();
		scene.setAmbientLight(0.05f);
		camObj.addComponent(new Component() {
			
			@Override
			public void update() {
				if (Input.getKey(KeyEvent.VK_E)) {
					RigidBody rb = (RigidBody) cubeObj.getComponent(RigidBody.class);
					rb.addForce(new Vector3f(0, 10, 0));
				}
			}
			
		});
		win.addDrawable(cam);
		win.mainLoop();
		material.close();
		boxTexture.close();
		boxTextureSpecular.close();
		scene.destroy();
		Window.terminate();
	}
	
}
