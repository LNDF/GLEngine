package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geometry.PxCapsuleGeometry;
import physx.physics.PxShape;

public class CapsuleCollider extends Collider {
	
	private float height;
	private float radius;
	
	public CapsuleCollider(PhysicalMaterial material, float radius, float height) {
		super(material);
		this.height = height;
		this.radius = radius;
	}
	
	public CapsuleCollider(PhysicalMaterial material) {
		this(material, 0.5f, 0.5f);
	}
	
	@Override
	protected void pxCreate() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float r = this.radius, h = this.height;
			GameObject obj = this.getGameObject();
			PhysicalMaterial material = this.material;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				r *= scale.x;
				h *= (float) Math.max(scale.y, scale.z);
			}
			PxCapsuleGeometry capsuleGeom = PxCapsuleGeometry.createAt(mem, MemoryStack::nmalloc, r, h);
			PxShape capsule = PhysXManager.getPhysics().createShape(capsuleGeom, material.getPxMaterial(), true);
			capsule.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			this.shape = capsule;
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		this.recreate();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		this.recreate();
	}

}
