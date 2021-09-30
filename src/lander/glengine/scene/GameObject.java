package lander.glengine.scene;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GameObject {
	
	private HashMap<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private HashMap<Class<? extends Component>, Component> componentsToAdd = new HashMap<Class<? extends Component>, Component>();
	private HashMap<Class<? extends Component>, Component> componentsToRemove = new HashMap<Class<? extends Component>, Component>();
	
	private Scene scene = null;
	
	private HashSet<GameObject> children = new HashSet<GameObject>();
	private GameObject parent;
	
	private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
	private Quaternionf rotation = new Quaternionf();
	private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
	private Vector3f front = new Vector3f(1, 0, 0);
	
	//cache
	private Matrix4f modelMatrix;
	
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
	
	public Vector3f getWorldPosition() {
		Vector4f v = new Vector4f(0, 0, 0, 1);
		Matrix4f m = this.getModelMatrix();
		v.mul(m);
		return new Vector3f(v.x, v.y, v.z);
	}
	
	public Vector3f getWorldRotationDirectionVector() {
		Vector4f v = new Vector4f(this.front.x, this.front.y, this.front.z, 0);
		Matrix4f m = this.getModelMatrix();
		m.transform(v);
		return new Vector3f(v.x, v.y, v.z).normalize();
	}
	
	public Vector3f getPosition() {
		return new Vector3f(this.position);
	}
	
	public Quaternionf getRotation() {
		return new Quaternionf(this.rotation);
	}
	
	public Vector3f getScale() {
		return new Vector3f(this.scale);
	}
	
	public Vector3f getRotationEulerAngles() {
		return this.rotation.getEulerAnglesXYZ(new Vector3f());
	}
	
	public void setPosition(Vector3f position) {
		this.modelMatrix = null;
		this.position.x = position.x;
		this.position.y = position.y;
		this.position.z = position.z;
	}
	
	public void setPosition(float x, float y, float z) {
		this.setPosition(new Vector3f(x, y, z));;
	}
	
	public void setScale(Vector3f scale) {
		this.modelMatrix = null;
		this.scale.x = scale.x;
		this.scale.y = scale.y;
		this.scale.z = scale.z;
	}
	
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		this.setScale(new Vector3f(scaleX, scaleY, scaleZ));
	}
	
	public void setRotation(Quaternionf localRotation) {
		this.modelMatrix = null;
		this.rotation = localRotation;
	}
	
	public void setRotationX(float rotation) {
		this.rotateX(rotation - this.getRotationEulerAngles().x);
	}
	
	public void setRotationY(float rotation) {
		this.rotateY(rotation - this.getRotationEulerAngles().y);
	}
	
	public void setRotationZ(float rotation) {
		this.rotateZ(rotation - this.getRotationEulerAngles().z);
	}
	
	public void rotateX(float rotation) {
		this.modelMatrix = null;
		this.rotation.rotateLocalX(rotation);
	}
	
	public void rotateY(float rotation) {
		this.modelMatrix = null;
		this.rotation.rotateLocalY(rotation);
	}
	
	public void rotateZ(float rotation) {
		this.modelMatrix = null;
		this.rotation.rotateLocalZ(rotation);
	}
	
	public GameObject getParent() {
		return this.parent;
	}
	
	private void setParent(GameObject parent) {
		this.parent = parent;
	}
	
	public Vector3f getFront() {
		return front;
	}

	public void setFront(Vector3f front) {
		this.front = front;
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
	
	public boolean isModelMatrixCached() {
		return this.modelMatrix != null;
	}
	
	public Matrix4f getLocalModelMatrix() {
		Matrix4f modelMatrix;
		if (this.modelMatrix != null) {
			modelMatrix = this.modelMatrix;
		} else {
			modelMatrix = new Matrix4f();
			modelMatrix.translate(this.position)
			           .rotate(this.rotation)
			           .scale(this.scale);
			this.modelMatrix = modelMatrix;
		}
		return modelMatrix;
	}
	
	public Matrix4f getModelMatrix() {
		Matrix4f mLocal = this.getLocalModelMatrix();
		if (this.parent != null) {
			return this.parent.getModelMatrix().mul(mLocal);
		}
		return new Matrix4f(mLocal);
	}
}
