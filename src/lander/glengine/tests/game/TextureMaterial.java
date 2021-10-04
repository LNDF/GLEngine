package lander.glengine.tests.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import lander.glengine.asset.Asset;
import lander.glengine.engine.DeltaTime;
import lander.glengine.gl.Material;
import lander.glengine.gl.Shader;
import lander.glengine.gl.texture.Texture2D;
import lander.glengine.scene.GameObject;

public class TextureMaterial extends Material {
	
	private static int instances = 0;
	private static Shader offShader = null;
	
	private Texture2D texture;
	private float speedX = 0.0f;
	private float speedY = 0.0f;
	private float offsetX = 0.0f;
	private float offsetY = 0.0f;
	private float repeatX = 0.0f;
	private float repeatY = 0.0f;
	
	public static Shader defaultShader() {
		if (TextureMaterial.instances > 0) return TextureMaterial.offShader;
		String[] vertex = new String[] {
				Shader.readShader(new Asset("resource:/testapp/off_tex.vshader"))
		};
		String[] fragment = new String[] {
				Shader.readShader(new Asset("resource:/testapp/off_tex.fshader"))
		};
		return new Shader(vertex, fragment, null);
	}
	
	public TextureMaterial(Texture2D tex, float repeatX, float repeatY, float speedX, float speedY) {
		super(defaultShader());
		TextureMaterial.offShader = this.getShader();
		TextureMaterial.instances++;
		this.texture = tex;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	@Override
	public void close() {
		super.close();
		TextureMaterial.instances--;
		if (TextureMaterial.instances <= 0) {
			if (TextureMaterial.offShader != null) TextureMaterial.offShader.close();
			TextureMaterial.offShader = null;
		}
	}

	@Override
	public void setUniform(Matrix4f vp, GameObject obj, Vector3f pov) {
		Matrix4f mvp = new Matrix4f(vp);
		mvp.mul(obj.getTransform().getWorldTransformation());
		this.texture.bind();
		this.offsetX = (this.offsetX + this.speedX * (float) DeltaTime.get()) % this.repeatX;
		this.offsetY = (this.offsetY + this.speedY * (float) DeltaTime.get()) % this.repeatY;
		this.getShader().setUniform("uTexCount", this.repeatX, this.repeatY);
		this.getShader().setUniform("uTexOff", this.offsetX / this.repeatX, this.offsetY / this.repeatY);
		this.getShader().setUniform("uMVP", mvp);
	}
	
	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public float getRepeatX() {
		return repeatX;
	}

	public void setRepeatX(float repeatX) {
		this.repeatX = repeatX;
	}

	public float getRepeatY() {
		return repeatY;
	}

	public void setRepeatY(float repeatY) {
		this.repeatY = repeatY;
	}

}
