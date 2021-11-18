package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geomutils.PxCapsuleGeometry;
import physx.physics.PxShape;

public class CapsuleCollider extends Collider {
	
	private PxShape capsule;
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
	
	private static PxShape create(PhysicalMaterial material, GameObject obj, float radius, float height) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float r = radius, h = height;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				r *= scale.x;
				h *= (float) Math.max(scale.y, scale.z);
			}
			PxCapsuleGeometry capsuleGeom = PxCapsuleGeometry.createAt(mem, MemoryStack::nmalloc, r, h);
			PxShape capsule = PhysXManager.getPhysics().createShape(capsuleGeom, material.getPxMaterial(), true);
			capsule.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			return capsule;
		}
	}
	
	@Override
	public boolean isPxCreated() {
		return this.capsule != null;
	}
	
	@Override
	public PxShape getPhysXShape() {
		if (this.capsule == null) {
			this.capsule = CapsuleCollider.create(this.getMaterial(), this.getGameObject(), this.radius, this.height);
		}
		return this.capsule;
	}

	@Override
	public void pxDestroy() {
		if (this.capsule != null) {
			this.capsule.release();
			this.capsule = null;
		}
	}

}
