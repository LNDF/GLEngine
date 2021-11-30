package com.lndf.glengine.scene.components.physics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.physics.PhysicalMaterial;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;

import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;

public abstract class Collider extends Component implements EngineResource {
	
	private Vector3f tmpV = new Vector3f();
	private Quaternionf tmpQ = new Quaternionf();
	
	private Vector3f parentPos;
	private Quaternionf parentRot;
	
	private RigidBody rigid;
	
	private Vector3f localPos = new Vector3f(0, 0, 0);
	private Quaternionf localRot = new Quaternionf(0, 0, 0, 1);
	
	private float restOffset;
	private float contactOffset;
	private boolean trigger;
	
	protected PxShape shape;
	protected PhysicalMaterial material;
	
	protected abstract void pxCreate();
	
	protected void createShape() {
		this.pxCreate();
		this.setRestOffset(this.restOffset);
		this.setContactOffset(this.contactOffset);
		this.setTrigger(this.trigger);
	}
	
	protected Collider(PhysicalMaterial material) {
		Engine.addEngineResource(this);
		this.material = material;
	}
	
	public PhysicalMaterial getMaterial() {
		return material;
	}
	
	public PxShape getPhysXShape() {
		if (this.shape == null) {
			this.createShape();
		}
		return this.shape;
	}
	
	protected void pxDestroy() {
		if (this.shape != null) {
			this.restOffset = this.getRestOffset();
			this.contactOffset = this.getContactOffset();
			this.trigger = this.isTrigger();
			this.shape.release();
			this.shape = null;
		}
	}
	
	public void recreate() {
		if (this.shape == null) return;
		GameObject obj = this.getGameObject();
		RigidBody rigid = null;
		if (obj != null) {
			rigid = obj.getPhysx().getRigidBody();
			rigid.removeShape(this);
		}
		this.pxDestroy();
		this.createShape();
		this.repose();
		if (obj != null) {
			rigid.addShape(this);
		}
	}
	
	private void repose() {
		if (this.shape == null) return;
		if (this.parentPos != null && this.parentRot != null) {
			Matrix4f mat = new Matrix4f().identity();
			mat.translate(this.parentPos).rotate(this.parentRot);
			mat.translate(this.localPos).rotate(this.localRot);
			mat.getUnnormalizedRotation(this.tmpQ);
			mat.getTranslation(this.tmpV);
		} else {
			tmpQ.set(this.localRot);
			tmpV.set(this.localPos);
		}
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxQuat q = PxQuat.createAt(mem, MemoryStack::nmalloc, tmpQ.x, tmpQ.y, tmpQ.z, tmpQ.w);
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, tmpV.x, tmpV.y, tmpV.z);
			PxTransform t = PxTransform.createAt(mem, MemoryStack::nmalloc, v, q);
			this.shape.setLocalPose(t);
		}
		if (this.rigid != null) this.rigid.shapeUpdated();
	}
	
	public void setParentPose(Vector3f pos, Quaternionf rot) {
		if (this.parentPos == null) this.parentPos = new Vector3f();
		if (this.parentRot == null) this.parentRot = new Quaternionf();
		this.parentPos.set(pos);
		this.parentRot.set(rot);
		this.repose();
	}
	
	public void unsetParentPose() {
		this.parentPos = null;
		this.parentRot = null;
		this.repose();
	}
	
	public void setCenterAndRotation(Vector3f center, Quaternionf rotation) {
		this.localPos.set(center);
		this.localRot.set(rotation);
		this.repose();
	}
	
	public void setCenter(Vector3f center) {
		this.localPos.set(center);
		this.repose();
	}
	
	public void setRotation(Quaternionf rotation) {
		this.localRot.set(rotation);
		this.repose();
	}
	
	public RigidBody getRigid() {
		return rigid;
	}

	public void setRigid(RigidBody rigid) {
		this.rigid = rigid;
	}
	
	public float getContactOffset() {
		if (this.shape == null) return this.contactOffset;
		return this.shape.getContactOffset();
	}
	
	public void setContactOffset(float contactOffset) {
		if (this.shape != null) {
			this.shape.setContactOffset(contactOffset);
		} else {
			this.contactOffset = contactOffset;
		}
	}
	
	public float getRestOffset() {
		if (this.shape == null) return this.restOffset;
		return this.shape.getRestOffset();
	}
	
	public void setRestOffset(float restOffset) {
		if (this.shape != null) {
			this.shape.setRestOffset(restOffset);
		} else {
			this.restOffset = restOffset;
		}
	}
	
	public boolean isTrigger() {
		if (this.shape == null) return this.trigger;
		return this.shape.getFlags().isSet(PxShapeFlagEnum.eTRIGGER_SHAPE);
	}
	
	public void setTrigger(boolean trigger) {
		if (this.shape != null) {
			if (trigger) {
				this.shape.setFlag(PxShapeFlagEnum.eSIMULATION_SHAPE, false);
				this.shape.setFlag(PxShapeFlagEnum.eTRIGGER_SHAPE, true);
			} else {
				this.shape.setFlag(PxShapeFlagEnum.eTRIGGER_SHAPE, false);
				this.shape.setFlag(PxShapeFlagEnum.eSIMULATION_SHAPE, true);
			}
		} else {
			this.trigger = trigger;
		}
	}
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getPhysx().addShape(this);
	}
	
	@Override
	public void removeFromGameObject() {
		this.getGameObject().getPhysx().removeShape(this);
	}
	
	public void destroy() {
		Engine.removeEngineResource(this);
		this.pxDestroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
}
