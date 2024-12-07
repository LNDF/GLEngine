package com.lndf.glengine.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.gl.Mesh;

import physx.PxTopLevelFunctions;
import physx.common.PxBoundedData;
import physx.common.PxVec3;
import physx.cooking.PxConvexFlagEnum;
import physx.cooking.PxConvexFlags;
import physx.cooking.PxConvexMeshDesc;
import physx.geometry.PxConvexMesh;
import physx.support.PxArray_PxVec3;

public class PhysicalConvexMesh implements EngineResource	 {
	
	private PxConvexMesh mesh;
	
	public PhysicalConvexMesh(Mesh mesh) {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float[] positions = mesh.getPositions();
			PxArray_PxVec3 points = PxArray_PxVec3.createAt(mem, MemoryStack::nmalloc, positions.length / 3);
			for (int i = 0; i < positions.length; i += 3) {
				PxVec3 point = points.get(i / 3);
				point.setX(positions[i]);
				point.setY(positions[i + 1]);
				point.setZ(positions[i + 2]);
			}
			PxBoundedData pointsBounded = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			pointsBounded.setCount(points.size());
			pointsBounded.setStride(PxVec3.SIZEOF);
			pointsBounded.setData(points.begin());
			PxConvexMeshDesc desc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
			desc.setPoints(pointsBounded);
			PxConvexFlags flags = PxConvexFlags.createAt(mem, MemoryStack::nmalloc, (short) PxConvexFlagEnum.eCOMPUTE_CONVEX.value);
			desc.setFlags(flags);
			this.mesh = PxTopLevelFunctions.CreateConvexMesh(PhysXManager.getCookingParams(), desc);
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
		if (this.mesh != null) Engine.addEndOfLoopRunnable(() -> this.destroy());
	}

}
