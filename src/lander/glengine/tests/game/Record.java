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
		this.setScale(4, 1, 1);
		this.setRotationX((float) (Math.PI / 2));
		Record.instances++;
		Plane plane = new Plane(Record.material);
		this.addComponent(plane);
		this.addComponent(new Component() {

			@Override
			public void start() {
				setPosition(0, 0.06f, -45f);
			}

			@Override
			public void update() {
				Vector3f pos = getPosition();
				pos.z += DeltaTime.get() * 20f;
				setPosition(pos);
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
