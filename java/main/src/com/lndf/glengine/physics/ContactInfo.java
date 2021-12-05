package com.lndf.glengine.physics;

import org.joml.Vector3f;

public class ContactInfo {
	
	private Vector3f position;
	private Vector3f normal;
	private float distance;
	
	public ContactInfo(Vector3f position, Vector3f normal, float distance) {
		this.position = position;
		this.normal = normal;
		this.distance = distance;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(this.position);
	}
	
	public Vector3f getNormal() {
		return new Vector3f(this.normal);
	}
	
	public float getDistance() {
		return this.distance;
	}
	
}
