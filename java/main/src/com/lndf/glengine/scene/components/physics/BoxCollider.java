package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geometry.PxBoxGeometry;
import physx.physics.PxShape;

public class BoxCollider extends Collider {
	
	private float x;
	private float y;
	private float z;
	
	public BoxCollider(PhysicalMaterial material) {
		this(material, 0.5f, 0.5f, 0.5f);
	}
	
	public BoxCollider(PhysicalMaterial material, float sizeX, float sizeY, float sizeZ) {
		super(material);
		this.x = sizeX;
		this.y = sizeY;
		this.z = sizeZ;
	}
	
	@Override
	protected void pxCreate() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float sx = this.x, sy = this.y, sz = this.z;
			GameObject obj = this.getGameObject();
			PhysicalMaterial material = this.material;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				sx *= scale.x;
				sy *= scale.y;
				sz *= scale.z;
			}
			PxBoxGeometry boxGeom = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, sx, sy, sz);
			PxShape box = PhysXManager.getPhysics().createShape(boxGeom, material.getPxMaterial(), true);
			box.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			this.shape = box;
		}
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		this.recreate();
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		this.recreate();
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
		this.recreate();
	}
	
}
