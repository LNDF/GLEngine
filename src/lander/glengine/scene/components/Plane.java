package lander.glengine.scene.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lander.glengine.gl.Mesh;
import lander.glengine.gl.Material;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.RenderComponent;

public class Plane extends RenderComponent {
	
	private Material material;
	
	protected static Mesh planeObject;
	
	static {
		float[] vertices = {
				 0.5f,  0.5f, 0.0f,    1.0f, 1.0f,    0.0f, 0.0f, 1.0f,
				 0.5f, -0.5f, 0.0f,    1.0f, 0.0f,    0.0f, 0.0f, 1.0f,
				-0.5f,  0.5f, 0.0f,    0.0f, 1.0f,    0.0f, 0.0f, 1.0f,
				-0.5f, -0.5f, 0.0f,    0.0f, 0.0f,    0.0f, 0.0f, 1.0f
		};
		int[] indices = {
				2, 1, 0,
				2, 3, 1
		};
		Plane.planeObject = new Mesh(vertices, indices);
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
	
	@Override
	public void destroy() {
		
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void start() {
		
	}

}
