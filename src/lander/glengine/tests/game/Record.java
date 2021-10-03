package lander.glengine.tests.game;

import org.joml.Vector3f;

import lander.glengine.asset.Asset;
import lander.glengine.engine.DeltaTime;
import lander.glengine.gl.texture.Texture2D;
import lander.glengine.scene.Component;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.components.Plane;

public class Record extends GameObject {
	
	private static int instances = 0;
	private static TextureMaterial material;
	private static Texture2D texture;
	
	public Record() {
		if (Record.instances <= 0) {
			Record.texture = new Texture2D(new Asset("resource:/testapp/goal.png"));
			Record.material = new TextureMaterial(Record.texture, 8, 1, 0, 0);
		}
		GameState.record = this;
		this.getTransform().setScale(new Vector3f(4, 1, 1));
		this.getTransform().rotateEuler(new Vector3f((float) (Math.PI / 2), 0, 0));
		Record.instances++;
		Plane plane = new Plane(Record.material);
		this.addComponent(plane);
		this.addComponent(new Component() {
			
			Vector3f pos = getTransform().getPosition();
			
			@Override
			public void start() {
				pos.set(new Vector3f(0, 0.06f, -45f));
			}

			@Override
			public void update() {
				Vector3f pos = getTransform().getPosition();
				pos.z += DeltaTime.get() * 20f;
				if (pos.z > 50f) {
					this.getGameObject().destroy();
				}
			}

			@Override
			public void destroy() {
				
			}
			
		});
	}
	
	@Override
	public void destroy() {
		super.destroy();
		GameState.record = null;
		Record.instances--;
		if (Record.instances <= 0) {
			Record.texture.close();
			Record.material.close();
		}
	}
	
}
