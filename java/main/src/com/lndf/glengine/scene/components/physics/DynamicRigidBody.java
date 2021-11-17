package com.lndf.glengine.scene.components.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.scene.Component;

import physx.common.PxIDENTITYEnum;
import physx.common.PxTransform;
import physx.physics.PxRigidDynamic;

public class DynamicRigidBody extends Component implements EngineResource {
	
	private PxRigidDynamic rigid;
	
	public DynamicRigidBody() {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			this.rigid = PhysXManager.getPhysics().createRigidDynamic(pose);
		}
	}
	
	public void destroy() {
		if (this.rigid == null) return;
		Engine.removeEngineResource(this);
		this.rigid.release();
		this.rigid = null;
	}
	
	 @Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getPhysx().setRigidBody(rigid);
	}
	
	@Override
	public void removeFromGameObject() {
		this.getGameObject().getPhysx().unsetRigidBody();
	}
	
}
