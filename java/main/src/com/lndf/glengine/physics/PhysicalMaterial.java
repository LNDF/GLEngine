package com.lndf.glengine.physics;

import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.Window;

import physx.physics.PxMaterial;

public class PhysicalMaterial implements EngineResource {
	
	private PxMaterial pxMaterial;
	
	public PhysicalMaterial(float staticFriction, float dynamicFriction, float restitution) {
		Window.addEngineResource(this);
		this.pxMaterial = PhysXManager.getPhysics().createMaterial(staticFriction, dynamicFriction, restitution);
	}
	
	public void destroy() {
		if (this.pxMaterial == null) return;
		Window.removeEngineResource(this);
		this.pxMaterial.release();
		this.pxMaterial = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Window.getWindow().addEndOfLoopRunnable(() -> this.destroy());
	}
	
	public PxMaterial getPxMaterial() {
		return pxMaterial;
	}
	
	public float getStaticFriction() {
		return this.pxMaterial.getStaticFriction();
	}
	
	public float getDynamicFriction() {
		return this.pxMaterial.getDynamicFriction();
	}
	
	public float getRestitution() {
		return this.getRestitution();
	}
	
	public void setStaticFriction(float staticFriction) {
		this.pxMaterial.setStaticFriction(staticFriction);
	}
	
	public void setDynamicFriction(float dynamicFriction) {
		this.pxMaterial.setDynamicFriction(dynamicFriction);
	}
	
	public void setRestitution(float restitution) {
		this.pxMaterial.setRestitution(restitution);
	}
}
