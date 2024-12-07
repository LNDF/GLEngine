package com.lndf.glengine.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.gl.Mesh;

import physx.PxTopLevelFunctions;
import physx.common.PxBoundedData;
import physx.common.PxVec3;
import physx.cooking.PxTriangleMeshDesc;
import physx.geometry.PxTriangleMesh;
import physx.support.PxArray_PxU32;
import physx.support.PxArray_PxVec3;

public class PhysicalTriangleMesh implements EngineResource {
	
	private PxTriangleMesh mesh;
	
	public PhysicalTriangleMesh(Mesh mesh) {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float[] positions = mesh.getPositions();
			int[] indices = mesh.getIndices();
			PxArray_PxVec3 vPoints = PxArray_PxVec3.createAt(mem, MemoryStack::nmalloc, positions.length / 3);
			PxArray_PxU32 vIndices = PxArray_PxU32.createAt(mem, MemoryStack::nmalloc, indices.length);
			for (int i = 0; i < positions.length; i += 3) {
				PxVec3 point = vPoints.get(i / 3);
				point.setX(positions[i]);
				point.setY(positions[i + 1]);
				point.setZ(positions[i + 2]);
			}
			for (int i = 0; i < indices.length; i++) {
				vIndices.pushBack(indices[i]);
			}
			PxBoundedData boundedPoints = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			boundedPoints.setCount(vPoints.size());
			boundedPoints.setStride(PxVec3.SIZEOF);
			boundedPoints.setData(vPoints.begin());
			PxBoundedData boundedIndices = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			boundedIndices.setCount(vIndices.size() / 3);
			boundedIndices.setStride(4 * 3);
			boundedIndices.setData(vIndices.begin());
			PxTriangleMeshDesc meshDesc = PxTriangleMeshDesc.createAt(mem, MemoryStack::nmalloc);
			meshDesc.setPoints(boundedPoints);
			meshDesc.setTriangles(boundedIndices);
			this.mesh = PxTopLevelFunctions.CreateTriangleMesh(PhysXManager.getCookingParams(), meshDesc);
		}
	}
	
	public PxTriangleMesh getPxMesh() {
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
