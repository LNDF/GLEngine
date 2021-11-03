package com.lndf.glengine.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private Quaternionf rotation = new Quaternionf(0, 0, 0, 1);
	private Vector3f scale = new Vector3f(1, 1, 1);
	
	//old
	private Vector3f oldPosition = new Vector3f(0, 0, 0);
	private Quaternionf oldRotation = new Quaternionf(0, 0, 0, 1);
	private Vector3f oldScale = new Vector3f(1, 1, 1);
	
	private Matrix4f transformation = null;
	
	private GameObject obj;
	
	//for rotation
	private static final Vector3f FRONT = new Vector3f(0, 0, -1);
	private static final Vector3f RIGHT = new Vector3f(1, 0, 0);
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	
	public Transform(GameObject obj) {
		if (obj == null) throw new RuntimeException("GameObject is null");
		this.obj = obj;
	}
	
	public void checkCache() {
		if (!this.position.equals(this.oldPosition) ||
			!this.rotation.equals(this.oldRotation) ||
			!this.scale.equals(this.oldScale)) {
			this.oldPosition = new Vector3f(this.position);
			this.oldRotation = new Quaternionf(this.rotation);
			this.oldScale = new Vector3f(this.scale);
			this.clearCache();
		}
	}
	
	public void clearCache() {
		this.transformation = null;
	}
	
	public Matrix4f getTransformation() {
		this.checkCache();
		if (this.transformation != null) return new Matrix4f(this.transformation);
		Matrix4f base = new Matrix4f().identity();
		base.translate(this.position).rotate(this.rotation).scale(this.scale);
		this.transformation = new Matrix4f(base);
		return base;
	}
	
	public Matrix4f getWorldTransformation() {
		Matrix4f parent = this.getParentTransformation();
		Matrix4f t = this.getTransformation();
		if (parent != null) {
			Matrix4f p = new Matrix4f(parent);
			p.mul(t, t);
		}
		return t;
	}
	private Transform getParentTransform() {
		if (obj.getParent() == null) return null;
		return obj.getParent().getTransform();
	}
	
	public Quaternionf getWorldRotation() {
		Quaternionf q = new Quaternionf(this.rotation);
		Transform t = this.getParentTransform();
		if (t != null) t.getWorldRotation().mul(q, q);
		return q;
	}
	
	public Vector3f getWorldPosition() {
		return this.getWorldTransformation().getTranslation(new Vector3f());
	}
	
	public Vector3f getWorldScale() {
		return this.getWorldTransformation().getScale(new Vector3f());
	}
	
	private Matrix4f getParentTransformation() {
		Transform t = this.getParentTransform();
		if (t == null) return null;
		return t.getWorldTransformation();
	}
	
	public Vector3f getFront() {
		return this.getTransformation().transformDirection(new Vector3f(FRONT));
	}
	
	public Vector3f getRight() {
		return this.getTransformation().transformDirection(new Vector3f(RIGHT));
	}
	
	public Vector3f getUp() {
		return this.getTransformation().transformDirection(new Vector3f(UP));
	}
	
	public Vector3f getBack() {
		return this.getTransformation().transformDirection(new Vector3f(FRONT).mul(-1));
	}
	
	public Vector3f getLeft() {
		return this.getTransformation().transformDirection(new Vector3f(RIGHT).mul(-1));
	}
	
	public Vector3f getDown() {
		return this.getTransformation().transformDirection(new Vector3f(UP).mul(-1));
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		if (position == null) position = new Vector3f(0, 0, 0);
		this.position.set(position);
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		if (rotation == null) rotation = new Quaternionf(0, 0, 0, 1);
		this.rotation.set(rotation);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		if (scale == null) scale = new Vector3f(1, 1, 1);
		this.scale.set(scale);
	}
	
	public void rotateArround(Vector3f axis, float angle) {
		this.rotation.set(new Quaternionf().rotateAxis(angle, axis).mul(this.rotation).normalize());
	}
	
	public void lookAt(Vector3f front, Vector3f up) {
		float fx = -front.x, fy = -front.y, fz = -front.z;
		float ux = -up.x, uy = -up.y, uz = -up.z;
		
		//normalize dir
		float fl = (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
		fx /= fl;
		fy /= fl;
		fz /= fl;
		
		//right
		float rx = fy * uz - fz * uy;
		float ry = fz * ux - fx * uz;
		float rz = fx * uy - fy * ux;
		
		//normalize left
		float rl = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
		rx /= rl;
		ry /= rl;
		rz /= rl;
		
		//normalized up
		ux = fy * rz - fz * ry;
		uy = fz * rx - fx * rz;
		uz = fx * ry - fy * rx;
		
		//Calculate as matrix: row 0 = right, row 1 = up, row 2 = front
		float x, y, z, w, s;
		float trace = rx + uy + fz;
		if (trace > 0) {
			s = (float) (2.0f * Math.sqrt(trace + 1.0));
			w = 0.25f * s;
			x = (uz - fy) / s;
			y = (fx - rz) / s;
			z = (ry - ux) / s;
		} else if (rx > uy && rx > fz) {
			s = (float) (2.0 * Math.sqrt(1.0 + rx - uy - fz));
			w = (uz - fy) / s;
			x = 0.25f * s;
			y = (ux + ry) / s;
			z = (fx + rz) / s;
		} else if (uy > fz) {
			s = (float) (2.0 * Math.sqrt(1.0 + uy - rx - fz));
			w = (fx - rz) / s;
			x = (ux + ry) / s;
			y = 0.25f * s;
			z = (fy + uz) / s;
		} else {
			s = (float) (2.0 * Math.sqrt(1.0 + fz - rx - uy));
			w = (ry - ux) / s;
			x = (fx + rz) / s;
			y = (fy + uz) / s;
			z = 0.25f * s;
		}
		float l = (float) Math.sqrt(x * x + y * y + z * z + w * w);
		this.rotation.set(x / l, y / l, z / l, w / l);
	}
	
	public void rotateEuler(Vector3f angles) {
		this.rotation.rotationXYZ(angles.x, angles.y, angles.z);
	}
	
}
