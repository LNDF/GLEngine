package com.lndf.glengine.scene.components;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;

public class MeshRenderer extends RenderComponent {
	
	private HashMap<Mesh, String> meshes = new HashMap<Mesh, String>();
	private HashMap<Mesh, Material> materials = new HashMap<Mesh, Material>();
	
	public MeshRenderer() {
		
	}
	
	public MeshRenderer(String meshName, Mesh mesh, Material material) {
		this.addMesh(meshName, mesh, material);
	}
	
	public void addMesh(String meshName, Mesh mesh, Material material) {
		meshes.put(mesh, meshName);
		materials.put(mesh, material);
	}
	
	public void removeMesh(Mesh mesh) {
		meshes.remove(mesh);
		materials.remove(mesh);
	}
	
	public HashMap<Mesh, String> getMeshes() {
		return meshes;
	}
	
	public HashMap<Mesh, Material> getMaterials() {
		return materials;
	}

	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		for (Mesh mesh : this.meshes.keySet()) {
			Material material = materials.get(mesh);
			if (material == null) continue;
			material.getShader().bind();
			material.setUniform(vp, obj, pov);
			mesh.draw();
		}
	}
	
	
	
}
