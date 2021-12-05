package com.lndf.glengine.physics;

import java.util.Collection;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;

import physx.common.PxVec3;
import physx.physics.JavaSimulationEventCallback;
import physx.physics.PxActor;
import physx.physics.PxContactPair;
import physx.physics.PxContactPairHeader;
import physx.physics.PxContactPairPoint;
import physx.physics.PxPairFlagEnum;
import physx.physics.PxPairFlags;
import physx.physics.PxTriggerPair;
import physx.support.TypeHelpers;

public class PhysXSimulationCallbacks extends JavaSimulationEventCallback {
	
	private static int CONTACT_POINT_SIZEOF = 0;
	
	static {
		CONTACT_POINT_SIZEOF += 3 * 4; //1 float and 2 U32
		CONTACT_POINT_SIZEOF += PxVec3.SIZEOF * 3;
	}
	
	@Override
	public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			PxActor a0 = pairHeader.getActors(0);
			PxActor a1 = pairHeader.getActors(1);
			GameObject object0 = PhysXManager.getGameObjectFromActor(a0);
			GameObject object1 = PhysXManager.getGameObjectFromActor(a1);
			long pointBufferPtr = mem.nmalloc(64 * CONTACT_POINT_SIZEOF);
			PxContactPairPoint buffer = PxContactPairPoint.wrapPointer(pointBufferPtr);
			for (int i = 0; i < nbPairs; i++) {
				PxContactPair pair = TypeHelpers.getContactPairAt(pairs, i);
				int nbContacts = pair.extractContacts(buffer, 64);
				ContactInfo c01[] = new ContactInfo[nbContacts], c10[] = new ContactInfo[nbContacts];
				for (int j = 0; j < nbContacts; j++) {
					PxContactPairPoint contact = PxContactPairPoint.wrapPointer(pointBufferPtr + CONTACT_POINT_SIZEOF * j);
					PxVec3 position = contact.getPosition();
					PxVec3 normal = contact.getNormal();
					float distance = contact.getSeparation();
					Vector3f pos10 = new Vector3f(position.getX(), position.getY(), position.getZ());
					Vector3f normal10 = new Vector3f(normal.getX(), normal.getY(), normal.getZ());
					Vector3f pos01 = new Vector3f(pos10);
					Vector3f normal01 = new Vector3f(normal10).mul(-1);
					c01[j] = new ContactInfo(pos01, normal01, distance);
					c10[j] = new ContactInfo(pos10, normal10, distance);
				}
				PxPairFlags events = pair.getEvents();
				Collection<Component> comp0 = object0.getComponents();
				Collection<Component> comp1 = object1.getComponents();
				if (events.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_FOUND)) {
					for (Component comp : comp0) {
						comp.collisionEnter(c01, object1);
					}
					for (Component comp : comp1) {
						comp.collisionEnter(c10, object0);
					}
				} else if (events.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_PERSISTS)) {
					for (Component comp : comp0) {
						comp.collisionPersist(c01, object1);
					}
					for (Component comp : comp1) {
						comp.collisionPersist(c10, object0);
					}
				} else if (events.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_LOST)) {
					for (Component comp : comp0) {
						comp.collisionExit(c01, object1);
					}
					for (Component comp : comp1) {
						comp.collisionExit(c10, object0);
					}
				}
			}
		}
	}
	
	@Override
	public void onTrigger(PxTriggerPair pairs, int count) {
		for (int i = 0; i < count; i++) {
			PxTriggerPair pair = TypeHelpers.getTriggerPairAt(pairs, i);
			PxActor actor0 = pair.getTriggerActor();
			PxActor actor1 = pair.getOtherActor();
			GameObject object0 = PhysXManager.getGameObjectFromActor(actor0);
			GameObject object1 = PhysXManager.getGameObjectFromActor(actor1);
			int status = pair.getStatus();
			Collection<Component> comp0 = object0.getComponents();
			Collection<Component> comp1 = object1.getComponents();
			if ((status & PxPairFlagEnum.eNOTIFY_TOUCH_FOUND) != 0) {
				for (Component comp : comp0) {
					comp.triggerEnter(object1);
				}
				for (Component comp : comp1) {
					comp.triggerEnter(object0);
				}
			} else if ((status & PxPairFlagEnum.eNOTIFY_TOUCH_PERSISTS) != 0) {
				for (Component comp : comp0) {
					comp.triggerPersist(object1);
				}
				for (Component comp : comp1) {
					comp.triggerPersist(object0);
				}
			} else if ((status & PxPairFlagEnum.eNOTIFY_TOUCH_LOST) != 0) {
				for (Component comp : comp0) {
					comp.triggerExit(object1);
				}
				for (Component comp : comp1) {
					comp.triggerExit(object0);
				}
			}
		}
	}
	
}
