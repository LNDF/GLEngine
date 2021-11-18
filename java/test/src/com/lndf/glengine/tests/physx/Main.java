package com.lndf.glengine.tests.physx;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.Input;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.physics.BoxCollider;
import com.lndf.glengine.scene.components.physics.DynamicRigidBody;
import com.lndf.glengine.scene.components.physics.SphereCollider;

public class Main {
	
	public static void main(String[] args) {
		Engine.createWindow("PhysX", 800, 600, true);
		Scene scene = new Scene();
		DefaultMaterial mat = new DefaultMaterial(new Vector4f(0, 0, 1, 1), new Vector4f(1, 1, 1, 1), 30);
		Cube cube = new Cube(mat);
		Cube cube2 = new Cube(mat);
		DynamicRigidBody cubeRigid = new DynamicRigidBody();
		PhysicalMaterial pMat = new PhysicalMaterial(0.5f, 0.5f, 0.5f);
		SphereCollider boxColl= new SphereCollider(pMat);
		BoxCollider boxColl2= new BoxCollider(pMat);
		FPCamera cam = new FPCamera((float) Math.PI / 4, 100);
		GameObject obj = new GameObject();
		DirectionalLight dirLight = new DirectionalLight();
		obj.addComponent(new Component() {
			
			@Override
			public void update() {
				if (Input.getKey(KeyEvent.VK_F)) {
					Vector3f s = cube.getTransform().getScale();
					s.add(new Vector3f(1,1,1));
					cube.getTransform().setScale(s);
				}
			}
			
		});
		obj.addComponent(dirLight);
		obj.addComponent(cam);
		cube.addComponent(cubeRigid);
		cube.addComponent(boxColl);
		cube2.addComponent(boxColl2);
		//cube2.getTransform().rotateArround(new Vector3f(0, 0, 1), 0.1f);
		//cube2.getTransform().rotateArround(new Vector3f(1, 0, 0), 0.2f);
		cube2.getTransform().setScale(new Vector3f(100, 1, 100));
		cube.getTransform().setPosition(new Vector3f(0, 3, -8));
		cube2.getTransform().setPosition(new Vector3f(0, -1, -8));
		Engine.addDrawable(cam);
		scene.addObject(obj);
		scene.addObject(cube);
		scene.addObject(cube2);
		scene.subscribeToUpdates();
		Engine.mainLoop();
		scene.destroy();
		mat.destroy();
		Engine.terminate();
	}
	
}
