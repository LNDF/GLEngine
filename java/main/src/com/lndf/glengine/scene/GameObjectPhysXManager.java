package com.lndf.glengine.scene;

import java.util.HashSet;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.scene.components.physics.Collider;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidStatic;

public class GameObjectPhysXManager implements EngineResource {
	
	private GameObject object;
	
	private boolean posChanged = false;
	private boolean rotChanged = false;
	private boolean scaleChanged = false;
	
	private HashSet<Collider> shapes = new HashSet<Collider>();
	private PxRigidActor rigid;
	private GameObject parentRigidOwner = null;
	private boolean customRigid = false;
	
	public GameObjectPhysXManager(GameObject object) {
		Engine.addEngineResource(this);
		this.object = object;
		this.setParentRigid();
	}
	
	private void setParentRigid() {
		GameObject pOwner = this.getParentRigidOwner();
		if (pOwner != null) {
			PxRigidActor oldRigid = this.rigid;
			this.parentRigidOwner = pOwner;
			this.swapRigidBody(pOwner.getPhysx().getPxRigid());
			if (oldRigid != null) oldRigid.release();
		}
	}
	
	private GameObject getParentRigidOwner() {
		GameObject parent = this.object.getParent();
		if (parent != null) {
			GameObjectPhysXManager pm = parent.getPhysx();
			if (pm.customRigid) {
				return parent;
			}
			if (pm.parentRigidOwner != null) {
				return pm.parentRigidOwner;
			}
		}
		return null;
	}
	
	public void updateChildrenRigids() {
		GameObject obj = null;
		if (this.customRigid) {
			obj = this.object;
		} else if (this.parentRigidOwner != null) {
			obj = this.parentRigidOwner;
		}
		for (GameObject child : this.object.getChildren()) {
			child.getPhysx().notifyChildrenRigidChange(obj);
		}
	}
	
	public void setRigidBody(PxRigidActor newRigid) {
		PxRigidActor oldRigid = this.rigid;
		this.parentRigidOwner = null;
		this.swapRigidBody(newRigid);
		if (!this.customRigid) {
			if (oldRigid != null) oldRigid.release();
			this.customRigid = true;
		}
	}
	
	public void unsetRigidBody() {
		PxRigidActor oldRigid = this.rigid;
		boolean oldWasParent = this.parentRigidOwner == null;
		if (this.shapes.size() > 0) {
			this.setParentRigid();
			if (this.rigid == null) {
				this.setDefaultRigidBody();
			} else {
				this.parentRigidOwner = null;
			}
		} else {
			this.rigid = null;
		}
		if (oldRigid != null && !this.customRigid && oldWasParent) oldRigid.release();
		this.customRigid = false;
	}
	
	private void notifyChildrenRigidChange(GameObject parent) {
		if (this.customRigid || this.shapes == null) return;
		if (parent != null) {
			PxRigidActor oldRigid = this.rigid;
			this.customRigid = false;
			this.parentRigidOwner = parent;
			this.swapRigidBody(parent.getPhysx().getPxRigid());
			if (oldRigid != null) oldRigid.release();
		} else {
			this.unsetRigidBody();
		}
		for (GameObject child : this.object.getChildren()) {
			child.getPhysx().notifyChildrenRigidChange(parent);
		}
	}
	
	private void setDefaultRigidBody() {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			PxRigidStatic rigid = PhysXManager.getPhysics().createRigidStatic(pose);
			this.swapRigidBody(rigid);
		}
	}
	
	private void swapRigidBody(PxRigidActor newRigid) {
		if (newRigid == null) return;
		Scene scene = object.getScene();
		if (this.rigid != null) {
			if (scene != null && this.parentRigidOwner == null) {
				scene.getPhysXScene().removeActor(this.rigid);
			}
			for (Collider shape : this.shapes) {
				this.rigid.detachShape(shape.getPhysXShape());
			}
		}
		for (Collider shape : this.shapes) {
			newRigid.attachShape(shape.getPhysXShape());
		}
		this.rigid = newRigid;
		if (scene != null && this.parentRigidOwner == null) scene.getPhysXScene().addActor(newRigid);
		this.updateChildrenRigids();
	}
	
	public void updateScene(Scene newScene) {
		if (this.rigid == null || this.parentRigidOwner != null) return;
		Scene scene = this.object.getScene();
		if (scene != null) {
			scene.getPhysXScene().removeActor(rigid);
		}
		if (newScene != null) {
			newScene.getPhysXScene().addActor(rigid);
		}
	}
	
	public void addShape(Collider shape) {
		this.shapes.add(shape);
		if (this.rigid == null) {
			customRigid = false;
			this.setDefaultRigidBody();
		}
		this.rigid.attachShape(shape.getPhysXShape());
	}
	
	public void removeShape(Collider shape) {
		if (this.shapes.contains(shape)) {
			this.shapes.remove(shape);
			this.rigid.detachShape(shape.getPhysXShape());
			if (this.shapes.size() == 0 && !this.customRigid && this.parentRigidOwner == null) {
				this.rigid.release();
				this.rigid = null;
			}
		}
	}
	
	//TODO: reimplement
	public void pullPoseFromRigidBody() {
		if (this.rigid == null) return;
		PxTransform pose = this.rigid.getGlobalPose();
		PxVec3 p = pose.getP();
		PxQuat q = pose.getQ();
		this.object.getTransform().setWorldPosition(new Vector3f(p.getX(), p.getY(), p.getZ()));
		this.object.getTransform().setWorldRotation(new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW()));
		this.posChanged = false;
		this.rotChanged = false;
	}
	
	//TODO: reimplement
	public void pushPoseToRigidBody() {
		if (this.rigid == null) return;
		if (this.posChanged || this.rotChanged) {
			this.posChanged = false;
			this.rotChanged = false;
			Quaternionf jq = this.object.getTransform().getWorldRotation();
			Vector3f jp = this.object.getTransform().getWorldPosition();
			try (MemoryStack mem = MemoryStack.stackPush()) {
				PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
				pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, jp.x, jp.y, jp.z));
				pose.setQ(PxQuat.createAt(mem, MemoryStack::nmalloc, jq.x, jq.y, jq.z, jq.w));
				this.rigid.setGlobalPose(pose);
			}
		}
		if (this.scaleChanged) {
			this.scaleChanged = false;
			for (Collider shape : this.shapes) {
				if (shape.getShouldScale()) {
					this.rigid.detachShape(shape.getPhysXShape());
					shape.pxDestroy();
					this.rigid.attachShape(shape.getPhysXShape());
				}
			}
		}
	}
	
	public void posHasChanged() {
		this.posChanged = true;
	}
	
	public void rotHasChanged() {
		this.rotChanged = true;
	}
	
	public void scaleHasChanged() {
		this.scaleChanged = true;
	}
	
	public PxRigidActor getPxRigid() {
		return this.rigid;
	}
	
	@Override
	public void destroy() {
		if (this.shapes == null) return;
		for (Collider shape : this.shapes) {
			this.rigid.detachShape(shape.getPhysXShape());
		}
		if (this.rigid != null && this.parentRigidOwner == null && !this.customRigid) {
			this.rigid.release();
		}
		this.parentRigidOwner = null;
		this.rigid = null;
		this.shapes.clear();
		this.shapes = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
}