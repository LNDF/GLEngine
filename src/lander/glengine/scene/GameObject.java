package lander.glengine.scene;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameObject {
	
	private HashMap<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private HashMap<Class<? extends Component>, Component> componentsToAdd = new HashMap<Class<? extends Component>, Component>();
	private HashMap<Class<? extends Component>, Component> componentsToRemove = new HashMap<Class<? extends Component>, Component>();
	
	private Scene scene = null;
	
	private HashSet<GameObject> children = new HashSet<GameObject>();
	private GameObject parent;
	
	private Transform transform = new Transform(this);
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void addComponent(Component component) {
		if (component.getGameObject() != null) return;
		Class<? extends Component> c = component.getClass();
		this.componentsToAdd.put(c, component);
		this.componentsToRemove.remove(c);
		component.setGameObject(this);
		component.start();
	}
	
	public Component getComponent(Class<? extends Component> c) {
		return this.components.get(c);
	}
	
	public Collection<Component> getComponents() {
		return Collections.unmodifiableCollection(this.components.values());
	}
	
	public void removeCompopnent(Class<? extends Component> c) {
		if (this.components.containsKey(c)) {
			this.componentsToRemove.put(c, this.components.get(c));
		}
		this.componentsToAdd.remove(c);
	}
	
	public void addAndRemoveComponents() {
		if (this.getScene() != null && (this.componentsToAdd.size() > 0 || this.componentsToRemove.size() > 0)) {
			this.getScene().clearComponentCaches();
			this.components.putAll(this.componentsToAdd);
			for (Map.Entry<Class<? extends Component>, Component> entry : this.componentsToRemove.entrySet()) {
				Class<? extends Component> cc = entry.getKey();
				Component c = entry.getValue();
				if (c != null) {
					c.destroy();
					c.setGameObject(null);
				}
				this.components.remove(cc);
			}
			this.componentsToAdd.clear();
			this.componentsToRemove.clear();
		}
	}
	
	public void destroy() {
		if (this.getScene() != null) {
			this.getScene().removeObject(this);
		}
		for (GameObject obj : this.children) {
			obj.destroy();
			obj.setParent(null);
		}
		this.children.clear();
		this.componentsToRemove.putAll(this.components);
		this.componentsToAdd.clear();
	}
	
	public GameObject getParent() {
		return this.parent;
	}
	
	private void setParent(GameObject parent) {
		this.parent = parent;
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
}
