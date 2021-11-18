package com.lndf.glengine.scene.components.physics;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;

import physx.physics.PxRigidActor;
import physx.physics.PxShape;

public abstract class Collider extends Component implements EngineResource {
	
	private PhysicalMaterial material;
	private boolean shouldScale = true;
	
	public abstract PxShape getPhysXShape();
	public abstract boolean isPxCreated();
	public abstract void pxDestroy();
	
	protected Collider(PhysicalMaterial material) {
		Engine.addEngineResource(this);
		this.material = material;
	}
	
	public PhysicalMaterial getMaterial() {
		return material;
	}
	
	public void setShouldScale(boolean shouldScale) {
		this.shouldScale = shouldScale;
	}
	
	public boolean getShouldScale() {
		return shouldScale;
	}
	
	public void recreate() {
		if (!this.isPxCreated()) return;
		GameObject obj = this.getGameObject();
		PxRigidActor rigid = null;
		if (obj != null) {
			rigid = obj.getPhysx().getPxRigid();
			rigid.detachShape(this.getPhysXShape());
		}
		this.pxDestroy();
		if (obj != null) {
			rigid.attachShape(this.getPhysXShape());
		}
	}
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getPhysx().addShape(this);
	}
	
	@Override
	public void removeFromGameObject() {
		this.getGameObject().getPhysx().removeShape(this);
	}
	
	public void destroy() {
		Engine.removeEngineResource(this);
		this.pxDestroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
}
