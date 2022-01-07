package com.lndf.glengine.primitives;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.components.MeshRenderer;

public class Plane extends GameObject {
	
	protected static Mesh planeObject;
	
	static {
		float[] positions = {
				 0.5f,  0.5f, 0.0f,
				 0.5f, -0.5f, 0.0f,
				-0.5f,  0.5f, 0.0f,
				-0.5f, -0.5f, 0.0f
		};
		float[] normals = {
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
		};
		float[] tangents = {
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f
		};
		float[] texCoords = {
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				0.0f, 0.0f
		};
		int[] indices = {
				2, 1, 0,
				2, 3, 1
		};
		Plane.planeObject = new Mesh(positions, normals, texCoords, tangents, indices);
	}
	
	public Plane(Material material) {
		super();
		this.addComponent(Plane.getMeshRenderer(material));
	}
	
	public Plane(String name, Material material) {
		super(name);
		this.addComponent(Plane.getMeshRenderer(material));
	}
	
	public static MeshRenderer getMeshRenderer(Material material) {
		return new MeshRenderer("plane", Plane.planeObject, material);
	}
	
}
