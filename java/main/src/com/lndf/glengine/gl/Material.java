package com.lndf.glengine.gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;

public abstract class Material {
	
	protected Shader shader;

	protected Material(Shader shader) {
		this.shader = shader;
	}
	
	public Shader getShader() {
		return this.shader;
	}
	
	public void destroy() {
		
	}
	
	protected void setVMP(Matrix4f vp, Scene scene, GameObject obj, Vector3f pov) {
		Shader shader = this.getShader();
		shader.setUniform("pov", pov.x, pov.y, pov.z);
		shader.setUniform("vp", vp);
		shader.setUniform("model", obj.getTransform().getWorldMatrix());
	}
	
	public abstract void setUniform(Matrix4f vp, GameObject obj, Vector3f pov);
	
}
