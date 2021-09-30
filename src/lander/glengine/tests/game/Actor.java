package lander.glengine.tests.game;

import lander.glengine.asset.Asset;
import lander.glengine.gl.texture.Texture2D;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.components.Cube;

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
		Cube cube = new Cube(Actor.material);
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
			Actor.texture.close();
			Actor.material.close();
		}
	}
	
}
