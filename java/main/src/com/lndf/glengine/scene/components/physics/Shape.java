package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;

import com.lndf.glengine.scene.Component;

import physx.physics.PxShape;

public abstract class Shape extends Component {
	
	public abstract PxShape getPhysXShape();
	public abstract void destroy();
	public abstract void scaleChanged(Vector3f newScale);
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getPhysx().addShape(this);
	}
	
	@Override
	public void removeFromGameObject() {
		this.getGameObject().getPhysx().removeShape(this);
	}
	
}
