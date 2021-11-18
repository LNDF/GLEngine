package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geomutils.PxSphereGeometry;
import physx.physics.PxShape;

public class SphereCollider extends Collider {
	
	private PxShape sphere;
	private float radius;
	
	public SphereCollider(PhysicalMaterial material, float radius) {
		super(material);
		this.radius = radius;
	}
	
	public SphereCollider(PhysicalMaterial material) {
		this(material, 0.5f);
	}
	
	private static PxShape create(PhysicalMaterial material, GameObject obj, float radius) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float r = radius;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				r *= (float) Math.max(scale.x, Math.max(scale.y, scale.z));
			}
			PxSphereGeometry sphereGeom = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, r);
			PxShape sphere = PhysXManager.getPhysics().createShape(sphereGeom, material.getPxMaterial(), true);
			sphere.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			return sphere;
		}
	}
	
	@Override
	public boolean isPxCreated() {
		return this.sphere != null;
	}
	
	@Override
	public PxShape getPhysXShape() {
		if (this.sphere == null) {
			this.sphere = SphereCollider.create(this.getMaterial(), this.getGameObject(), this.radius);
		}
		return this.sphere;
	}

	@Override
	public void pxDestroy() {
		if (this.sphere != null) {
			this.sphere.release();
			this.sphere = null;
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
