package com.lndf.glengine.physics;

import physx.physics.JavaSimulationEventCallback;
import physx.physics.PxContactPair;
import physx.physics.PxContactPairHeader;
import physx.physics.PxTriggerPair;

public class PhysXSimulationCallbacks extends JavaSimulationEventCallback {
	
	@Override
	public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
		
	}
	
	@Override
	public void onTrigger(PxTriggerPair pairs, int count) {
		
	}
	
}
