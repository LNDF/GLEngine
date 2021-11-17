package com.lndf.glengine.scene;

import java.util.HashSet;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.scene.components.physics.Collider;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidStatic;

public class GameObjectPhysXManager implements EngineResource {
	
	private Vector3f lastPos = null;
	private Vector3f lastScale = null;
	private Quaternionf lastRot = null;
	
	private GameObject object;
	
	private boolean userRigid = false;
	private PxRigidActor rigid = null;
	
	private HashSet<Collider> shapes = new HashSet<Collider>();
	
	public GameObjectPhysXManager(GameObject object) {
		Engine.addEngineResource(this);
		this.object = object;
	}
	
	public void addShape(Collider shape) {
		this.shapes.add(shape);
		if (this.rigid == null) {
			userRigid = false;
			this.createDefaultRigidBody();
		}
		this.rigid.attachShape(shape.getPhysXShape());
	}
	
	public void removeShape(Collider shape) {
		if (this.shapes.contains(shape)) {
			this.shapes.remove(shape);
			this.rigid.detachShape(shape.getPhysXShape());
			if (this.shapes.size() == 0 && !this.userRigid) {
				this.rigid.release();
				this.rigid = null;
			}
		}
	}
	
	public void setRigidBody(PxRigidActor rigid) {
		PxRigidActor oldRigid = this.rigid;
		this.swapRigidBody(rigid);
		if (!this.userRigid) {
			if (oldRigid != null) oldRigid.release();
			this.userRigid = true;
		}
	}
	
	public void unsetRigidBody() {
		PxRigidActor oldRigid = this.rigid;
		if (this.shapes.size() > 0) {
			this.createDefaultRigidBody();
		} else {
			this.rigid = null;
		}
		if (oldRigid != null && !this.userRigid) oldRigid.release();
		this.userRigid = false;
	}
	
	private void createDefaultRigidBody() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			PxRigidStatic rigid = PhysXManager.getPhysics().createRigidStatic(pose);
			this.swapRigidBody(rigid);
		}
	}
	
	private void swapRigidBody(PxRigidActor newRigid) {
		Scene scene = object.getScene();
		if (scene != null) scene.getPhysXScene().removeActor(this.rigid);
		if (this.rigid != null) {
			for (Collider shape : this.shapes) {
				this.rigid.detachShape(shape.getPhysXShape());
			}
		}
		for (Collider shape : this.shapes) {
			newRigid.attachShape(shape.getPhysXShape());
		}
		this.rigid = newRigid;
		if (scene != null) scene.getPhysXScene().addActor(newRigid);
	}
	
	public void pullPoseFromRigidBody() {
		if (this.rigid == null) return;
		PxTransform pose = this.rigid.getGlobalPose();
		PxVec3 p = pose.getP();
		PxQuat q = pose.getQ();
		this.object.getTransform().setWorldPosition(new Vector3f(p.getX(), p.getY(), p.getZ()));
		this.object.getTransform().setWorldRotation(new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW()));
		this.lastPos = this.object.getTransform().getWorldPosition();
		this.lastRot = this.object.getTransform().getWorldRotation();
	}
	
	public void pushPoseToRigidBody() {
		if (this.rigid == null) return;
		Quaternionf jq = this.object.getTransform().getWorldRotation();
		Vector3f jp = this.object.getTransform().getWorldPosition();
		Vector3f js = this.object.getTransform().getWorldScale();
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
		if (!jp.equals(this.lastScale)) {
			this.lastScale = js;
			for (Collider shape : this.shapes) {
				shape.scaleChanged(js);
			}
		}
	}
	
	public void updateScene(Scene newScene) {
		if (this.rigid == null) return;
		Scene scene = this.object.getScene();
		if (scene != null) {
			scene.getPhysXScene().removeActor(rigid);
		}
		if (newScene != null) {
			newScene.getPhysXScene().addActor(rigid);
		}
	}
	
	public void destroy() {
		if (this.shapes == null) return;
		for (Collider shape : this.shapes) {
			this.rigid.detachShape(shape.getPhysXShape());
		}
		if (this.rigid != null) {
			if (!this.userRigid) this.rigid.release();
			this.rigid = null;
		}
		this.shapes.clear();
		this.shapes = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
}
