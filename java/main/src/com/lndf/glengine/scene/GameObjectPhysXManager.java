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
	
	private float poseThreshold = 0.00001f;
	
	private Vector3f lastPos = null;
	private Quaternionf lastRot = null;
	private Vector3f lastScale = null;
	
	private HashSet<Collider> shapes = new HashSet<Collider>();
	private PxRigidActor rigid;
	private GameObject parentRigidOwner = null;
	private boolean customRigid = false;
	
	public GameObjectPhysXManager(GameObject object) {
		Engine.addEngineResource(this);
		this.object = object;
		GameObject pOwner = this.getParentCustomRigidObject();
		if (pOwner != null) {
			PxRigidActor oldRigid = this.rigid;
			this.parentRigidOwner = pOwner;
			this.swapRigidBody(pOwner.getPhysx().getPxRigid(), false);
			if (oldRigid != null) oldRigid.release();
		}
	}
	
	private GameObject getParentCustomRigidObject() {
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
		boolean oldWasParent = this.parentRigidOwner != null;
		this.parentRigidOwner = null;
		boolean wasCustomRigid = this.customRigid;
		this.customRigid = true;
		this.swapRigidBody(newRigid, oldWasParent);
		if (!wasCustomRigid && !oldWasParent) {
			if (oldRigid != null) oldRigid.release();
			this.customRigid = true;
		}
		if (oldWasParent) {
			this.unsetShapeParentPose();
		}
	}
	
	public void unsetRigidBody() {
		if (!this.customRigid || this.parentRigidOwner != null) return;
		this.customRigid = false;
		if (this.shapes.size() > 0) {
			GameObject prOwner = this.getParentCustomRigidObject();
			if (prOwner != null) {
				this.parentRigidOwner = prOwner;
				this.swapRigidBody(prOwner.getPhysx().getPxRigid(), false);
			} else {
				this.setDefaultRigidBody(false);
			}
		} else {
			this.updateChildrenRigids();
			this.rigid = null;
		}
	}
	
	private void unsetShapeParentPose() {
		for (Collider shape : this.shapes) {
			shape.unsetParentPose();
		}
		this.lastPos = null;
		this.lastRot = null;
	}
	
	public void notifyChildrenRigidChange(GameObject parent) {
		if (this.customRigid || this.shapes == null) return;
		if (parent != null) {
			PxRigidActor oldRigid = this.rigid;
			boolean oldWasParent = this.parentRigidOwner != null;
			this.parentRigidOwner = parent;
			this.swapRigidBody(parent.getPhysx().getPxRigid(), oldWasParent);
			if (oldRigid != null && !oldWasParent) oldRigid.release();
			this.lastPos = null;
			this.lastRot = null;
		} else {
			this.parentRigidOwner = null;
			this.customRigid = false;
			this.unsetShapeParentPose();
			if (this.shapes.size() > 0) {
				this.setDefaultRigidBody(true);
			} else {
				this.updateChildrenRigids();
				this.rigid = null;
			}
		}
		for (GameObject child : this.object.getChildren()) {
			child.getPhysx().notifyChildrenRigidChange(parent);
		}
	}
	
	private void setDefaultRigidBody(boolean oldWasParent) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			PxRigidStatic rigid = PhysXManager.getPhysics().createRigidStatic(pose);
			this.swapRigidBody(rigid, oldWasParent);
		}
	}
	
	private void swapRigidBody(PxRigidActor newRigid, boolean oldWasParent) {
		if (newRigid == null) return;
		Scene scene = object.getScene();
		if (this.rigid != null) {
			if (scene != null && !oldWasParent) {
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
			this.setDefaultRigidBody(false);
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
				this.updateChildrenRigids();
			}
		}
	}
	
	public void pullPoseFromRigidBody() {
		if (this.rigid == null || this.parentRigidOwner != null) return;
		PxTransform pose = this.rigid.getGlobalPose();
		PxVec3 p = pose.getP();
		PxQuat q = pose.getQ();
		Vector3f jp = new Vector3f(p.getX(), p.getY(), p.getZ());
		Quaternionf jq = new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW());
		this.object.getTransform().setWorldPosition(jp);
		this.object.getTransform().setWorldRotation(jq);
		this.lastPos = jp;
		this.lastRot = jq;
	}
	
	//TODO: reimplement
	public void pushPoseToRigidBody() {
		if (this.rigid == null) return;
		Quaternionf jq = this.object.getTransform().getWorldRotation();
		Vector3f jp = this.object.getTransform().getWorldPosition();
		Vector3f js = this.object.getTransform().getWorldScale();
		if (this.parentRigidOwner != null) {
			jq = this.parentRigidOwner.getTransform().getRelativeRotation(jq);
			jp = this.parentRigidOwner.getTransform().getRelativePosition(jp).mul(js);
		}
		boolean posHasChanged = true;
		boolean rotHasChanged = true;
		boolean scaleHasChanged = true;
		if (this.lastPos != null) {
			posHasChanged = Math.abs(jp.x - this.lastPos.x) > this.poseThreshold ||
							Math.abs(jp.y - this.lastPos.y) > this.poseThreshold ||
							Math.abs(jp.z - this.lastPos.z) > this.poseThreshold;
		}
		if (this.lastRot != null) {
			rotHasChanged = Math.abs(jq.x - this.lastRot.x) > this.poseThreshold ||
							Math.abs(jq.y - this.lastRot.y) > this.poseThreshold ||
							Math.abs(jq.z - this.lastRot.z) > this.poseThreshold ||
							Math.abs(jq.w - this.lastRot.w) > this.poseThreshold;
		}
		if (this.lastScale != null) {
			scaleHasChanged = Math.abs(js.x - this.lastScale.x) > this.poseThreshold ||
							  Math.abs(js.y - this.lastScale.y) > this.poseThreshold ||
							  Math.abs(js.z - this.lastScale.z) > this.poseThreshold;
		}
		if (posHasChanged || rotHasChanged) {
			if (this.parentRigidOwner != null) {
				for (Collider shape : this.shapes) {
					shape.setParentPose(jp, jq);

				}
			} else {
				this.lastPos = jp;
				this.lastRot = jq;
				try (MemoryStack mem = MemoryStack.stackPush()) {
					PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
					pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, jp.x, jp.y, jp.z));
					pose.setQ(PxQuat.createAt(mem, MemoryStack::nmalloc, jq.x, jq.y, jq.z, jq.w));
					this.rigid.setGlobalPose(pose);
				}
			}
		}
		if (scaleHasChanged) {
			this.lastScale = js;
			for (Collider shape : this.shapes) {
				shape.recreate();
			}
		}
	}
	
	public float getPoseThreshold() {
		return poseThreshold;
	}

	public void setPoseThreshold(float poseThreshold) {
		this.poseThreshold = poseThreshold;
	}

	public PxRigidActor getPxRigid() {
		return this.rigid;
	}
	
	public boolean hasCustomRigid() {
		return this.customRigid;
	}
	
	public GameObject getParentRigidOwner() {
		return this.parentRigidOwner;
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