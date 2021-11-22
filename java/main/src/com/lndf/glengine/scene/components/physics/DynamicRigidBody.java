package com.lndf.glengine.scene.components.physics;

import java.util.Collection;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.scene.Component;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;
import physx.physics.PxRigidDynamicLockFlagEnum;
import physx.physics.PxRigidDynamicLockFlags;

public class DynamicRigidBody extends Component implements EngineResource, RigidBody {
	
	private PxRigidDynamic rigid;
	
	private boolean autoRecalculateMass = true;
	
	public DynamicRigidBody() {
		Engine.addEngineResource(this);
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			this.rigid = PhysXManager.getPhysics().createRigidDynamic(pose);
		}
	}
	
	public void destroy() {
		if (this.rigid == null) return;
		Engine.removeEngineResource(this);
		this.rigid.release();
		this.rigid = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getPhysx().setRigidBody(this);
	}
	
	@Override
	public void removeFromGameObject() {
		this.getGameObject().getPhysx().unsetRigidBody();
	}
	
	public void setKinematicTarget(Vector3f pos, Quaternionf rot) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, pos.x, pos.y, pos.z));
			pose.setQ(PxQuat.createAt(mem, MemoryStack::nmalloc, rot.x,	rot.y, rot.z, rot.w));
			this.rigid.setKinematicTarget(pose);
		}
	}
	
	public void getKinematicTarget(Vector3f destPos, Quaternionf destRot) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			PxVec3 p = pose.getP();
			PxQuat q = pose.getQ();
			destPos.set(p.getX(), p.getY(), p.getZ());
			destRot.set(q.getX(), q.getY(), q.getZ(), q.getW());
		}
	}
	
	public void setLinearDamping(float linearDamping) {
		this.rigid.setLinearDamping(linearDamping);
	}
	
	public float getLinearDamping() {
		return this.rigid.getLinearDamping();
	}
	
	public void setAngularDamping(float angularDamping) {
		this.rigid.setAngularDamping(angularDamping);
	}
	
	public float getAngularDamping() {
		return this.rigid.getAngularDamping();
	}
	
	public void setMaxAngularVelocity(float maxAngularVelocity) {
		this.rigid.setMaxAngularVelocity(maxAngularVelocity);
	}
	
	public float getMaxAngularVelocity() {
		return this.rigid.getMaxAngularVelocity();
	}
	
	public void setXPositionLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_X, lock);
	}
	
	public void setYPositionLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Y, lock);
	}
	
	public void setZPositionLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_LINEAR_Z, lock);
	}
	
	public void setkXRotationLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, lock);
	}
	
	public void setYRotationLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, lock);
	}
	
	public void setZRotationLock(boolean lock) {
		this.rigid.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, lock);
	}
	
	public void setCenterOfMass(Vector3f pos) {
		this.setCenterOfMass(pos, new Quaternionf());
	}
	
	public void setCenterOfMass(Vector3f pos, Quaternionf quat) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, pos.x, pos.y, pos.z));
			this.rigid.setCMassLocalPose(pose);
		}
	}
	
	public void getCenterOfMass(Vector3f destPos, Quaternionf destRot) {
		PxTransform pose = this.rigid.getCMassLocalPose();
		PxVec3 p = pose.getP();
		PxQuat q = pose.getQ();
		destPos.set(p.getX(), p.getY(), p.getZ());
		destRot.set(q.getX(), q.getY(), q.getZ(), q.getW());
	}
	
	public void setMass(float mass) {
		this.rigid.setMass(mass);
	}
	
	public float getMass() {
		return this.rigid.getMass();
	}
	
	public float getInverseMass() {
		return this.rigid.getInvMass();
	}

	@Override
	public PxRigidActor getPxRigidActor() {
		return this.rigid;
	}

	@Override
	public void pxRelease() {
		
	}
	
	@Override
	public void addShapes(Collection<Collider> shapes) {
		RigidBody.super.addShapes(shapes);
		//TODO
	}
	
	@Override
	public void removeShapes(Collection<Collider> shapes) {
		RigidBody.super.removeShapes(shapes);
		//TODO
	}
	
	@Override
	public void addShape(Collider collider) {
		RigidBody.super.addShape(collider);
		//TODO
	}
	
	@Override
	public void removeShape(Collider collider) {
		RigidBody.super.removeShape(collider);
		//TODO
	}
	
}
