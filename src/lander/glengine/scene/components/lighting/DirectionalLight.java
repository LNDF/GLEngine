package lander.glengine.scene.components.lighting;

import org.joml.Vector3f;

import lander.glengine.scene.Component;

public class DirectionalLight extends Component {
	
	private Vector3f color;
	
	public DirectionalLight() {
		this.color = new Vector3f(1, 1, 1);
	}
	
	public DirectionalLight(Vector3f color) {
		this.color = color;
	}
	
	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

}
