package lander.glengine.scene.components.lighting;

import org.joml.Vector3f;

import lander.glengine.scene.Component;

public class Spotlight extends Component {
	
	private Vector3f color;
	private float atConstant;
	private float atLinear;
	private float atQuadratic;
	private float innerCone;
	private float outerCone;
	
	
	public Spotlight() {
		this.color = new Vector3f(1, 1, 1);
		this.atConstant = 1.0f;
		this.atLinear = 0.22f;
		this.atQuadratic = 0.20f;
		this.innerCone = (float) Math.toRadians(10);
		this.outerCone = (float) Math.toRadians(15);
	}
	
	public Spotlight(Vector3f color, float atConstant, float atLinear, float atQuadratic, float innerCone, float outerCone) {
		this.color = color;
		this.atConstant = atConstant;
		this.atLinear = atLinear;
		this.atQuadratic = atQuadratic;
		this.innerCone = innerCone;
		this.outerCone = outerCone;
	}

	public float getInnerCone() {
		return innerCone;
	}

	public void setInnerCone(float innerCone) {
		this.innerCone = innerCone;
	}

	public float getOuterCone() {
		return outerCone;
	}

	public void setOuterCone(float outerCone) {
		this.outerCone = outerCone;
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

	@Override
	public void start() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void destroy() {
		
	}
	
}
