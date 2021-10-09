package lander.glengine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lander.glengine.gl.Material;
import lander.glengine.gl.Mesh;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.RenderComponent;

public class ModelRenderComponent extends RenderComponent {
	
	private Mesh[] meshes;
	private Material[] materials;
	
	public ModelRenderComponent(MeshContainer[] meshContainers) {
		int pos = 0;
		this.meshes = new Mesh[meshContainers.length];
		this.materials = new Material[meshContainers.length];
		for (MeshContainer container : meshContainers) {
			this.meshes[pos] = container.getMesh();
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

	@Override
	public void start() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void destroy() {
		
	}
	
	
	
}
