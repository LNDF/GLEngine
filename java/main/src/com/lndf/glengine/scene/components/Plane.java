package com.lndf.glengine.scene.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;

public class Plane extends RenderComponent {
	
	private Material material;
	
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
		Plane.planeObject = new Mesh(positions, normals, texCoords, indices);
	}
	
	public Plane(Material material) {
		this.material = material;
	}
	
	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		if (!this.getVisible()) return;
		this.material.getShader().bind();
		this.material.setUniform(vp, this.getGameObject(), pov);
		Plane.planeObject.draw();
	}

}
