package com.lndf.glengine.engine;

import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.physics.PhysXSimulationCallbacks;
import com.lndf.glengine.scene.GameObject;

import physx.PxTopLevelFunctions;
import physx.common.PxDefaultErrorCallback;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.common.PxVec3;
import physx.cooking.PxCooking;
import physx.cooking.PxCookingParams;
import physx.extensions.PxDefaultAllocator;
import physx.physics.PxFilterData;
import physx.physics.PxPairFlagEnum;
import physx.physics.PxPhysics;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;
import physx.physics.PxSceneDesc;

public class PhysXManager {
	
	private static HashMap<Long, GameObject> rigidToGameObject = new HashMap<Long, GameObject>();
	
	public static final int PX_PHYSX_VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();
	
	private static PxDefaultAllocator defaultAllocator;
	private static PxDefaultErrorCallback defaultErrorCallback;
	private static PxFoundation foundation;
	private static PxPhysics physics;
	private static PxFilterData filterData;
	private static PxCookingParams cookingParams;
	private static PxCooking cooking;
	private static PxTolerancesScale toleranceScale;
	
	private static int cpuThreads = 4;
	private static double simulationTime = 1.0 / 60.0;
	private static double recoverSimulationTime = 1.0 / 60.0;
	private static int recoverTriggerMultiplier = 3;
	
	public static void start() {
		defaultAllocator = new PxDefaultAllocator();
		defaultErrorCallback = new PxDefaultErrorCallback();
		foundation = PxTopLevelFunctions.CreateFoundation(PX_PHYSX_VERSION, defaultAllocator, defaultErrorCallback);
		toleranceScale = new PxTolerancesScale();
		physics = PxTopLevelFunctions.CreatePhysics(PX_PHYSX_VERSION, foundation, toleranceScale);
		filterData = new PxFilterData(0, 0, 0, 0);
		filterData.setWord0(1);
		filterData.setWord1(0xffffffff);
		filterData.setWord2(
				PxPairFlagEnum.eNOTIFY_TOUCH_FOUND      |
				PxPairFlagEnum.eNOTIFY_TOUCH_LOST       |
				PxPairFlagEnum.eNOTIFY_TOUCH_PERSISTS   |
	
				PxPairFlagEnum.eNOTIFY_CONTACT_POINTS);
		filterData.setWord3(0);
		cookingParams = new PxCookingParams(toleranceScale);
		cooking = PxTopLevelFunctions.CreateCooking(PX_PHYSX_VERSION, foundation, cookingParams);
	}
	
	public static void stop() {
		cooking.release();
		cookingParams.destroy();
		filterData.destroy();
		physics.release();
		toleranceScale.destroy();
		foundation.release();
		defaultAllocator.destroy();
		defaultErrorCallback.destroy();
	}
	
	public static PxScene createScene(Vector3f gravity) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxSceneDesc desc = PxSceneDesc.createAt(mem, MemoryStack::nmalloc, physics.getTolerancesScale());
			//PxSceneFlags flags = PxSceneFlags.createAt(mem, MemoryStack::nmalloc, (1<<5) | (1<<6));
			desc.setSimulationEventCallback(new PhysXSimulationCallbacks());
			desc.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
			desc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(cpuThreads));
			desc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
			//desc.setFlags(flags);
			return physics.createScene(desc);
		}
	}

	public static PxFoundation getFoundation() {
		return foundation;
	}

	public static PxPhysics getPhysics() {
		return physics;
	}
	
	public static PxFilterData getDefaultFilterData() {
		return filterData;
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

	public static int getCpuThreads() {
		return cpuThreads;
	}

	public static void setCpuThreads(int cpuThreads) {
		PhysXManager.cpuThreads = cpuThreads;
	}

	public static double getSimulationTime() {
		return simulationTime;
	}

	public static void setSimulationTime(double simulationTime) {
		PhysXManager.simulationTime = simulationTime;
	}
	
	public static double getRecoverSimulationTime() {
		return recoverSimulationTime;
	}

	public static void setRecoverSimulationTime(double recoverSimulationTime) {
		PhysXManager.recoverSimulationTime = recoverSimulationTime;
	}

	public static int getRecoverTriggerMultiplier() {
		return recoverTriggerMultiplier;
	}

	public static void setRecoverTriggerMultiplier(int recoverTriggerMultiplier) {
		PhysXManager.recoverTriggerMultiplier = recoverTriggerMultiplier;
	}
	
	public static void addRigidToGameObjectMapping(PxRigidActor rigid, GameObject object) {
		PhysXManager.rigidToGameObject.put(rigid.getAddress(), object);
	}
	
	public static void removeRigidToGameobjectMapping(PxRigidActor rigid, GameObject object) {
		PhysXManager.rigidToGameObject.remove(rigid.getAddress());
	}
	
	public static GameObject getGameObjectFromRigid(PxRigidActor rigid) {
		return PhysXManager.rigidToGameObject.get(rigid.getAddress());
	}
	
}
