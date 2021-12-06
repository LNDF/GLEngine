package com.lndf.glengine.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameObject {
	
	private String name;
	
	private ConcurrentHashMap<Class<? extends Component>, ConcurrentLinkedQueue<Component>> components = new ConcurrentHashMap<Class<? extends Component>, ConcurrentLinkedQueue<Component>>();
	private KeySetView<Component, Boolean> componentsToDestroy = ConcurrentHashMap.newKeySet();
	private ArrayList<Component> cachedAllComponents;
	
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
		if (this.scene != null) {
			for (ConcurrentLinkedQueue<Component> tcomp : this.components.values()) {
				for (Component comp : tcomp) {
					comp.removeFromScene();
				}
			}
			this.scene.clearComponentCaches();
		}
		this.physx.updateScene(scene);
		this.scene = scene;
		if (this.scene != null) {
			this.scene.clearComponentCaches();
			for (ConcurrentLinkedQueue<Component> tcomp : this.components.values()) {
				for (Component comp : tcomp) {
					comp.addToScene();
				}
			}
		}
	}
	
	public void addComponent(Component component) {
		if (component.getGameObject() != null) return;
		Class<? extends Component> c = component.getClass();
		ConcurrentLinkedQueue<Component> tcomp;
		if (!this.components.containsKey(c)) {
			tcomp = new ConcurrentLinkedQueue<Component>();;
			this.components.put(c, tcomp);
		} else {
			tcomp = this.components.get(c);
		}
		tcomp.add(component);
		this.cachedAllComponents = null;
		if (this.scene != null) this.scene.clearComponentCaches();
		boolean isNew = !this.componentsToDestroy.contains(component);
		this.componentsToDestroy.remove(component);
		component.setGameObject(this);
		if (isNew) {
			component.addToGameObject();
			if (this.getScene() != null) component.addToScene();
		}
	}
	
	public Component getComponent(Class<? extends Component> c) {
		ConcurrentLinkedQueue<Component> t = this.components.get(c);
		if (t != null) return t.peek();
		return null;
	}
	
	public Collection<Component> getComponents(Class<? extends Component> c) {
		ConcurrentLinkedQueue<Component> t = this.components.get(c);
		if (t != null) return Collections.unmodifiableCollection(t);
		return Collections.unmodifiableCollection(new ConcurrentLinkedQueue<Component>());
	}
	
	public Collection<Component> getComponents() {
		if (this.cachedAllComponents != null) Collections.unmodifiableCollection(this.cachedAllComponents);
		ArrayList<Component> comps = new ArrayList<Component>();
		for (ConcurrentLinkedQueue<Component> t : this.components.values()) {
			comps.addAll(t);
		}
		this.cachedAllComponents = comps;
		return Collections.unmodifiableCollection(comps);
	}
	
	public void removeCompopnent(Component component) {
		Class<? extends Component> c = component.getClass();
		if (this.components.containsKey(c)) {
			ConcurrentLinkedQueue<Component> tcomp = this.components.get(c);
			this.componentsToDestroy.add(component);
			tcomp.remove(component);
			this.cachedAllComponents = null;
			if (this.scene != null) this.scene.clearComponentCaches();
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
			for (ConcurrentLinkedQueue<Component> tcomp : this.components.values()) {
				for (Component comp : tcomp) {
					comp.removeFromScene();
				}
			}
		}
		for (ConcurrentLinkedQueue<Component> tcomp : this.components.values()) {
			for (Component comp : tcomp) {
				comp.removeFromGameObject();
				comp.setGameObject(null);
			}
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
			if (this.physx.hasCustomRigid()) {
				child.physx.notifyChildrenRigidChange(this);
			} else if (this.physx.getParentRigidOwner() != null) {
				child.physx.notifyChildrenRigidChange(this.physx.getParentRigidOwner());
			}
			if (scene != null) scene.addObject(child, false);
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
			if (this.physx.hasCustomRigid() || this.physx.getParentRigidOwner() != null) {
				child.physx.notifyChildrenRigidChange(null);
			}
		}
	}

	public Transform getTransform() {
		return this.transform;
	}
	
	public GameObjectPhysXManager getPhysx() {
		return physx;
	}
	
}
