package com.lndf.glengine.engine;

import physx.PxTopLevelFunctions;
import physx.common.PxDefaultErrorCallback;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.cooking.PxCooking;
import physx.cooking.PxCookingParams;
import physx.extensions.PxDefaultAllocator;
import physx.physics.PxPhysics;

public class PhysXManager {
	
	public static final int PX_PHYSX_VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();
	
	private static PxDefaultAllocator defaultAllocator;
	private static PxDefaultErrorCallback defaultErrorCallback;
	private static PxFoundation foundation;
	private static PxPhysics physics;
	private static PxCookingParams cookingParams;
	private static PxCooking cooking;
	
	private static PxTolerancesScale toleranceScale;
	
	public static void start() {
		defaultAllocator = new PxDefaultAllocator();
		defaultErrorCallback = new PxDefaultErrorCallback();
		foundation = PxTopLevelFunctions.CreateFoundation(PX_PHYSX_VERSION, defaultAllocator, defaultErrorCallback);
		toleranceScale = new PxTolerancesScale();
		physics = PxTopLevelFunctions.CreatePhysics(PX_PHYSX_VERSION, foundation, toleranceScale);
		cookingParams = new PxCookingParams(toleranceScale);
		cooking = PxTopLevelFunctions.CreateCooking(PX_PHYSX_VERSION, foundation, cookingParams);
	}
	
	public static void stop() {
		cooking.release();
		cookingParams.destroy();
		physics.release();
		toleranceScale.destroy();
		foundation.release();
		defaultAllocator.destroy();
		defaultErrorCallback.destroy();
	}

	public static PxFoundation getFoundation() {
		return foundation;
	}

	public static PxPhysics getPhysics() {
		return physics;
	}

	public static PxCookingParams getCookingParams() {
		return cookingParams;
	}

	public static PxCooking getCooking() {
		return cooking;
	}

	public static PxTolerancesScale getToleranceScale() {
		return toleranceScale;
	}
	
}
