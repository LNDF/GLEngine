package com.lndf.glengine.tests.physx;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lndf.glengine.engine.Window;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.physics.DynamicRigidBody;

public class Main {
	
	public static void main(String[] args) {
		Window win = Window.createWindow("PhysX", 800, 600, true);
		Scene scene = new Scene();
		DefaultMaterial mat = new DefaultMaterial(new Vector4f(0, 0, 1, 1), new Vector4f(1, 1, 1, 1), 30);
		Cube cube = new Cube(mat);
		DynamicRigidBody cubeRigid = new DynamicRigidBody();
		FPCamera cam = new FPCamera((float) Math.PI / 4, 100);
		GameObject obj = new GameObject();
		DirectionalLight dirLight = new DirectionalLight();
		obj.addComponent(dirLight);
		obj.addComponent(cam);
		cube.addComponent(cubeRigid);
		cube.getTransform().setPosition(new Vector3f(0, 30, -20));
		win.addDrawable(cam);
		scene.addObject(obj);
		scene.addObject(cube);
		scene.subscribeToUpdates();
		win.mainLoop();
		scene.destroy();
		mat.close();
		Window.terminate();
	}
	
}
