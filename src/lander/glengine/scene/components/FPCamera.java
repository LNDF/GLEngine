package lander.glengine.scene.components;

import java.awt.event.KeyEvent;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import lander.glengine.engine.DeltaTime;
import lander.glengine.engine.Input;
import lander.glengine.scene.GameObject;

public class FPCamera extends Camera{
	
	private float pitch = 0.0f;
	private float yaw = 0.0f;
	
	private float speed = 1f;
	private float rotationSpeed = 1f;
	
	private int fordwardKey = KeyEvent.VK_W;
	private int backwardsKey = KeyEvent.VK_S;
	private int leftKey = KeyEvent.VK_A;
	private int rightKey = KeyEvent.VK_D;
	private int upKey = KeyEvent.VK_SPACE;
	private int downKey = GLFW.GLFW_KEY_LEFT_SHIFT;
	private int camUpKey = KeyEvent.VK_I;
	private int camDownKey = KeyEvent.VK_K;
	private int camLeftKey = KeyEvent.VK_J;
	private int camRightKey = KeyEvent.VK_L;
	
	public FPCamera(float FOV, float drawDistance) {
		super(FOV, drawDistance);
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public int getFordwardKey() {
		return fordwardKey;
	}

	public void setFordwardKey(int fordwardKey) {
		this.fordwardKey = fordwardKey;
	}

	public int getBackwardsKey() {
		return backwardsKey;
	}

	public void setBackwardsKey(int backwardsKey) {
		this.backwardsKey = backwardsKey;
	}

	public int getLeftKey() {
		return leftKey;
	}

	public void setLeftKey(int leftKey) {
		this.leftKey = leftKey;
	}

	public int getRightKey() {
		return rightKey;
	}

	public void setRightKey(int rightKey) {
		this.rightKey = rightKey;
	}

	public int getUpKey() {
		return upKey;
	}

	public void setUpKey(int upKey) {
		this.upKey = upKey;
	}

	public int getDownKey() {
		return downKey;
	}

	public void setDownKey(int downKey) {
		this.downKey = downKey;
	}

	public int getCamUpKey() {
		return camUpKey;
	}

	public void setCamUpKey(int camUpKey) {
		this.camUpKey = camUpKey;
	}

	public int getCamDownKey() {
		return camDownKey;
	}

	public void setCamDownKey(int camDownKey) {
		this.camDownKey = camDownKey;
	}

	public int getCamLeftKey() {
		return camLeftKey;
	}

	public void setCamLeftKey(int camLeftKey) {
		this.camLeftKey = camLeftKey;
	}

	public int getCamRightKey() {
		return camRightKey;
	}

	public void setCamRightKey(int camRightKey) {
		this.camRightKey = camRightKey;
	}
	
	
	@Override
	public void update() {
		GameObject obj = this.getGameObject();
		Vector3f pos = obj.getPosition();
		float rsin = (float) Math.sin(this.yaw);
		float rcos = (float) Math.cos(this.yaw);
		Vector3f go = new Vector3f(0, 0, 0);
		float goPitch = 0;
		float goYaw = 0;
		float a360 = (float) (2 * Math.PI);
		float a90 = (float) (Math.PI / 2);
		if (Input.getKey(this.fordwardKey)) {
			go.add(-1f * rsin, 0, -1f * rcos);
		}
		if (Input.getKey(this.backwardsKey)) {
			go.add(1f * rsin, 0, 1f * rcos);
		}
		if (Input.getKey(this.leftKey)) {
			go.add(-1f * rcos, 0, 1f * rsin);
		}
		if (Input.getKey(this.rightKey)) {
			go.add(1f * rcos, 0, -1f * rsin);
		}
		if (Input.getKey(this.upKey)) {
			go.add(0, 1f, 0);
		}
		if (Input.getKey(this.downKey)) {
			go.add(0, -1f, 0);
		}
		if (Input.getKey(this.camUpKey)) {
			goPitch += 1f;
		}
		if (Input.getKey(this.camDownKey)) {
			goPitch -= 1f;
		}
		if (Input.getKey(this.camLeftKey)) {
			goYaw += 1f;
		}
		if (Input.getKey(this.camRightKey)) {
			goYaw -= 1f;
		}
		go.mul((float) DeltaTime.get()).mul(this.speed);
		goYaw *= DeltaTime.get() * this.rotationSpeed;
		goPitch *= DeltaTime.get() * this.rotationSpeed;
		pos.add(go);
		this.yaw = (this.yaw +  goYaw) % a360;
		if (this.yaw < 0) {
			this.yaw = (float) (2 * Math.PI) - this.yaw;
		}
		this.pitch += goPitch;
		this.pitch = this.pitch > a90 ? a90 : this.pitch < -a90 ? -a90 : this.pitch;
		obj.setPosition(pos);
		Quaternionf newRotation = new Quaternionf();
		newRotation.rotateLocalX(this.pitch).rotateLocalY(this.yaw);
		obj.setRotation(newRotation);
	}
	
}
