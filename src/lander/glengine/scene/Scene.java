package lander.glengine.scene;

import java.util.HashSet;

import lander.glengine.engine.SceneManager;
import lander.glengine.scene.components.lighting.DirectionalLight;
import lander.glengine.scene.components.lighting.PointLight;
import lander.glengine.scene.components.lighting.Spotlight;

public class Scene {
	
	private HashSet<GameObject> gameObjects = new HashSet<GameObject>();
	private HashSet<GameObject> objectsToAdd = new HashSet<GameObject>();
	private HashSet<GameObject> objectsToRemove = new HashSet<GameObject>();
	
	private float ambientLight;
	
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
		this.objectsToAdd.add(object);
		this.objectsToRemove.remove(object);
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
		if (this.gameObjects.contains(object)) {
			this.objectsToRemove.add(object);
		}
		this.objectsToAdd.remove(object);
		for (GameObject child : object.getChildren()) {
			this._removeObject(child);
		}
	}
	
	public HashSet<GameObject> getGameObjects() {
		return this.gameObjects;
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
	
	public void addAndRemoveObjects() {
		if (this.objectsToAdd.size() > 0 || this.objectsToRemove.size() > 0) {
			this.clearComponentCaches();
			this.gameObjects.addAll(this.objectsToAdd);
			this.gameObjects.removeAll(this.objectsToRemove);
			//TODO: WTF!!!
			//for (GameObject obj : this.objectsToAdd) {
			//	obj.addAndRemoveComponents();
			//}
			for (GameObject obj : this.objectsToRemove) {
				obj.setScene(null);
			}
			this.objectsToAdd.clear();
			this.objectsToRemove.clear();
		}
	}
	
	public void update() {
		for (GameObject obj : this.gameObjects) {
			for (Component comp : obj.getComponents()) {
				comp.update();
			}
			obj.addAndRemoveComponents();
		}
		this.addAndRemoveObjects();
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
	
}
