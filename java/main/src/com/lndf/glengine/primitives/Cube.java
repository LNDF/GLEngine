package com.lndf.glengine.primitives;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.components.MeshRenderer;

public class Cube extends GameObject {
	
	protected static Mesh cubeObject;
	
	static {
		float[] positions = {
				-0.5f,  0.5f, -0.5f,
				 0.5f,  0.5f, -0.5f,
				 0.5f,  0.5f,  0.5f,
				-0.5f,  0.5f,  0.5f,
				-0.5f, -0.5f, -0.5f,
				 0.5f, -0.5f, -0.5f,
				 0.5f, -0.5f,  0.5f,
				-0.5f, -0.5f,  0.5f,
				 0.5f,  0.5f, -0.5f,
				 0.5f, -0.5f, -0.5f,
				-0.5f, -0.5f, -0.5f,
				-0.5f,  0.5f, -0.5f,
				 0.5f,  0.5f,  0.5f,
				 0.5f, -0.5f,  0.5f,
				-0.5f, -0.5f,  0.5f,
				-0.5f,  0.5f,  0.5f,
				-0.5f,  0.5f,  0.5f,
				-0.5f, -0.5f,  0.5f,
				-0.5f, -0.5f, -0.5f,
				-0.5f,  0.5f, -0.5f,
				 0.5f,  0.5f,  0.5f,
				 0.5f, -0.5f,  0.5f,
				 0.5f, -0.5f, -0.5f,
				 0.5f,  0.5f, -0.5f
		};
		float[] normals = {
				 0.0f,  1.0f,  0.0f,
				 0.0f,  1.0f,  0.0f,
				 0.0f,  1.0f,  0.0f,
				 0.0f,  1.0f,  0.0f,
				 0.0f, -1.0f,  0.0f,
				 0.0f, -1.0f,  0.0f,
				 0.0f, -1.0f,  0.0f,
				 0.0f, -1.0f,  0.0f,
				 0.0f,  0.0f, -1.0f,
				 0.0f,  0.0f, -1.0f,
				 0.0f,  0.0f, -1.0f,
				 0.0f,  0.0f, -1.0f,
				 0.0f,  0.0f,  1.0f,
				 0.0f,  0.0f,  1.0f,
				 0.0f,  0.0f,  1.0f,
				 0.0f,  0.0f,  1.0f,
				-1.0f,  0.0f,  0.0f,
				 -1.0f,  0.0f,  0.0f,
				-1.0f,  0.0f,  0.0f,
				-1.0f,  0.0f,  0.0f,
				 1.0f,  0.0f,  0.0f,
				 1.0f,  0.0f,  0.0f,
				 1.0f,  0.0f,  0.0f,
				 1.0f,  0.0f,  0.0f
		};
		float[] texCoords = {
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
				0.0f, 1.0f,
				0.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f,
				0.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f
		};
		int[] indices = {
				2,   1,  0,
				0,   3,  2,
				4,   5,  6,
				6,   7,  4,
				8,   9, 10,
				10, 11,  8,
				14, 13, 12,
				12, 15, 14,
				18, 17, 16,
				16, 19, 18,
				20, 21, 22,
				22, 23, 20,
				24, 25, 26,
				26, 27, 24
		};
		Cube.cubeObject = new Mesh(positions, normals, texCoords, indices);
	}
	
	public Cube(Material material) {
		this.addComponent(Cube.getMeshRenderer(material));
	}
	
	public static MeshRenderer getMeshRenderer(Material material) {
		return new MeshRenderer("cube", Cube.cubeObject, material);
	}
	
}
