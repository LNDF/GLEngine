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
import physx.extensions.PxRigidBodyExt;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;
import physx.physics.PxRigidDynamicLockFlagEnum;

public class DynamicRigidBody extends Component implements EngineResource, RigidBody {
	
	private PxRigidDynamic rigid;
	
	private boolean autoComputeCMassAndInertia = true;
	
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
		PhysXManager.addRigidToGameObjectMapping(this.rigid, this.getGameObject());
	}
	
	@Override
	public void removeFromGameObject() {
		PhysXManager.removeRigidToGameobjectMapping(this.rigid, this.getGameObject());
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
	
	public void setMomentOfInertia(Vector3f m) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			this.rigid.setMassSpaceInertiaTensor(PxVec3.createAt(mem, MemoryStack::nmalloc, m.x, m.y, m.z));
		}
	}
	
	public Vector3f getMmoentOfInertia() {
		PxVec3 m = this.rigid.getMassSpaceInertiaTensor();
		return new Vector3f(m.getX(), m.getY(), m.getZ());
	}
	
	public Vector3f getInvertedMomentOfInertia() {
		PxVec3 m = this.rigid.getMassSpaceInvInertiaTensor();
		return new Vector3f(m.getX(), m.getY(), m.getZ());
	}
	
	public Vector3f getLinearVelocity() {
		PxVec3 v = this.rigid.getLinearVelocity();
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}
	
	public void setLinearVelocity(Vector3f velocity) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, velocity.x, velocity.y, velocity.z);
			this.rigid.setLinearVelocity(v);
		}
	}
	
	public Vector3f getAngularVelocity() {
		PxVec3 v = this.rigid.getAngularVelocity();
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}
	
	public void setAngularVelocity(Vector3f velocity) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, velocity.x, velocity.y, velocity.z);
			this.rigid.setAngularVelocity(v);
		}
	}
	
	public void addForce(Vector3f force) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, force.x, force.y, force.z);
			this.rigid.addForce(v, PxForceModeEnum.eFORCE);
		}
	}
	
	public void addForceImpulse(Vector3f force) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, force.x, force.y, force.z);
			this.rigid.addForce(v, PxForceModeEnum.eIMPULSE);
		}
	}
	
	public void addTorque(Vector3f torque) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, torque.x, torque.y, torque.z);
			this.rigid.addTorque(v, PxForceModeEnum.eFORCE);
		}
	}
	
	public void addTorqueImpulse(Vector3f torque) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 v = PxVec3.createAt(mem, MemoryStack::nmalloc, torque.x, torque.y, torque.z);
			this.rigid.addTorque(v, PxForceModeEnum.eIMPULSE);
		}
	}
	
	public void clearForce() {
		this.rigid.clearForce(PxForceModeEnum.eFORCE);
	}
	
	public void clearForceImpulse() {
		this.rigid.clearForce(PxForceModeEnum.eIMPULSE);
	}
	
	public void clearTorque() {
		this.rigid.clearTorque(PxForceModeEnum.eFORCE);
	}
	
	public void clearTorqueImpulse() {
		this.rigid.clearTorque(PxForceModeEnum.eIMPULSE);
	}
	
	public void clearAllForces() {
		this.clearForce();
		this.clearForceImpulse();
		this.clearTorque();
		this.clearTorqueImpulse();
	}
	
	public boolean isKinematic() {
		return this.rigid.getRigidBodyFlags().isSet(PxRigidBodyFlagEnum.eKINEMATIC);
	}
	
	public void setKinematic(boolean kinematic) {
		this.rigid.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, kinematic);
	}
	
	public float getMaxDepenetrationVelocity() {
		return this.rigid.getMaxDepenetrationVelocity();
	}
	
	public void setMaxDepenetrationVelocity(float velocity) {
		this.rigid.setMaxDepenetrationVelocity(velocity);
	}
	
	public float getMaxContactImpulse() {
		return this.rigid.getMaxContactImpulse();
	}
	
	public void setMaxContactImpulse(float impulse) {
		this.rigid.setMaxContactImpulse(impulse);
	}
	
	public void computeCMassAndInertia() {
		PxRigidBodyExt.setMassAndUpdateInertia(this.rigid, this.rigid.getMass());
	}
	
	public void setAutoComputeCMassAndInertia(boolean compute) {
		this.autoComputeCMassAndInertia = compute;
	}
	
	public boolean getAutoComputeCMassAndInertia() {
		return this.autoComputeCMassAndInertia;
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
		if (this.autoComputeCMassAndInertia) this.computeCMassAndInertia();
	}
	
	@Override
	public void removeShapes(Collection<Collider> shapes) {
		RigidBody.super.removeShapes(shapes);
		if (this.autoComputeCMassAndInertia) this.computeCMassAndInertia();
	}
	
	@Override
	public void addShape(Collider collider) {
		RigidBody.super.addShape(collider);
		if (this.autoComputeCMassAndInertia) this.computeCMassAndInertia();
	}
	
	@Override
	public void removeShape(Collider collider) {
		RigidBody.super.removeShape(collider);
		if (this.autoComputeCMassAndInertia) this.computeCMassAndInertia();
	}

	@Override
	public void shapeUpdated() {
		if (this.autoComputeCMassAndInertia) this.computeCMassAndInertia();
	}
	
}
