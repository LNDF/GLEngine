package com.lndf.glengine.scene.components.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.Window;
import com.lndf.glengine.scene.Component;

import physx.common.PxIDENTITYEnum;
import physx.common.PxTransform;
import physx.physics.PxRigidDynamic;

public class DynamicRigidBody extends Component {
	
	private PxRigidDynamic rigid;
	
	public DynamicRigidBody() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			this.rigid = PhysXManager.getPhysics().createRigidDynamic(pose);
		}
	}
	
	public void destroy() {
		this.rigid.release();
	}
	
	 @Override
	protected void finalize() throws Throwable {
		Window.getWindow().addEndOfLoopRunnable(new Runnable() {
			@Override
			public void run() {
				destroy();
			}
		});
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
