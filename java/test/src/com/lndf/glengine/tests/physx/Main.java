package com.lndf.glengine.tests.physx;

import org.joml.Vector3f;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.physics.BoxCollider;
import com.lndf.glengine.scene.components.physics.DynamicRigidBody;

public class Main {
	
	public static void main(String[] args) {
		Engine.createWindow("PhysX", 800, 600, true);
		Scene scene = new Scene();
		DefaultMaterial mat = new DefaultMaterial();
		DefaultMaterial matCube = new DefaultMaterial();
		mat.setAlbedoColor(new Vector3f(0, 0, 1));
		mat.setRoughness(.3f);
		mat.setMetalness(.2f);
		matCube.setAlbedoColor(new Vector3f(1, 0, 0));
		matCube.setRoughness(.3f);
		matCube.setMetalness(.2f);
		Cube cube2 = new Cube(mat);
		PhysicalMaterial pMat = new PhysicalMaterial(0.5f, 0.5f, 0.5f);
		BoxCollider collbox2 = new BoxCollider(pMat);
		FPCamera cam = new FPCamera((float) Math.PI / 4, 100);
		GameObject obj = new GameObject();
		DirectionalLight dirLight = new DirectionalLight();
		for (int i = 0; i < 1000; i++) {
			Cube c = new Cube("cube" + i, matCube);
			DynamicRigidBody rb = new DynamicRigidBody();
			BoxCollider col = new BoxCollider(pMat);
			c.getTransform().setPosition(new Vector3f(50 - i * 0.1f, 2 * i + 20, -10));
			c.addComponent(rb);
			c.addComponent(col);
			scene.addObject(c);
		}
		obj.addComponent(dirLight);
		obj.addComponent(cam);
		cube2.addComponent(collbox2);
		cube2.getTransform().setScale(new Vector3f(1000, 1, 1000));
		cube2.getTransform().setPosition(new Vector3f(0, -1, -8));
		Engine.addDrawable(cam);
		scene.addObject(obj);
		scene.addObject(cube2);
		scene.subscribeToUpdates();
		Engine.mainLoop();
		scene.destroy();
		mat.destroy();
		Engine.terminate();
	}
	
}
