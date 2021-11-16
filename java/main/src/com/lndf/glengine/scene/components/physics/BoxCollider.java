package com.lndf.glengine.scene.components.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;

import physx.geomutils.PxBoxGeometry;
import physx.physics.PxShape;

public class BoxCollider extends Collider {
	
	private PxShape box;
	
	public BoxCollider(PhysicalMaterial material, float sizeX, float sizeY, float sizeZ) {
		super(material);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxBoxGeometry boxGeom = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, sizeX, sizeY, sizeZ);
			this.box = PhysXManager.getPhysics().createShape(boxGeom, material.getPxMaterial(), false);
			this.box.setSimulationFilterData(PhysXManager.getDefaultFilterData());
		}
	}

	@Override
	public PxShape getPhysXShape() {
		return box;
	}

	@Override
	public void destroy() {
		box.release();
	}
	
}
