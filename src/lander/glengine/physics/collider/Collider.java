package lander.glengine.physics.collider;

import lander.glengine.scene.Component;

public abstract class Collider extends Component {
	
	public abstract boolean getCollision(Collider other);
	
}