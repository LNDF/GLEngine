package com.lndf.glengine.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.scene.GameObject;

import physx.common.PxIDENTITYEnum;
import physx.common.PxTransform;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidStatic;

public class StaticRigidBody implements RigidBody {
	
	private PxRigidStatic rigid;
	private GameObject object;

	public StaticRigidBody(GameObject object) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			this.rigid = PhysXManager.getPhysics().createRigidStatic(pose);
		}
		this.object = object;
		PhysXManager.addActorToGameObjectMapping(this.rigid, object);
	}

	@Override
	public PxRigidActor getPxRigidActor() {
		return this.rigid;
	}

	@Override
	public void pxRelease() {
		if (this.rigid != null) {
			PhysXManager.removeActorToGameobjectMapping(this.rigid, this.object);
			this.rigid.release();
			this.rigid = null;
		}
	}

	@Override
	public void shapeUpdated() {
		
	}
	
	
	
	
}
