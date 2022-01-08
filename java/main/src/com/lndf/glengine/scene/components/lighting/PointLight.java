package com.lndf.glengine.scene.components.lighting;

import org.joml.Vector3f;

import com.lndf.glengine.scene.Component;

public class PointLight extends Component {
	
	private Vector3f color;
	private float radius;
	private float strength;
	
	public PointLight() {
		this.color = new Vector3f(1);
		this.radius = 1;
		this.strength = 1.3f;
	}
	
	public PointLight(Vector3f color, float radius, float strength) {
		this.color = color;
		this.radius = radius;
		this.strength = strength;
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
