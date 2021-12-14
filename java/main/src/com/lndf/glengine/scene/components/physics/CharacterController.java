package com.lndf.glengine.scene.components.physics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.physics.RigidBody;
import com.lndf.glengine.scene.Component;

import physx.NativeObject;
import physx.character.PxController;
import physx.character.PxControllerDesc;
import physx.character.PxControllerNonWalkableModeEnum;
import physx.character.PxExtendedVec3;
import physx.character.PxUserControllerHitReport;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;

public abstract class CharacterController extends Component implements EngineResource, RigidBody {
	
	private static final int PXEXTENDEDVEC3_SIZEOF = 24;
	
	//FIELDS
	private Vector3f upDirection = new Vector3f(0, 1, 0);
	private float slopeLimit = 0.707f;
	private float invisibleWallHeight = 0;
	private float maxJumpHeight = 0;
	private float contactOffset = 0.1f;
	private float stepOffset = 0.5f;
	private float density = 10.0f;
	private float scaleCoef = 0.8f;
	private boolean shouldSlide = false;
	//volumeGrowth = 1.5f
	//userData = 0
	//reportCallback = NULL
	//behaviourCallback = NULL
	//position = 0, 0, 0
	//registerDeclarationListener = true
	
	public abstract PxController getPxCtt();
	
	public void pxCreate() {
		Engine.addEngineResource(this);
	}
	
	public CharacterController() {
		Engine.addEngineResource(this);
	}
	
	@Override
	public void shapeUpdated() {}
	
	@Override
	public void pxRelease() {}
	
	@Override
	public PxRigidActor getPxRigidActor() {
		if (this.getPxCtt() == null) return null;
		return this.getPxCtt().getActor();
	}
	
	public void pxDestroy() {
		Engine.removeEngineResource(this);
		PxController ctt = this.getPxCtt();
		PxVec3 up = ctt.getUpDirection();
		this.upDirection.x = up.getX();
		this.upDirection.y = up.getY();
		this.upDirection.z = up.getZ();
		this.slopeLimit = ctt.getSlopeLimit();
		this.contactOffset = ctt.getContactOffset();
		this.stepOffset = ctt.getStepOffset();
		this.shouldSlide = ctt.getNonWalkableMode() == PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING;
		ctt.release();
	}

	@Override
	public void destroy() {
		if (this.getPxCtt() != null) {
			this.pxDestroy();
		}
	}
	
	protected void configureBaseControllerDesc(PxControllerDesc desc) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxVec3 up = PxVec3.createAt(mem, MemoryStack::nmalloc, this.upDirection.x, this.upDirection.y, this.upDirection.z);
			PxExtendedVec3 pos = PxExtendedVec3.wrapPointer(mem.nmalloc(PXEXTENDEDVEC3_SIZEOF));
			int nonWalkableMode = this.shouldSlide ? PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING : PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING;
			PxUserControllerHitReport report = PxUserControllerHitReport.wrapPointer(0);
			NativeObject user = NativeObject.wrapPointer(0);
			pos.setX(0);
			pos.setY(0);
			pos.setZ(0);
			desc.setSlopeLimit(this.slopeLimit);
			desc.setInvisibleWallHeight(this.invisibleWallHeight);
			desc.setMaxJumpHeight(this.maxJumpHeight);
			desc.setContactOffset(this.contactOffset);
			desc.setStepOffset(this.stepOffset);
			desc.setDensity(this.density);
			desc.setScaleCoeff(this.scaleCoef);
			desc.setNonWalkableMode(nonWalkableMode);
			desc.setVolumeGrowth(1.5f);
			desc.setUserData(user);
			desc.setReportCallback(report);
			desc.setRegisterDeletionListener(true);
		}
	}

	public Vector3f getUpDirection() {
		if (this.getPxCtt() != null) {
			PxVec3 up = this.getPxCtt().getUpDirection();
			return new Vector3f(up.getX(), up.getY(), up.getZ());
		}
		return upDirection;
	}

	public void setUpDirection(Vector3f upDirection) {
		if (this.getPxCtt() != null) {
			try (MemoryStack mem = MemoryStack.stackPush()) {
				PxVec3 up = PxVec3.createAt(mem, MemoryStack::nmalloc, this.upDirection.x, this.upDirection.y, this.upDirection.z);
				this.getPxCtt().setUpDirection(up);
			}
		}
		this.upDirection = upDirection;
	}

	public float getSlopeLimit() {
		if (this.getPxCtt() != null) return this.getPxCtt().getSlopeLimit();
		return slopeLimit;
	}

	public void setSlopeLimit(float slopeLimit) {
		if (this.getPxCtt() != null) this.getPxCtt().setSlopeLimit(slopeLimit);
		this.slopeLimit = slopeLimit;
	}

	public float getInvisibleWallHeight() {
		return invisibleWallHeight;
	}

	public void setInvisibleWallHeight(float invisibleWallHeight) {
		this.invisibleWallHeight = invisibleWallHeight;
		this.pxDestroy();
		this.pxCreate();
	}

	public float getMaxJumpHeight() {
		return maxJumpHeight;
	}

	public void setMaxJumpHeight(float maxJumpHeight) {
		this.maxJumpHeight = maxJumpHeight;
		this.pxDestroy();
		this.pxCreate();
	}

	public float getContactOffset() {
		if (this.getPxCtt() != null) this.getPxCtt().getContactOffset();
		return contactOffset;
	}

	public void setContactOffset(float contactOffset) {
		if (this.getPxCtt() != null) this.getPxCtt().setContactOffset(contactOffset);
		this.contactOffset = contactOffset;
	}

	public float getStepOffset() {
		if (this.getPxCtt() != null) return this.getPxCtt().getStepOffset();
		return stepOffset;
	}

	public void setStepOffset(float stepOffset) {
		if (this.getPxCtt()) this.getPxCtt().setStepOffset(stepOffset);
		this.stepOffset = stepOffset;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
		this.pxDestroy();
		this.pxCreate();
	}

	public float getScaleCoef() {
		return scaleCoef;
	}

	public void setScaleCoef(float scaleCoef) {
		this.scaleCoef = scaleCoef;
		this.pxDestroy();
		this.pxCreate();
	}

	public boolean isShouldSlide() {
		if (this.getPxCtt() != null) return this.getPxCtt().getNonWalkableMode() == PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING;
		return shouldSlide;
	}

	public void setShouldSlide(boolean shouldSlide) {
		if (this.getPxCtt() != null) this.getPxCtt().setNonWalkableMode(this.shouldSlide ? PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING : PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING);
		this.shouldSlide = shouldSlide;
	}
	
	m
	
}
