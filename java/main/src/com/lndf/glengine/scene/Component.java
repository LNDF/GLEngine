package com.lndf.glengine.scene;

import com.lndf.glengine.physics.ContactInfo;

public class Component {
	
	private GameObject gameObject = null;
	
	public void update() {}
	public void addToGameObject() {}
	public void removeFromGameObject() {}
	public void addToScene() {}
	public void removeFromScene() {}
	
	public void collisionEnter(ContactInfo[] contactInfo, GameObject other) {}
	public void collisionPersist(ContactInfo[] contactInfo, GameObject other) {}
	public void collisionExit(ContactInfo[] contactInfo, GameObject other) {}
	
	public void triggerEnter(GameObject other) {}
	public void triggerPersist(GameObject other) {}
	public void triggerExit(GameObject other) {}
	
	public GameObject getGameObject() {
		return this.gameObject;
	}
	
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	public Scene getScene() {
		GameObject go = this.getGameObject();
		return go == null ? null : go.getScene();
	}
	
}
