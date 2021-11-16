package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;

import com.lndf.glengine.engine.Window;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.scene.Component;

import physx.physics.PxShape;

public abstract class Shape extends Component {
	
	private PhysicalMaterial material;
	
	public abstract PxShape getPhysXShape();
	public abstract void destroy();
	public abstract void scaleChanged(Vector3f newScale);
	
	private Shape(PhysicalMaterial material) {
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
	
	@Override
	protected void finalize() throws Throwable {
		Window.getWindow().addEndOfLoopRunnable(new Runnable() {
			@Override
			public void run() {
				destroy();
			}
		});
	}
	
}
