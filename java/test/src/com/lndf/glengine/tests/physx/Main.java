package com.lndf.glengine.tests.physx;

import org.joml.Vector3f;
import org.joml.Vector4f;

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

import physx.physics.PxRigidDynamicLockFlagEnum;

public class Main {
	
	public static void main(String[] args) {
		Engine.createWindow("PhysX", 800, 600, true);
		Scene scene = new Scene();
		DefaultMaterial mat = new DefaultMaterial(new Vector4f(0, 0, 1, 1), new Vector4f(1, 1, 1, 1), 30);
		Cube cube = new Cube(mat);
		Cube cube2 = new Cube(mat);
		DynamicRigidBody cubeRigid = new DynamicRigidBody();
		PhysicalMaterial pMat = new PhysicalMaterial(0.5f, 0.5f, 0.5f);
		BoxCollider boxColl= new BoxCollider(pMat);
		BoxCollider boxColl2= new BoxCollider(pMat);
		FPCamera cam = new FPCamera((float) Math.PI / 4, 100);
		GameObject obj = new GameObject();
		DirectionalLight dirLight = new DirectionalLight();
		obj.addComponent(dirLight);
		obj.addComponent(cam);
		cube.addComponent(cubeRigid);
		cube.addComponent(boxColl);
		cube2.addComponent(boxColl2);
		cube2.getTransform().setScale(new Vector3f(100, 1, 100));
		cube.getTransform().setPosition(new Vector3f(0, 200.5f, -8));
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
