package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.GameObject;

import physx.geomutils.PxBoxGeometry;
import physx.physics.PxShape;

public class BoxCollider extends Collider {
	
	private PxShape box;
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
	
	private static PxShape create(PhysicalMaterial material, GameObject obj, float x, float y, float z) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float sx = x, sy = y, sz = z;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				sx *= scale.x;
				sy *= scale.y;
				sz *= scale.z;
			}
			PxBoxGeometry boxGeom = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, sx, sy, sz);
			PxShape box = PhysXManager.getPhysics().createShape(boxGeom, material.getPxMaterial(), true);
			box.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			return box;
		}
	}
	
	@Override
	public PxShape getPhysXShape() {
		if (this.box == null) {
			this.box = BoxCollider.create(this.getMaterial(), this.getGameObject(), x, y, z);
		}
		return this.box;
	}

	@Override
	public void pxDestroy() {
		if (box != null) box.release();
	}
	
	@Override
	public void scale() {
		if (this.box != null) box.release();
		this.box = BoxCollider.create(this.getMaterial(), this.getGameObject(), x, y, z);
	}
	
}
