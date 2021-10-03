package lander.glengine.scene.components;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import lander.glengine.engine.DeltaTime;
import lander.glengine.engine.Input;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.Transform;

public class FPCamera extends Camera{
	
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	
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
		Transform t = obj.getTransform();
		Vector3f pos = t.getPosition();
		Vector3f go = new Vector3f();
		float rotAngle = (float) (rotationSpeed * DeltaTime.get());
		float step = (float) (speed * DeltaTime.get());
		if (Input.getKey(this.fordwardKey)) {
			go.add(t.getFront().mul(step));
		}
		if (Input.getKey(this.backwardsKey)) {
			go.add(t.getBack().mul(step));
		}
		if (Input.getKey(this.leftKey)) {
			go.add(t.getLeft().mul(step));
		}
		if (Input.getKey(this.rightKey)) {
			go.add(t.getRight().mul(step));
		}
		if (Input.getKey(this.upKey)) {
			pos.add(0, step, 0);
		}
		if (Input.getKey(this.downKey)) {
			pos.add(0, -step, 0);
		}
		if (Input.getKey(this.camUpKey)) {
			
			t.rotateArround(t.getRight(), rotAngle);
		}
		if (Input.getKey(this.camDownKey)) {
			t.rotateArround(t.getRight(), -rotAngle);
		}
		if (Input.getKey(this.camLeftKey)) {
			t.rotateArround(UP, rotAngle);
		}
		if (Input.getKey(this.camRightKey)) {
			t.rotateArround(UP, -rotAngle);
		}
		float lenCorrect = go.length();
		float y = go.y;
		go.y = 0;
		float len = go.length();
		if (len != 0) {
			go.mul(lenCorrect / len);
		} else {
			t.getDown().mul(y, go);
		}
		pos.add(go);
	}
	
}
