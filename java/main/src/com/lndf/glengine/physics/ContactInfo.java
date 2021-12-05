package com.lndf.glengine.physics;

import org.joml.Vector3f;

public class ContactInfo {
	
	public Vector3f position;
	public Vector3f normal;
	public float distance;
	
	public ContactInfo(Vector3f position, Vector3f normal, float distance) {
		this.position = position;
		this.normal = normal;
		this.distance = distance;
	}
	
	public ContactInfo(ContactInfo contactInfo) {
		this(new Vector3f(contactInfo.position), new Vector3f(contactInfo.normal), contactInfo.distance);
	}
	
}
