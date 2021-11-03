package com.lndf.glengine.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModelNode {
	
	private String name;
	
	private ModelNode[] children;
	
	private MeshContainer[] meshContainers;
	
	private Vector3f position;
	private Vector3f scale;
	private Quaternionf rotation;
	
	public ModelNode(String name, ModelNode[] children, MeshContainer[] meshContainers, Matrix4f transform) {
		this.name = name;
		this.children = children;
		this.meshContainers = meshContainers;
		this.position = transform.getTranslation(new Vector3f());
		this.scale = transform.getScale(new Vector3f());
		this.rotation = transform.getUnnormalizedRotation(new Quaternionf());
	}
	
	public String getName() {
		return name;
	}

	public ModelNode[] getChildren() {
		return children;
	}
	
	public MeshContainer[] getMeshContainers() {
		return meshContainers;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public Quaternionf getRotation() {
		return rotation;
	}
	
}
