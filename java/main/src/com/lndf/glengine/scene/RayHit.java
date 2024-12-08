package com.lndf.glengine.scene;

import org.joml.Vector3f;

import com.lndf.glengine.engine.PhysXManager;

import physx.physics.PxRaycastHit;

public class RayHit {

    private boolean hit = false;
    private float distance = 0;
    private Vector3f point = new Vector3f();
    private GameObject gameObject = null;

    public RayHit(PxRaycastHit hit) {
        if (hit != null) {
            this.hit = true;
            this.distance = hit.getDistance();
            this.point = new Vector3f(hit.getPosition().getX(), hit.getPosition().getY(), hit.getPosition().getZ());
            this.gameObject = PhysXManager.getGameObjectFromActor(hit.getActor());
        }
    }

    public boolean isHit() {
        return hit;
    }

    public float getDistance() {
        return distance;
    }

    public Vector3f getPoint() {
        return point;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

}
