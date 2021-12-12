package com.lndf.glengine.scene;

import java.util.HashSet;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.physics.StaticRigidBody;
import com.lndf.glengine.scene.components.physics.Collider;

public class GameObjectPhysXManager implements EngineResource {
	
	private GameObject object;
	
	private float poseThreshold = 0.00001f;
	
	private Vector3f lastPos = null;
	private Quaternionf lastRot = null;
	private Vector3f lastScale = null;
	
	private HashSet<Collider> shapes = new HashSet<Collider>();
	//private PxRigidActor rigid;
	private RigidBody rigid;
	private GameObject parentRigidOwner = null;
	private boolean customRigid = false;
	
	public GameObjectPhysXManager(GameObject object) {
		Engine.addEngineResource(this);
		this.object = object;
		GameObject pOwner = this.getParentCustomRigidObject();
		if (pOwner != null) {
			this.parentRigidOwner = pOwner;
			this.swapRigidBody(pOwner.getPhysx().getRigidBody(), false);
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
	
	public void setRigidBody(RigidBody newRigid) {
		if (this.customRigid) {
			throw new RuntimeException("Cannot add multiple custom rigid bodies to the same game object");
		}
		RigidBody oldRigid = this.rigid;
		boolean oldWasParent = this.parentRigidOwner != null;
		this.parentRigidOwner = null;
		boolean wasCustomRigid = this.customRigid;
		this.customRigid = true;
		this.swapRigidBody(newRigid, oldWasParent);
		if (!wasCustomRigid && !oldWasParent) {
			if (oldRigid != null) oldRigid.pxRelease();
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
				this.swapRigidBody(prOwner.getPhysx().getRigidBody(), false);
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
			RigidBody oldRigid = this.rigid;
			boolean oldWasParent = this.parentRigidOwner != null;
			this.parentRigidOwner = parent;
			this.swapRigidBody(parent.getPhysx().getRigidBody(), oldWasParent);
			if (oldRigid != null && !oldWasParent) oldRigid.pxRelease();
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
		StaticRigidBody staticBody = new StaticRigidBody(this.object);
		this.swapRigidBody(staticBody, oldWasParent);
	}
	
	private void swapRigidBody(RigidBody newRigid, boolean oldWasParent) {
		if (newRigid == null) return;
		Scene scene = object.getScene();
		if (this.rigid != null) {
			if (scene != null && !oldWasParent) {
				this.rigid.removeFromScene(scene);
			}
			this.rigid.removeShapes(this.shapes);
		}
		newRigid.addShapes(this.shapes);
		this.rigid = newRigid;
		if (scene != null && this.parentRigidOwner == null) newRigid.addToScene(scene);
		this.updateChildrenRigids();
	}
	
	public void updateScene(Scene newScene) {
		if (this.rigid == null || this.parentRigidOwner != null) return;
		Scene scene = this.object.getScene();
		if (scene != null) {
			this.rigid.removeFromScene(scene);
		}
		if (newScene != null) {
			this.rigid.addToScene(newScene);
		}
	}
	
	public void addShape(Collider shape) {
		if (this.rigid == null) {
			customRigid = false;
			this.setDefaultRigidBody(false);
		}
		this.shapes.add(shape);
		this.rigid.addShape(shape);
	}
	
	public void removeShape(Collider shape) {
		if (this.shapes.contains(shape)) {
			this.shapes.remove(shape);
			this.rigid.removeShape(shape);
			if (this.shapes.size() == 0 && !this.customRigid && this.parentRigidOwner == null) {
				this.rigid.pxRelease();
				this.rigid = null;
				this.updateChildrenRigids();
			}
		}
	}
	
	public void pullPoseFromRigidBody() {
		if (this.rigid == null || this.parentRigidOwner != null) return;
		Vector3f jp = this.rigid.getPxPosition();
		Quaternionf jq = this.rigid.getPxRotation();
		this.object.getTransform().setWorldPosition(jp);
		this.object.getTransform().setWorldRotation(jq);
		this.lastPos = jp;
		this.lastRot = jq;
	}
	
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
			this.lastPos = jp;
			this.lastRot = jq;
			if (this.parentRigidOwner != null) {
				for (Collider shape : this.shapes) {
					shape.setParentPose(jp, jq);
				}
			} else {
				this.rigid.setPxPose(jp, jq);
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

	public RigidBody getRigidBody() {
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
		if (this.rigid != null) this.rigid.removeShapes(this.shapes);
		if (this.rigid != null && this.parentRigidOwner == null && !this.customRigid) {
			this.rigid.pxRelease();
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