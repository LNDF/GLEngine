package com.lndf.glengine.tests.game;

import java.util.Random;

import org.joml.Vector3f;

import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;

public class Enemy extends Component {
	
	private static Random rand = new Random();
	
	private boolean derecha = Enemy.rand.nextBoolean();
	private boolean puntosAdded = false;
	private boolean spawned = false;
	
	@Override
	public void addToGameObject() {
		GameObject go = this.getGameObject();
		go.getTransform().setPosition(new Vector3f(this.derecha ? 0.7f : -0.7f, -1.2f, -70f));
		GameState.enemigos.add(go);
	}

	@Override
	public void update() {
		GameObject go = this.getGameObject();
		Vector3f pos = go.getTransform().getPosition();
		if (pos.z > -50f && !this.spawned) {
			this.spawned = true;
			GameState.spawned++;
			if (GameState.spawned - 1 == GameState.maxPuntos) {
				this.getScene().addObject(new Record());
			}
		}
		pos.z += DeltaTime.get() * 20f * GameState.speed;
		if (pos.y < 0.6f) {
			pos.y += DeltaTime.get() * 1.6f;
		}
		if (pos.z > 10f) {
			go.destroy();
			return;
		}
		go.getTransform().setPosition(pos);
		Player player = GameState.player;
		if (pos.z >= -2.5f && pos.z <= -1.5f) {
			if (this.derecha == player.derecha) {
				GameState.reset();
			} else if (!this.puntosAdded) {
				this.puntosAdded = true;
				GameState.sumarPuntos();
			}
			
		}
	}

	@Override
	public void removeFromGameObject() {
		if (!GameState.noBorrar) {
			GameState.enemigos.remove(this.getGameObject());
		}
	}

}
