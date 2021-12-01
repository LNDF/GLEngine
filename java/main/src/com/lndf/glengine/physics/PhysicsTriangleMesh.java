package com.lndf.glengine.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.gl.Mesh;

import physx.common.PxBoundedData;
import physx.common.PxVec3;
import physx.cooking.PxTriangleMeshDesc;
import physx.geomutils.PxTriangleMesh;
import physx.geomutils.PxTriangleMeshFlagEnum;
import physx.support.Vector_PxU32;
import physx.support.Vector_PxVec3;

public class PhysicsTriangleMesh implements EngineResource {
	
	private PxTriangleMesh mesh;
	
	public PhysicsTriangleMesh(Mesh mesh) {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			Vector_PxVec3 vPoints = new Vector_PxVec3();
			Vector_PxU32 vIndices = new Vector_PxU32();
			PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc);
			float[] positions = mesh.getPositions();
			int[] indices = mesh.getIndices();
			for (int i = 0; i < positions.length; i += 3) {
				tmpVec.setX(positions[i]);
				tmpVec.setY(positions[i + 1]);
				tmpVec.setZ(positions[i + 2]);
				vPoints.push_back(tmpVec);
			}
			for (int i = 0; i < indices.length; i++) {
				vIndices.push_back(indices[i]);
			}
			PxBoundedData boundedPoints = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			boundedPoints.setCount(vPoints.size());
			boundedPoints.setStride(PxVec3.SIZEOF);
			boundedPoints.setData(vPoints.data());
			PxBoundedData boundedIndices = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
			boundedIndices.setCount(vIndices.size() / 3);
			boundedIndices.setStride(4 * 3);
			boundedIndices.setData(vIndices.data());
			PxTriangleMeshDesc meshDesc = PxTriangleMeshDesc.createAt(mem, MemoryStack::nmalloc);
			meshDesc.setPoints(boundedPoints);
			meshDesc.setTriangles(boundedIndices);
			this.mesh = PhysXManager.getCooking().createTriangleMesh(meshDesc, PhysXManager.getPhysics().getPhysicsInsertionCallback());
			vPoints.destroy();
			vIndices.destroy();
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
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}

}
