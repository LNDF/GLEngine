package lander.glengine.scene.components;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import lander.glengine.engine.Window;
import lander.glengine.gl.Drawable;
import lander.glengine.scene.Component;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.RenderComponent;
import lander.glengine.scene.Scene;

public class Camera extends Component implements Drawable {
	
	private boolean is3D;
	
	//3D only
	private float FOV;
	private float drawDistance;
	
	protected Vector3f lastPOVPos = null;
	
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
		Matrix4f modelMatrix = obj.getModelMatrix();
		Matrix4f vp = new Matrix4f();
		Vector3f point = modelMatrix.getTranslation(new Vector3f());
		Vector3f scale = modelMatrix.getScale(new Vector3f());
		AxisAngle4f rot = modelMatrix.getRotation(new AxisAngle4f());
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
		vp.scale(scale)
		  .rotate(new AxisAngle4f(-rot.angle, rot.x, rot.y, rot.z))
		  .translate(-point.x, -point.y, -point.z);
		this.lastPOVPos = point;
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
