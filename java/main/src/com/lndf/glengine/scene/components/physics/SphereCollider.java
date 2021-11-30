package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geomutils.PxSphereGeometry;
import physx.physics.PxShape;

public class SphereCollider extends Collider {
	
	private float radius;
	
	public SphereCollider(PhysicalMaterial material, float radius) {
		super(material);
		this.radius = radius;
	}
	
	public SphereCollider(PhysicalMaterial material) {
		this(material, 0.5f);
	}
	
	@Override
	protected void pxCreate() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float r = this.radius;
			GameObject obj = this.getGameObject();
			PhysicalMaterial material = this.material;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				r *= (float) Math.max(scale.x, Math.max(scale.y, scale.z));
			}
			PxSphereGeometry sphereGeom = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, r);
			PxShape sphere = PhysXManager.getPhysics().createShape(sphereGeom, material.getPxMaterial(), true);
			sphere.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			this.shape = sphere;
		}
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		this.recreate();
	}
	
}
