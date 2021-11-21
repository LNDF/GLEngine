package com.lndf.glengine.scene;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.engine.PhysXManager;
import com.lndf.glengine.engine.RunnableList;
import com.lndf.glengine.engine.SceneManager;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.lighting.PointLight;
import com.lndf.glengine.scene.components.lighting.Spotlight;

import physx.common.PxVec3;
import physx.physics.PxScene;

public class Scene {
	
	private KeySetView<GameObject, Boolean> gameObjects = ConcurrentHashMap.newKeySet();
	private KeySetView<GameObject, Boolean> gameObjectsToDestroy = ConcurrentHashMap.newKeySet();
	private KeySetView<GameObject, Boolean> rootGameObjects = ConcurrentHashMap.newKeySet();
	
	private float ambientLight;
	
	private PxScene physXScene;
	private double physXDelta = 0.0;
	private boolean physXRecovering = false;
	
	private RunnableList updateRunnables = new RunnableList();
	
	//caches
	private HashSet<RenderComponent> renderComponentCache = null;
	private HashSet<DirectionalLight> directionalLightCache = null;
	private HashSet<PointLight> pointLightCache = null;
	private HashSet<Spotlight> spotlightCache = null;
	
	public Scene() {
		this.physXScene = PhysXManager.createScene(new Vector3f(0, -9.81f, 0));
		this.ambientLight = 0.01f;
	}
	
	public Scene(float ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public void clearComponentCaches() {
		this.renderComponentCache = null;
		this.directionalLightCache = null;
		this.pointLightCache = null;
		this.spotlightCache = null;
	}
	
	public void addObject(GameObject object) {
		this.addObject(object, true);
	}
	
	public void addObject(GameObject object, boolean isRoot) {
		if (object.getParent() != null) return;
		Scene old = object.getScene();
		if (old != null) old.removeObject(object);
		if (isRoot) this.rootGameObjects.add(object);
		this._addObject(object);
	}
	
	private void _addObject(GameObject object) {
		this.clearComponentCaches();
		this.gameObjects.add(object);
		this.gameObjectsToDestroy.remove(object);
		object.setScene(this);
		for (GameObject child : object.getChildren()) {
			this._addObject(child);
		}
	}
	
	public void removeObject(GameObject object) {
		if (object.getParent() != null) return;
		this.rootGameObjects.remove(object);
		this._removeObject(object);
	}
	
	private void _removeObject(GameObject object) {
		this.clearComponentCaches();
		if (this.gameObjects.contains(object)) {
			this.gameObjects.remove(object);
			this.gameObjectsToDestroy.add(object);
		}
		for (GameObject child : object.getChildren()) {
			this._removeObject(child);
		}
	}
	
	public Set<GameObject> getGameObjects() {
		return Collections.unmodifiableSet(this.gameObjects);
	}
	
	public Set<GameObject> getRootGameObjects() {
		return Collections.unmodifiableSet(this.rootGameObjects);
	}
	
	public HashSet<RenderComponent> getRenderComponents() {
		if (this.renderComponentCache == null) {
			this.renderComponentCache = new HashSet<>();
			for (GameObject obj : this.gameObjects) {
				for (Component comp : obj.getComponents()) {
					if (comp instanceof RenderComponent) {
						this.renderComponentCache.add((RenderComponent) comp);
					}
				}
			}
		}
		return this.renderComponentCache;
	}
	
	public HashSet<DirectionalLight> getDirectionalLights() {
		if (this.directionalLightCache == null) {
			this.directionalLightCache = new HashSet<DirectionalLight>();
			for (GameObject obj : this.gameObjects) {
				for (Component comp : obj.getComponents()) {
					if (comp instanceof DirectionalLight) {
						this.directionalLightCache.add((DirectionalLight) comp);
					}
				}
			}
		}
		return this.directionalLightCache;
	}
	
	public HashSet<PointLight> getPointLights() {
		if (this.pointLightCache == null) {
			this.pointLightCache = new HashSet<PointLight>();
			for (GameObject obj : this.gameObjects) {
				for (Component comp : obj.getComponents()) {
					if (comp instanceof PointLight) {
						this.pointLightCache.add((PointLight) comp);
					}
				}
			}
		}
		return this.pointLightCache;
	}
	
	public HashSet<Spotlight> getSpotlights() {
		if (this.spotlightCache == null) {
			this.spotlightCache = new HashSet<Spotlight>();
			for (GameObject obj : this.gameObjects) {
				for (Component comp : obj.getComponents()) {
					if (comp instanceof Spotlight) {
						this.spotlightCache.add((Spotlight) comp);
					}
				}
			}
		}
		return this.spotlightCache;
	}
	
	public void update() {
		for (GameObject obj : this.gameObjects) {
			for (Component comp : obj.getComponents()) {
				comp.update();
			}
		}
		for (GameObject obj : this.gameObjects) {
			obj.destroyComponents();
		}
		this.updateRunnables.executeAll(false);
		for (GameObject obj : this.gameObjectsToDestroy) {
			obj.setScene(null);
		}
		double simulationTime = PhysXManager.getSimulationTime();
		double recoverSimulationTime = PhysXManager.getRecoverSimulationTime();
		int recoverTriggerMultiplier = PhysXManager.getRecoverTriggerMultiplier();
		this.physXDelta += DeltaTime.get();
		if (this.physXDelta >= simulationTime * recoverTriggerMultiplier) {
			this.physXRecovering = true;
		} else if (this.physXDelta < recoverSimulationTime) {
			this.physXRecovering = false;
		}
		if (this.physXRecovering) {
			simulationTime = recoverSimulationTime;
		}
		if (physXDelta >= simulationTime) {
			int steps = (int) (physXDelta / simulationTime);
			this.physXDelta %= simulationTime;
			for (GameObject obj : this.gameObjects) {
				obj.getPhysx().pushPoseToRigidBody();
			}
			for (int i = 0; i < steps; i++) {
				physXScene.simulate((float) simulationTime);
				physXScene.fetchResults(true);
			}
			for (GameObject obj : this.rootGameObjects) {
				this.pullPosesFromPhysX(obj);
			}
		}
	}
	
	private void pullPosesFromPhysX(GameObject object) {
		object.getPhysx().pullPoseFromRigidBody();
		for (GameObject child : object.getChildren()) {
			this.pullPosesFromPhysX(child);
		}
	}
	
	public void destroy() {
		this.unsubscribeFromUpdates();
		for (GameObject obj : this.gameObjects) {
			obj.setScene(null);
			obj.destroy();
		}
		this.gameObjects.clear();
		this.physXScene.release();
	}
	
	public void subscribeToUpdates() {
		SceneManager.scenes.add(this);
	}
	
	public void unsubscribeFromUpdates() {
		SceneManager.scenes.remove(this);
	}
	
	public float getAmbientLight() {
		return ambientLight;
	}

	public void setAmbientLight(float ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public PxScene getPhysXScene() {
		return physXScene;
	}
	
	public Vector3f getGravity() {
		PxVec3 pv = physXScene.getGravity();
		return new Vector3f(pv.getX(), pv.getY(), pv.getZ());
	}
	
	public void setGravity(Vector3f gravity) {
		try (MemoryStack mem = MemoryStack.stackPush()) {
			physXScene.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
		}
	}
	
	public void addUpdateRunnable(Runnable runnable) {
		this.updateRunnables.addRunnable(runnable);
	}
	
	public void removeRunnable(Runnable runnable) {
		this.updateRunnables.removeRunnable(runnable);
	}
	
	public GameObject searchObject(String searchingName) {
		for (GameObject obj : this.gameObjects) {
			if (obj.getName() == searchingName) return obj;
		}
		return null;
	}
	
}
