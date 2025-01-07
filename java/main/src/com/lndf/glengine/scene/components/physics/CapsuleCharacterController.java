package com.lndf.glengine.scene.components.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.physics.PhysicalMaterial;

import physx.character.PxCapsuleClimbingModeEnum;
import physx.character.PxCapsuleController;
import physx.character.PxCapsuleControllerDesc;
import physx.character.PxController;

public class CapsuleCharacterController extends CharacterController {

	private PxCapsuleController ctt;
	
	private float radius = 0.0f;
	private float height = 0.0f;
	private boolean constrainedClimbing = false;
	
	@Override
	public PxController getPxCtt() {
		return ctt;
	}
	
	public CapsuleCharacterController(PhysicalMaterial material, float radius, float height) {
		super(material);
		this.height = height;
		this.radius = radius;
	}

	public float getRadius() {
		if (this.ctt != null) return this.ctt.getRadius();
		return radius;
	}

	public void setRadius(float radius) {
		if (this.ctt != null) this.ctt.setRadius(radius);
		this.radius = radius;
	}

	public float getHeight() {
		if (this.ctt != null) return this.ctt.getHeight();
		return height;
	}

	public void setHeight(float height) {
		if (this.ctt != null) this.ctt.setHeight(height);
		this.height = height;
	}

	public boolean isConstrainedClimbing() {
		if (this.ctt != null) return this.ctt.getClimbingMode() == PxCapsuleClimbingModeEnum.eCONSTRAINED;
		return constrainedClimbing;
	}

	public void setConstrainedClimbing(boolean constrainedClimbing) {
		if (this.ctt != null) this.ctt.setClimbingMode(constrainedClimbing ? PxCapsuleClimbingModeEnum.eCONSTRAINED : PxCapsuleClimbingModeEnum.eEASY);
		this.constrainedClimbing = constrainedClimbing;
	}
	
	@Override
	protected void pxCreate() {
		super.pxCreate();
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxCapsuleControllerDesc desc = PxCapsuleControllerDesc.createAt(mem, MemoryStack::nmalloc);
			this.configureBaseControllerDesc(desc);
			desc.setRadius(this.radius);
			desc.setHeight(this.height);
			this.ctt = PxCapsuleController.wrapPointer(this.cttManager.createController(desc).getAddress());
		}
	}
	
	@Override
	protected void pxDestroy() {
		this.height = this.ctt.getHeight();
		this.radius = this.ctt.getRadius();
		super.pxDestroy();
		this.ctt = null;
	}
	
}
