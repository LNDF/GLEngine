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
	
	private HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();
	private HashMap<String, Material> materials = new HashMap<String, Material>();
	
	public MeshRenderer() {
		
	}
	
	public MeshRenderer(String meshName, Mesh mesh, Material material) {
		this.addMesh(meshName, mesh, material);
	}
	
	public void addMesh(String meshName, Mesh mesh, Material material) {
		meshes.put(meshName, mesh);
		materials.put(meshName, material);
	}
	
	public void removeMesh(String meshName) {
		meshes.remove(meshName);
		materials.remove(meshName);
	}
	
	public HashMap<String, Mesh> getMeshes() {
		return meshes;
	}
	
	public HashMap<String, Material> getMaterials() {
		return materials;
	}

	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		for (Map.Entry<String, Mesh> meshWithName : this.meshes.entrySet()) {
			String name = meshWithName.getKey();
			Mesh mesh = meshWithName.getValue();
			Material material = materials.get(name);
			if (material == null) continue;
			material.getShader().bind();
			material.setUniform(vp, obj, pov);
			mesh.draw();
		}
	}
	
	
	
}
