package com.lndf.glengine.scene.components.physics;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.scene.Component;

import physx.character.PxController;
import physx.physics.PxRigidActor;

public class CharacterController extends Component implements EngineResource, RigidBody {

	private PxController ctt;
	
	//FIELDS
	
	public CharacterController() {
		Engine.addEngineResource(this);
	}
	
	@Override
	public void shapeUpdated() {}
	
	@Override
	public void pxRelease() {}
	
	@Override
	public PxRigidActor getPxRigidActor() {
		if (this.ctt == null) return null;
		return this.ctt.getActor();
	}
	
	public PxController getPxCtt() {
		return this.ctt;
	}
	
	public void pxDestroy() {
		this.ctt.release();
		this.ctt = null;
	}

	@Override
	public void destroy() {
		if (this.ctt != null) {
			Engine.removeEngineResource(this);
			this.pxDestroy();
		}
	}
	
	

}
