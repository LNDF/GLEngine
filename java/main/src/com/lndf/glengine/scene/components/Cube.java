package com.lndf.glengine.scene.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;

public class Cube extends RenderComponent {
	
	private Material material;
	
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
		this.material = material;
	}
	
	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		this.material.getShader().bind();
		this.material.setUniform(vp, this.getGameObject(), pov);
		Cube.cubeObject.draw();
	}

}
