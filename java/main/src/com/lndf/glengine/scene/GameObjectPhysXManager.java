package com.lndf.glengine.scene;

import java.util.HashSet;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;

public class GameObjectPhysXManager {
	
	private Vector3f lastPos = null;
	private Quaternionf lastRot = null;
	
	private GameObject object;
	
	private boolean userRigid = false;
	private PxRigidActor rigid = null;
	
	private HashSet<PxShape> shapes = new HashSet<PxShape>();
	
	public GameObjectPhysXManager(GameObject object) {
		this.object = object;
	}
	
	public void addShape(PxShape shape) {
		this.shapes.add(shape);
		if (rigid == null) {
			userRigid = false;
			this.createDefaultRigidBody();
		}
	}
	
	public void removeShape(PxShape shape) {
		
	}
	
	public void setCustomRigidBody() {
		
	}
	
	public void unsetCustomRigidBody() {
		
	}
	
	private void createDefaultRigidBody() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			this.rigid = PhysXManager.getPhysics().createRigidStatic(pose);
		}
	}
	
	private void swapRigidBody() {
		
	}
	
	public void pullPoseFromRigidBody() {
		
	}
	
	public void pushPoseToRigidBody() {
		if (this.rigid == null) return;
		Quaternionf jq = this.object.getTransform().getWorldRotation();
		Vector3f jp = this.object.getTransform().getWorldPosition();
		if (!jp.equals(this.lastPos) || !jq.equals(this.lastRot)) {
			try (MemoryStack mem = MemoryStack.stackPush()) {
				PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
				pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, jp.x, jp.y, jp.z));
				pose.setQ(PxQuat.createAt(mem, MemoryStack::nmalloc, jq.x, jq.y, jq.z, jq.w));
				this.lastPos = jp;
				this.lastRot = jq;
				this.rigid.setGlobalPose(pose);
			}
		}		
	}
	
}
