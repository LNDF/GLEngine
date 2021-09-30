package lander.glengine.model;

import lander.glengine.scene.GameObject;

public class ModelGameObject extends GameObject {
	
	private String name;
	
	public ModelGameObject(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
