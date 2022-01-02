package com.lndf.glengine.scene.components.physics;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.physics.PhysicalMaterial;

import physx.character.PxBoxController;
import physx.character.PxBoxControllerDesc;
import physx.character.PxCapsuleController;
import physx.character.PxCapsuleControllerDesc;
import physx.character.PxController;

public class BoxCharacterController extends CharacterController {
	
	private PxBoxController ctt;
	
	private float halfHeight;
	private float halfForward;
	private float halfSide;
	
	@Override
	public PxController getPxCtt() {
		return ctt;
	}
	
	public BoxCharacterController(PhysicalMaterial material, float halfSide, float halfHeight, float halfForward) {
		super(material);
		this.halfHeight = halfHeight;
		this.halfForward = halfForward;
		this.halfSide = halfSide;
	}
	
	public float getHalfHeight() {
		if (this.ctt != null) return this.ctt.getHalfHeight();
		return halfHeight;
	}
	
	public void setHalfHeight(float halfHeight) {
		if (this.ctt != null) this.ctt.setHalfHeight(halfHeight);
		this.halfHeight = halfHeight;
	}
	
	public float getHalfSide() {
		if (this.ctt != null) return this.ctt.getHalfSideExtent();
		return halfSide;
	}
	
	public void setHalfSide(float halfSide) {
		if (this.ctt != null) this.ctt.setHalfSideExtent(halfSide);
		this.halfSide = halfSide;
	}
	
	public float getHalfForward() {
		if (this.ctt != null) return this.ctt.getHalfForwardExtent();
		return halfForward;
	}
	
	public void setHalfForward(float halfForward) {
		if (this.ctt != null) this.ctt.setHalfForwardExtent(halfForward);
		this.halfForward = halfForward;
	}
	
	@Override
	protected void pxCreate() {
		super.pxCreate();
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxBoxControllerDesc desc = PxBoxControllerDesc.createAt(mem, MemoryStack::nmalloc);
			this.configureBaseControllerDesc(desc);
			desc.setHalfForwardExtent(this.halfForward);
			desc.setHalfHeight(this.halfHeight);
			desc.setHalfSideExtent(this.halfSide);
			this.ctt = PxBoxController.wrapPointer(this.cttManager.createController(desc).getAddress());
		}
	}
	
	@Override
	protected void pxDestroy() {
		this.halfHeight = this.ctt.getHalfHeight();
		this.halfSide = this.ctt.getHalfSideExtent();
		this.halfForward = this.ctt.getHalfForwardExtent();
		super.pxDestroy();
	}
}
