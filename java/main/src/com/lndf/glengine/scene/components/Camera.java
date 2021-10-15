package com.lndf.glengine.scene.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.lndf.glengine.engine.Window;
import com.lndf.glengine.gl.Drawable;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.RenderComponent;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.Transform;

public class Camera extends Component implements Drawable {
	
	private boolean is3D;
	
	//3D only
	private float FOV;
	private float drawDistance;
	
	protected Vector3f lastPOVPos = new Vector3f();
	
	public Camera() {
		this.is3D = false;
	}
	
	public Camera(float FOV, float drawDistance) {
		this.is3D = true;
		this.FOV = FOV;
		this.drawDistance = drawDistance;
	}
	
	public Matrix4f makeViewProjection(Window window) {
		GameObject obj = this.getGameObject();
		if (obj == null) return null;
		Transform t = obj.getTransform();
		Matrix4f vp = new Matrix4f();
		Quaternionf rot = t.getWorldRotation().conjugate();
		Vector3f pos = t.getWorldPosition().mul(-1);
		Vector3f scale = t.getWorldScale();
		if (this.is3D) { 
			vp.perspective(FOV, (float) window.getWidth() / (float) window.getHeight(), 0.01f, this.drawDistance);
		} else {
			if (window.getWidth() > window.getHeight()) {
				float ratio = (float) window.getWidth() / (float) window.getHeight();
				vp.setOrtho(-ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);
			} else {
				float ratio = (float) window.getHeight() / (float) window.getWidth();
				vp.setOrtho(-1.0f, 1.0f, -ratio, ratio, -1.0f, 1.0f);
			}
		}
		vp.scale(scale).rotate(rot).translate(pos);
		this.lastPOVPos = pos.mul(-1, this.lastPOVPos);
		return vp;
	}
	
	public void draw(Window window) {
		Scene current = this.getScene();
		if (current == null) return;
		Matrix4f vp = this.makeViewProjection(window);
		for (RenderComponent comp : current.getRenderComponents()) {
			comp.setGLSettings();
			comp.render(vp, this.lastPOVPos);
		}
	}
	
	public float getFOV() {
		return this.FOV;
	}
	
	public void setFOV(float FOV) {
		this.FOV = FOV;
	}
	
	public float getDrawDistance() {
		return this.drawDistance;
	}
	
	public void setDrawDistance(float drawDistance) {
		this.drawDistance = drawDistance;
	}
	
}
