package com.lndf.glengine.model;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModelNode {
	
	private String name;
	private String path;
	
	private ModelNode[] children;
	
	private MeshContainer[] meshContainers;
	
	private Vector3f position;
	private Vector3f scale;
	private Quaternionf rotation;
	
	public ModelNode(String name, String path, ModelNode[] children, MeshContainer[] meshContainers, Vector3f position, Vector3f scale, Quaternionf rotation) {
		this.name = name;
		this.path = path;
		this.children = children;
		this.meshContainers = meshContainers;
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
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
