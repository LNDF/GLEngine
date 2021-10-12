package com.lndf.glengine.physics.collider;

import com.lndf.glengine.scene.Component;

public abstract class Collider extends Component {
	
	public abstract boolean getCollision(Collider other);
	
}