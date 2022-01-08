package com.lndf.glengine.scene.components.lighting;

import org.joml.Vector3f;

import com.lndf.glengine.scene.Component;

public class Spotlight extends Component {
	
	private Vector3f color;
	private float radius;
	private float strength;
	private float innerCone;
	private float outerCone;
	
	
	public Spotlight() {
		this.color = new Vector3f(1, 1, 1);
		this.radius = 1;
		this.strength = 1.3f;
		this.innerCone = (float) Math.toRadians(10);
		this.outerCone = (float) Math.toRadians(15);
	}
	
	public Spotlight(Vector3f color, float radius, float strength, float innerCone, float outerCone) {
		this.color = color;
		this.radius = radius;
		this.strength = strength;
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
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	
}
