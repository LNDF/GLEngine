package com.lndf.glengine.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.gl.Mesh;

import physx.common.PxBoundedData;
import physx.common.PxVec3;
import physx.cooking.PxConvexMeshDesc;
import physx.geomutils.PxConvexMesh;
import physx.support.Vector_PxVec3;

public class PhysicalConvexMesh implements EngineResource	 {
	
	private PxConvexMesh mesh;
	
	public PhysicalConvexMesh(Mesh mesh) {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			Vector_PxVec3 points = new Vector_PxVec3();
			PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc);
			float[] positions = mesh.getPositions();
			for (int i = 0; i < positions.length; i += 3) {
				tmpVec.setX(positions[i]);
				tmpVec.setY(positions[i + 1]);
				tmpVec.setZ(positions[i + 2]);
				points.push_back(tmpVec);
			}
			PxBoundedData pointsBounded = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			pointsBounded.setCount(points.size());
			pointsBounded.setStride(PxVec3.SIZEOF);
			pointsBounded.setData(points.data());
			PxConvexMeshDesc desc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
			desc.setPoints(pointsBounded);
			this.mesh = PhysXManager.getCooking().createConvexMesh(desc, PhysXManager.getPhysics().getPhysicsInsertionCallback());
			points.destroy();
		}
	}
	
	public PxConvexMesh getPxMesh() {
		return this.mesh;
	}
	
	@Override
	public void destroy() {
		if (this.mesh != null) {
			Engine.removeEngineResource(this);
			this.mesh.release();
			this.mesh = null;
		}
	}
	
	@Override
	protected void finalize() {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}

}
