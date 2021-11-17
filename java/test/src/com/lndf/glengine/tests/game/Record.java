package com.lndf.glengine.tests.game;

import org.joml.Vector3f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.primitives.Plane;
import com.lndf.glengine.scene.Component;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.components.MeshRenderer;

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
		MeshRenderer plane = Plane.getMeshRenderer(Record.material);
		this.addComponent(plane);
		this.addComponent(new Component() {
			
			@Override
			public void addToGameObject() {
				getTransform().setPosition(new Vector3f(0, 0.06f, -45f));
			}

			@Override
			public void update() {
				Vector3f pos = getTransform().getPosition();
				pos.z += DeltaTime.get() * 20f;
				if (pos.z > 50f) {
					this.getGameObject().destroy();
				}
				getTransform().setPosition(pos);
			}
			
		});
	}
	
	@Override
	public void destroy() {
		super.destroy();
		GameState.record = null;
		Record.instances--;
		if (Record.instances <= 0) {
			Record.texture.destroy();
			Record.material.destroy();
		}
	}
	
}
