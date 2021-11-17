package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;

import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.Window;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.Component;

import physx.physics.PxShape;

public abstract class Collider extends Component implements EngineResource {
	
	private PhysicalMaterial material;
	
	public abstract PxShape getPhysXShape();
	public abstract void pxDestroy();
	
	public void scaleChanged(Vector3f newScale) {}
	
	protected Collider(PhysicalMaterial material) {
		Window.addEngineResource(this);
		this.material = material;
	}
	
	public PhysicalMaterial getMaterial() {
		return material;
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
		Window.removeEngineResource(this);
		this.pxDestroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		Window.getWindow().addEndOfLoopRunnable(() -> this.destroy());
	}
	
}
