package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.physics.PhysicalTriangleMesh;
import com.lndf.glengine.scene.GameObject;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxVec3;
import physx.geometry.PxMeshScale;
import physx.geometry.PxTriangleMeshGeometry;
import physx.physics.PxShape;

public class TriangleMeshCollider extends Collider {
	
	private PhysicalTriangleMesh mesh;
	
	public TriangleMeshCollider(PhysicalMaterial material, PhysicalTriangleMesh mesh) {
		super(material);
		this.massComputable = false;
		this.mesh = mesh;
	}

	@Override
	protected void pxCreate() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			float sx = 1, sy = 1, sz = 1;
			GameObject obj = this.getGameObject();
			PhysicalMaterial material = this.material;
			if (obj != null) {
				Vector3f scale = obj.getTransform().getWorldScale();
				sx = scale.x;
				sy = scale.y;
				sz = scale.z;
			}
			PxMeshScale pxScale = PxMeshScale.createAt(mem, MemoryStack::nmalloc, PxVec3.createAt(mem, MemoryStack::nmalloc, sx, sy, sz), PxQuat.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity));
			PxTriangleMeshGeometry meshGeom = PxTriangleMeshGeometry.createAt(mem, MemoryStack::nmalloc, this.mesh.getPxMesh(), pxScale);
			PxShape mesh = PhysXManager.getPhysics().createShape(meshGeom, material.getPxMaterial());
			mesh.setSimulationFilterData(PhysXManager.getDefaultFilterData());
			this.shape = mesh;
		}
	}

}
