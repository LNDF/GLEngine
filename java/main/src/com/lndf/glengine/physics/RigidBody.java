package com.lndf.glengine.physics;

import java.util.Collection;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.scene.components.physics.Collider;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;

public interface RigidBody {
	
	public PxRigidActor getPxRigidActor();
	public void pxRelease();
	
	public default Quaternionf getPxRotation() {
		PxRigidActor rigid = this.getPxRigidActor();
		PxTransform t = rigid.getGlobalPose();
		PxQuat q = t.getQ();
		return new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW());
	}
	
	public default Vector3f getPxPosition() {
		PxRigidActor rigid = this.getPxRigidActor();
		PxTransform t = rigid.getGlobalPose();
		PxVec3 p = t.getP();
		return new Vector3f(p.getX(), p.getY(), p.getZ());
	}
	
	public default void addShapes(Collection<Collider> shapes) {
		PxRigidActor rigid = this.getPxRigidActor();
		for (Collider shape : shapes) {
			rigid.attachShape(shape.getPhysXShape());
		}
	}
	public default void removeShapes(Collection<Collider> shapes) {
		PxRigidActor rigid = this.getPxRigidActor();
		for (Collider shape : shapes) {
			rigid.detachShape(shape.getPhysXShape());
		}
	}
	
	public default void addShape(Collider collider) {
		PxRigidActor rigid = this.getPxRigidActor();
		rigid.attachShape(collider.getPhysXShape());
	}
	public default void removeShape(Collider collider) {
		PxRigidActor rigid = this.getPxRigidActor();
		rigid.attachShape(collider.getPhysXShape());
	}
	
	public default void setPxPose(Vector3f jp, Quaternionf jq) {
		PxRigidActor rigid = this.getPxRigidActor();
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
			pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, jp.x, jp.y, jp.z));
			pose.setQ(PxQuat.createAt(mem, MemoryStack::nmalloc, jq.x, jq.y, jq.z, jq.w));
			rigid.setGlobalPose(pose);
		}
	}
	
	
	
}
