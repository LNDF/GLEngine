package com.lndf.glengine.tests.kinematic;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.FPCamera;
import com.lndf.glengine.scene.components.physics.DynamicRigidBody;

public class KinematicMain {
	
	public static void main(String[] args) {
		Engine.createWindow("Lights test", 800, 600, true);
		DefaultMaterial cubeMat = new DefaultMaterial();
		FPCamera cam = new FPCamera((float) (Math.PI / 2), 100.0f);
		Scene scene = new Scene();
		Cube c2 = new Cube(cubeMat);
		Cube c1 = new Cube(cubeMat);
		DynamicRigidBody r = new DynamicRigidBody();
		r.setKinematic(true);
		c1.addComponent(r);
		c2.getTransform().setPosition(new Vector3f(3, 0, 0));
		c1.addComponent(new Component() {
			@Override
			public void update() {
				Vector3f p = c1.getTransform().getPosition();
				Quaternionf q = c1.getTransform().getRotation();
				p.y += 0.01;
				if (p.y >= 10) {
					c1.addChild(c2);
				}
				r.setKinematicTarget(p, q);
			}
		});
		GameObject camObj = new GameObject();
		camObj.addComponent(cam);
		scene.addObject(camObj);
		scene.addObject(c1);
		scene.addObject(c2);
		scene.subscribeToUpdates();
		Engine.addDrawable(cam);
		Engine.mainLoop();
		scene.destroy();
		cubeMat.destroy();
		Engine.terminate();
	}
	
}
