package com.lndf.glengine.scene;

public class Component {
	
	private GameObject gameObject = null;
	
	public void update()  {}
	public void addToGameObject() {}
	public void removeFromGameObject()  {}
	public void addToScene()  {}
	public void removeFromScene()  {}
	
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
