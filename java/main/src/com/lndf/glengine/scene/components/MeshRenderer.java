package com.lndf.glengine.scene.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;

public class MeshRenderer extends RenderComponent {
	
	private String name;
	private Mesh mesh;
	private Material material;
	
	public MeshRenderer() {
		
	}
	
	public MeshRenderer(String name, Mesh mesh, Material material) {
		this.name = name;
		this.mesh = mesh;
		this.material = material;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		if (this.material == null) return;
		this.material.getShader().bind();
		this.material.setUniform(vp, obj, pov);
		this.mesh.draw();
	}
	
	
	
}
