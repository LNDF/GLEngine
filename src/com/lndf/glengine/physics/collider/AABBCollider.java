package com.lndf.glengine.physics.collider;

import org.joml.Vector3f;

import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.scene.GameObject;

public class AABBCollider extends PolyhedronCollider {
	
	private Vector3f min;
	private Vector3f max;
	
	public AABBCollider(Vector3f min, Vector3f max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean getCollision(Collider other) {
		if (other == null) return false;
		if (other instanceof AABBCollider) {
			return this.getCollisionAABB((AABBCollider) other);
		} else return false;
	}
	
	public boolean getCollisionAABB(AABBCollider other) {
		if (other == null) return false;
		GameObject thisObject = this.getGameObject();
		GameObject otherObject = other.getGameObject();
		if (thisObject == null || otherObject == null) return false;
		Vector3f otherMin = other.getMin();
		Vector3f otherMax = other.getMax();
		Vector3f thisPos = thisObject.getTransform().getWorldPosition();
		Vector3f otherPos = otherObject.getTransform().getWorldPosition();
		float txMin, tyMin, tzMin, oxMin, oyMin, ozMin;
		float txMax, tyMax, tzMax, oxMax, oyMax, ozMax;
		txMin = this.min.x + thisPos.x;
		tyMin = this.min.y + thisPos.y;
		tzMin = this.min.z + thisPos.z;
		txMax = this.max.x + thisPos.x;
		tyMax = this.max.y + thisPos.y;
		tzMax = this.max.z + thisPos.z;
		oxMin = otherMin.x + otherPos.x;
		oyMin = otherMin.y + otherPos.y;
		ozMin = otherMin.z + otherPos.z;
		oxMax = otherMax.x + otherPos.x;
		oyMax = otherMax.y + otherPos.y;
		ozMax = otherMax.z + otherPos.z;
		float d1x, d2x, d1y, d2y, d1z, d2z;
		d1x = oxMin - txMax;
		d1y = oyMin - tyMax;
		d1z = ozMin - tzMax;
		d2x = txMin - oxMax;
		d2y = tyMin - oyMax;
		d2z = tzMin - ozMax;
		if (d1x > 0 || d1y > 0 || d1z > 0 || d2x > 0 || d2y > 0 || d2z > 0) return false;
		return true;
	}
	
	@Override
	public Mesh getMesh() {
		GameObject thisObject = this.getGameObject();
		if (thisObject == null) return null;
		Vector3f thisPos = thisObject.getTransform().getWorldPosition();
		float minX = this.min.x + thisPos.x;
		float minY = this.min.y + thisPos.y;
		float minZ = this.min.z + thisPos.z;
		float maxX = this.max.x + thisPos.x;
		float maxY = this.max.y + thisPos.y;
		float maxZ = this.max.z + thisPos.z;
		float[] vertices = {
				 minX, maxY, minZ,    0.0f, 1.0f,    0.0f,  1.0f,  0.0f,
				 maxX, maxY, minZ,    1.0f, 1.0f,    0.0f,  1.0f,  0.0f,
				 maxX, maxY, maxZ,    1.0f, 0.0f,    0.0f,  1.0f,  0.0f,
				 minX, maxY, maxZ,    0.0f, 0.0f,    0.0f,  1.0f,  0.0f,
				 minX, minY, minZ,    0.0f, 0.0f,    0.0f, -1.0f,  0.0f,
				 maxX, minY, minZ,    1.0f, 0.0f,    0.0f, -1.0f,  0.0f,
				 maxX, minY, maxZ,    1.0f, 1.0f,    0.0f, -1.0f,  0.0f,
				 minX, minY, maxZ,    0.0f, 1.0f,    0.0f, -1.0f,  0.0f,
				 maxX, maxY, minZ,    0.0f, 1.0f,    0.0f,  0.0f, -1.0f,
				 maxX, minY, minZ,    0.0f, 0.0f,    0.0f,  0.0f, -1.0f,
				 minX, minY, minZ,    1.0f, 0.0f,    0.0f,  0.0f, -1.0f,
				 minX, maxY, minZ,    1.0f, 1.0f,    0.0f,  0.0f, -1.0f,
				 maxX, maxY, maxZ,    1.0f, 1.0f,    0.0f,  0.0f,  1.0f,
				 maxX, minY, maxZ,    1.0f, 0.0f,    0.0f,  0.0f,  1.0f,
				 minX, minY, maxZ,    0.0f, 0.0f,    0.0f,  0.0f,  1.0f,
				 minX, maxY, maxZ,    0.0f, 1.0f,    0.0f,  0.0f,  1.0f,
				 minX, maxY, maxZ,    1.0f, 1.0f,   -1.0f,  0.0f,  0.0f,
				 minX, minY, maxZ,    1.0f, 0.0f,   -1.0f,  0.0f,  0.0f,
				 minX, minY, minZ,    0.0f, 0.0f,   -1.0f,  0.0f,  0.0f,
				 minX, maxY, minZ,    0.0f, 1.0f,   -1.0f,  0.0f,  0.0f,
				 maxX, maxY, maxZ,    0.0f, 1.0f,    1.0f,  0.0f,  0.0f,
				 maxX, minY, maxZ,    0.0f, 0.0f,    1.0f,  0.0f,  0.0f,
				 maxX, minY, minZ,    1.0f, 0.0f,    1.0f,  0.0f,  0.0f,
				 maxX, maxY, minZ,    1.0f, 1.0f,    1.0f,  0.0f,  0.0f
		};
		int[] indices = {
				2,   1,  0,
				0,   3,  2,
				4,   5,  6,
				6,   7,  4,
				8,   9, 10,
				10, 11,  8,
				14, 13, 12,
				12, 15, 14,
				18, 17, 16,
				16, 19, 18,
				20, 21, 22,
				22, 23, 20,
				24, 25, 26,
				26, 27, 24
		};
		return new Mesh(vertices, indices);
	}

	public Vector3f getMin() {
		return min;
	}

	public void setMin(Vector3f min) {
		this.min = min;
	}

	public Vector3f getMax() {
		return max;
	}
	
	public void setMax(Vector3f max) {
		this.max = max;
	}
	
}
