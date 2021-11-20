package com.lndf.glengine.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
	
	//for rotation
	private static final Vector3f FRONT = new Vector3f(0, 0, -1);
	private static final Vector3f RIGHT = new Vector3f(1, 0, 0);
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	
	//caches
	private Matrix4f cacheLocalMatrix = null;
	private Matrix4f cacheParentMatrix = null;
	private Matrix4f cacheParentMatrixInv = null;
	private Vector3f cacheWorldPosition = null;
	private Vector3f cacheWorldScale = null;
	private Quaternionf cacheWorldRotation = null;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f scale = new Vector3f(1, 1, 1);
	private Quaternionf rotation = new Quaternionf(0, 0, 0, 1);
	
	private GameObject obj;
	
	private Vector3f tmpV = new Vector3f();
	private Quaternionf tmpQ = new Quaternionf();
	
	public Transform(GameObject obj) {
		if (obj == null) throw new RuntimeException("GameObject is null");
		this.obj = obj;
	}
	
	public void clearParentCache() {
		if (this.cacheParentMatrix != null ||
			this.cacheParentMatrixInv != null ||
			this.cacheWorldPosition != null ||
			this.cacheWorldScale != null ||
			this.cacheWorldRotation != null) {
			
			this.cacheParentMatrix = null;
			this.cacheParentMatrixInv = null;
			this.cacheWorldPosition = null;
			this.cacheWorldScale = null;
			this.cacheWorldRotation = null;
			for (GameObject child : this.obj.getChildren()) {
				child.getTransform().clearParentCache();
			}
		}
	}
	
	public void clearLocalCache() {
		if (this.cacheLocalMatrix != null ||
			this.cacheWorldPosition != null ||
			this.cacheWorldScale != null ||
			this.cacheWorldRotation != null) {
			
			this.cacheLocalMatrix = null;
			this.cacheWorldPosition = null;
			this.cacheWorldScale = null;
			this.cacheWorldRotation = null;
			for (GameObject child : this.obj.getChildren()) {
				child.getTransform().clearParentCache();
			}
		}
	}
	
	public Matrix4f getLocalMatrix() {
		if (this.cacheLocalMatrix != null) {
			return new Matrix4f(this.cacheLocalMatrix);
		}
		Matrix4f localMatrix = new Matrix4f().identity();
		localMatrix.translate(this.position).rotate(this.rotation).scale(this.scale);
		this.cacheLocalMatrix = localMatrix;
		return new Matrix4f(localMatrix);
	}
	
	public Matrix4f getParentMatrix() {
		if (this.cacheParentMatrix != null) {
			return new Matrix4f(this.cacheParentMatrix);
		}
		GameObject parentObj = this.obj.getParent();
		if (parentObj != null) {
			Transform pTrans = parentObj.getTransform();
			Matrix4f p = pTrans.getParentMatrix();
			Matrix4f l = pTrans.getLocalMatrix();
			this.cacheParentMatrix = p.mul(l);
			return new Matrix4f(this.cacheParentMatrix);
		}
		return new Matrix4f().identity();
	}
	
	public Matrix4f getInverseParentMatrix() {
		if (this.cacheParentMatrixInv != null) {
			return new Matrix4f(this.cacheParentMatrixInv);
		}
		Matrix4f inv = this.getParentMatrix();
		this.cacheParentMatrixInv = inv.invert();
		return new Matrix4f(inv);
	}
	
	public Matrix4f getWorldMatrix() {
		Matrix4f parent = this.getParentMatrix();
		Matrix4f local = this.getLocalMatrix();
		return parent.mul(local);
	}
	
	public Vector3f getPosition() {
		return new Vector3f(this.position);
	}
	
	public Vector3f getScale() {
		return new Vector3f(this.scale);
	}
	
	public Quaternionf getRotation() {
		return new Quaternionf(this.rotation);
	}
	
	public void setPosition(Vector3f position) {
		this.position.set(position);
		this.clearLocalCache();
	}
	
	public void setScale(Vector3f scale) {
		this.scale.set(scale);
		this.clearLocalCache();
	}
	
	public void setRotation(Quaternionf rotation) {
		this.rotation.set(rotation);
		this.clearLocalCache();
	}
	
	public Vector3f getWorldPosition() {
		if (this.cacheWorldPosition != null) {
			return new Vector3f(this.cacheWorldPosition);
		}
		this.cacheWorldPosition = this.getWorldMatrix().getTranslation(new Vector3f());
		return new Vector3f(this.cacheWorldPosition);
	}
	
	public Vector3f getWorldScale() {
		if (this.cacheWorldScale != null) {
			return new Vector3f(this.cacheWorldScale);
		}
		this.cacheWorldScale = this.getWorldMatrix().getScale(new Vector3f());
		return new Vector3f(this.cacheWorldScale);
	}
	
	public Quaternionf getWorldRotation() {
		if (this.cacheWorldRotation != null) {
			return new Quaternionf(this.cacheWorldRotation);
		}
		this.cacheWorldRotation = this.getWorldMatrix().getUnnormalizedRotation(new Quaternionf());
		return new Quaternionf(this.cacheWorldRotation);
	}
	
	public void setWorldPosition(Vector3f position) {
		this.setPosition(position);
		this.getInverseParentMatrix().transformPosition(this.position);
	}
	
	public void setWorldScale(Vector3f scale) {
		this.setScale(scale);
		this.scale.div(this.getParentMatrix().getScale(tmpV));
	}
	
	public void setWorldRotation(Quaternionf rotation) {
		this.setRotation(this.getParentMatrix().getUnnormalizedRotation(tmpQ).invert().mul(rotation));
	}
	
	public Vector3f getFront() {
		return this.getLocalMatrix().transformDirection(new Vector3f(FRONT));
	}

	public Vector3f getRight() {
		return this.getLocalMatrix().transformDirection(new Vector3f(RIGHT));
	}

	public Vector3f getUp() {
		return this.getLocalMatrix().transformDirection(new Vector3f(UP));
	}

	public Vector3f getBack() {
		return this.getLocalMatrix().transformDirection(new Vector3f(FRONT).mul(-1));
	}

	public Vector3f getLeft() {
		return this.getLocalMatrix().transformDirection(new Vector3f(RIGHT).mul(-1));
	}

	public Vector3f getDown() {
		return this.getLocalMatrix().transformDirection(new Vector3f(UP).mul(-1));
	}
	
	public Vector3f getWorldFront() {
		return this.getWorldMatrix().transformDirection(new Vector3f(FRONT));
	}

	public Vector3f getWorldRight() {
		return this.getWorldMatrix().transformDirection(new Vector3f(RIGHT));
	}

	public Vector3f getWorldUp() {
		return this.getWorldMatrix().transformDirection(new Vector3f(UP));
	}

	public Vector3f getWorldBack() {
		return this.getWorldMatrix().transformDirection(new Vector3f(FRONT).mul(-1));
	}

	public Vector3f getWorldLeft() {
		return this.getWorldMatrix().transformDirection(new Vector3f(RIGHT).mul(-1));
	}

	public Vector3f getWorldDown() {
		return this.getWorldMatrix().transformDirection(new Vector3f(UP).mul(-1));
	}
	
	public void rotateArround(Vector3f axis, float angle) {
		this.setRotation(new Quaternionf().rotateAxis(angle, axis).mul(this.getRotation()).normalize());
	}
	
	public void worldRotateArround(Vector3f axis, float angle) {
		this.setWorldRotation(new Quaternionf().rotateAxis(angle, axis).mul(this.getWorldRotation()).normalize());
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
		this.clearLocalCache();
	}
	
	public void worldLookAt(Vector3f front, Vector3f up) {
		this.lookAt(front, up);
		this.rotation.div(this.getParentMatrix().getUnnormalizedRotation(tmpQ));
	}
	
	public void rotateEuler(Vector3f angles) {
		this.setRotation(this.getRotation().rotationXYZ(angles.x, angles.y, angles.z));
	}
	
	public void worldRotateEuler(Vector3f angles) {
		this.setWorldRotation(this.getWorldRotation().rotateXYZ(angles.x, angles.y, angles.z));
	}
}
