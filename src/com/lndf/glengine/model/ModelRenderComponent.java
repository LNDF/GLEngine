package com.lndf.glengine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;

public class ModelRenderComponent extends RenderComponent {
	
	private Mesh[] meshes;
	private Material[] materials;
	
	public ModelRenderComponent(MeshContainer[] meshContainers) {
		int pos = 0;
		this.meshes = new Mesh[meshContainers.length];
		this.materials = new Material[meshContainers.length];
		for (MeshContainer container : meshContainers) {
			Mesh mesh = container.getMesh();
			mesh.upload();
			this.meshes[pos] = mesh;
			this.materials[pos++] = container.createMaterial();
		}
	}
	
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Material[] getMaterials() {
		return materials;
	}
	
	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		for (int i = 0; i < this.meshes.length; i++) {
			Mesh mesh = this.meshes[i];
			Material material = this.materials[i];
			material.getShader().bind();
			material.setUniform(vp, obj, pov);
			mesh.draw();
		}
	}
	
	
	
}
