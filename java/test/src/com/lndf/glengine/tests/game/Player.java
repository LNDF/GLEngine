package com.lndf.glengine.tests.game;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.engine.Input;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.Scene;

public class Player extends Component {
	
	public boolean derecha;
	public float spawnCooldown = 10;
	
	@Override
	public void addToGameObject() {
		this.getGameObject().getTransform().setPosition(new Vector3f(-0.7f, 0.6f, -2f));;
	}

	@Override
	public void update() {
		this.spawnCooldown += DeltaTime.get();
		if (this.spawnCooldown >= 1) {
			this.spawnCooldown = 0;
			Scene currentScene = this.getScene();
			Actor enemy = new Actor(true);
			currentScene.addObject(enemy);
		}
		if (Input.getKey(GLFW.GLFW_KEY_RIGHT)) {
			this.derecha = true;
		}
		if (Input.getKey(GLFW.GLFW_KEY_LEFT)) {
			this.derecha = false;
		}
		Vector3f pos = this.getGameObject().getTransform().getPosition();
		if (pos.x > -0.7f && !this.derecha) {
			pos.add(-30f * (float) DeltaTime.get(), 0, 0);
		} else if (pos.x < 0.7f && this.derecha) {
			pos.add(30f * (float) DeltaTime.get(), 0, 0);
		}
		if (pos.x > 0.7) pos.x = 0.7f;
		if (pos.x < -0.7) pos.x = -0.7f;
		this.getGameObject().getTransform().setPosition(pos);
	}

}
