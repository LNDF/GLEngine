package lander.glengine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lander.glengine.gl.Material;
import lander.glengine.gl.Mesh;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.RenderComponent;

public class ModelRenderComponent extends RenderComponent {
	
	private Mesh mesh;
	private Material material;
	
	public ModelRenderComponent(Mesh mesh, Material material) {
		this.mesh = mesh;
		this.material = material;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	@Override
	public void render(Matrix4f vp, Vector3f pov) {
		if (!this.getVisible()) return;
		GameObject obj = this.getGameObject();
		if (obj == null) return;
		this.material.getShader().bind();
		this.material.setUniform(vp, obj, pov);
		this.mesh.draw();
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
