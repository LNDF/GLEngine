package com.lndf.glengine.scene;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import com.lndf.glengine.engine.SceneManager;
import com.lndf.glengine.engine.Task;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.lighting.PointLight;
import com.lndf.glengine.scene.components.lighting.Spotlight;

public class Scene {
	
	private KeySetView<GameObject, Boolean> gameObjects = ConcurrentHashMap.newKeySet();
	private KeySetView<GameObject, Boolean> gameObjectsToDestroy = ConcurrentHashMap.newKeySet();
	
	private float ambientLight;
	
	private HashSet<Task> updateTasks = new HashSet<Task>();
	
	//caches
	private HashSet<RenderComponent> renderComponentCache = null;
	private HashSet<DirectionalLight> directionalLightCache = null;
	private HashSet<PointLight> pointLightCache = null;
	private HashSet<Spotlight> spotlightCache = null;
	
	public Scene() {
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
		if (object.getParent() != null) return;
		Scene old = object.getScene();
		if (old != null) old.removeObject(object);
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
			obj.destroyComponents();
			obj.getTransform().checkCache();
		}
		for (Task task : this.updateTasks) {
			task.execute();
		}
		for (GameObject obj : this.gameObjectsToDestroy) {
			obj.setScene(null);
		}
	}
	
	public void destroy() {
		this.unsubscribeFromUpdates();
		for (GameObject obj : this.gameObjects) {
			obj.destroy();
			obj.setScene(null);
		}
		this.gameObjects.clear();
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
	
	public void addUpdateTask(Task task) {
		this.updateTasks.add(task);
	}
	
	public void removeTask(Task task) {
		this.updateTasks.remove(task);
	}
	
}
