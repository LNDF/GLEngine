package com.lndf.glengine.tests.game;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.components.MeshRenderer;

public class Actor extends GameObject {
	
	private static int instances = 0;
	private static TextureMaterial material;
	private static Texture2D texture;
	
	public Actor(boolean isEnemy) {
		if (Actor.instances <= 0) {
			Actor.texture = new Texture2D(new Asset("resource:/testapp/placeholder.png"));
			Actor.material = new TextureMaterial(Actor.texture, 1, 1, 0, 0);
		}
		Actor.instances++;
		MeshRenderer cube = Cube.getMeshRenderer(Actor.material);
		this.addComponent(cube);
		if (!isEnemy) {
			Player p = new Player();
			GameState.player = p;
			this.addComponent(p);
		} else {
			this.addComponent(new Enemy());
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		Actor.instances--;
		if (Actor.instances <= 0) {
			Actor.texture.destroy();
			Actor.material.destroy();
		}
	}
	
}
