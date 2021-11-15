package com.lndf.glengine.scene;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

public class GameObject {
	
	private String name;
	
	private ConcurrentHashMap<Class<? extends Component>, Component> components = new ConcurrentHashMap<Class<? extends Component>, Component>();
	private KeySetView<Component, Boolean> componentsToDestroy = ConcurrentHashMap.newKeySet();
	
	private Scene scene = null;
	
	private HashSet<GameObject> children = new HashSet<GameObject>();
	private GameObject parent;
	
	private Transform transform = new Transform(this);
	private GameObjectPhysXManager physx = new GameObjectPhysXManager(this);
	
	public GameObject(String name) {
		this.name = name;
	}
	
	public GameObject() {
		this("");
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void addComponent(Component component) {
		if (component.getGameObject() != null) return;
		Class<? extends Component> c = component.getClass();
		this.components.put(c, component);
		boolean isNew = !this.componentsToDestroy.contains(component);
		this.componentsToDestroy.remove(component);
		component.setGameObject(this);
		if (isNew) {
			component.addToGameObject();
			if (this.getScene() != null) component.addToScene();
		}
	}
	
	public Component getComponent(Class<? extends Component> c) {
		return this.components.get(c);
	}
	
	public Collection<Component> getComponents() {
		return Collections.unmodifiableCollection(this.components.values());
	}
	
	public void removeCompopnent(Class<? extends Component> c) {
		if (this.components.containsKey(c)) {
			Component comp = this.components.get(c);
			this.componentsToDestroy.add(comp);
			this.components.remove(c);
		}
	}
	
	public void destroyComponents() {
		for (Component c : this.componentsToDestroy) {
			if (this.getScene() != null) c.removeFromScene();
			c.removeFromGameObject();
			c.setGameObject(null);
		}
		this.componentsToDestroy.clear();
	}
	
	public void destroy() {
		for (GameObject obj : this.children) {
			obj.destroy();
			obj.setParent(null);
		}
		if (this.getScene() == null) {
			for (Component comp : this.components.values()) {
				comp.removeFromScene();
			}
		}
		for (Component comp : this.components.values()) {
			comp.removeFromGameObject();
			comp.setGameObject(null);
		}
		this.destroyComponents();
		if (this.getScene() != null) {
			this.getScene().removeObject(this);
		}
		this.children.clear();
		this.components.clear();
		this.physx.destroy();
	}
	
	public GameObject getParent() {
		return this.parent;
	}
	
	private void setParent(GameObject parent) {
		this.transform.clearLocalCache();
		this.parent = parent;
	}
	
	public GameObject search(String searchingName) {
		if (this.name == searchingName) return this;
		for (GameObject child : this.children) {
			GameObject result = child.search(searchingName);
			if (result != null) return result;
		}
		return null;
	}
	
	public Set<GameObject> getChildren() {
		return Collections.unmodifiableSet(this.children);
	}
	
	public void addChild(GameObject child) {
		if (child.getParent() == null) {
			Scene scene = this.getScene();
			if (scene != null) scene.addObject(child);
			this.children.add(child);
			child.setParent(this);
		}
	}
	
	public void removeChild(GameObject child) {
		if (this.children.contains(child)) {
			Scene scene = child.getScene();
			this.children.remove(child);
			child.setParent(null);
			if (scene != null) scene.removeObject(child);
		}
	}

	public Transform getTransform() {
		return this.transform;
	}
	
	public GameObjectPhysXManager getPhysx() {
		return physx;
	}
	
}
