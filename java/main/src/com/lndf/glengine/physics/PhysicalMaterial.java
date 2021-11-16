package com.lndf.glengine.physics;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.Window;

import physx.physics.PxMaterial;

public class PhysicalMaterial {
	
	private PxMaterial pxMaterial;
	
	public PhysicalMaterial(float staticFriction, float dynamicFriction, float restitution) {
		this.pxMaterial = PhysXManager.getPhysics().createMaterial(staticFriction, dynamicFriction, restitution);
	}
	
	public void destroy() {
		this.pxMaterial.release();
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
