package lander.glengine.scene.components.lighting;

import org.joml.Vector3f;

import lander.glengine.scene.Component;

public class PointLight extends Component {
	
	private Vector3f color;
	private float atConstant;
	private float atLinear;
	private float atQuadratic;
	
	public PointLight() {
		this.color = new Vector3f(1, 1, 1);
		this.atConstant = 1.0f;
		this.atLinear = 0.22f;
		this.atQuadratic = 0.20f;
	}
	
	public PointLight(Vector3f color, float atConstant, float atLinear, float atQuadratic) {
		this.color = color;
		this.atConstant = atConstant;
		this.atLinear = atLinear;
		this.atQuadratic = atQuadratic;
	}

	public float getAtConstant() {
		return atConstant;
	}

	public void setAtConstant(float atConstant) {
		this.atConstant = atConstant;
	}

	public float getAtLinear() {
		return atLinear;
	}

	public void setAtLinear(float atLinear) {
		this.atLinear = atLinear;
	}

	public float getAtQuadratic() {
		return atQuadratic;
	}

	public void setAtQuadratic(float atQuadratic) {
		this.atQuadratic = atQuadratic;
	}
	
	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	
}
