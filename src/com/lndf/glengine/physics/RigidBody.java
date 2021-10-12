package com.lndf.glengine.physics;

import org.joml.Vector3f;

import com.lndf.glengine.engine.DeltaTime;
import com.lndf.glengine.scene.Component;

public class RigidBody extends Component {
	
	private float mass;
	private Vector3f velocity = new Vector3f();
	private Vector3f gravity;
	private Vector3f force = new Vector3f();
	
	public RigidBody(float mass) {
		this(mass, new Vector3f(0, -9.8f, 0));
	}
	
	public RigidBody(float mass, Vector3f gravity) {
		this.mass = mass;
		this.gravity = gravity;
	}
	
	@Override
	public void start() {
		this.velocity.set(0);
		this.force.set(0);
	}

	@Override
	public void update() {
		Vector3f position = this.getGameObject().getTransform().getPosition();
		float dt = (float) DeltaTime.get();
		float gx, gy, gz;
		gx = this.gravity.x * mass;
		gy = this.gravity.y * mass;
		gz = this.gravity.z * mass;
		this.force.add(gx, gy, gz);
		this.force.mul(dt / mass);
		this.velocity.add(force);
		this.force.set(0);
		float x, y, z;
		x = this.velocity.x * dt;
		y = this.velocity.y * dt;
		z = this.velocity.z * dt;
		position.add(x, y, z);
	}
	
	public void addForce(Vector3f force) {
		this.force.add(force);
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public Vector3f getGravity() {
		return gravity;
	}

	public void setGravity(Vector3f gravity) {
		this.gravity = gravity;
	}

	public Vector3f getForce() {
		return force;
	}

	public void setForce(Vector3f force) {
		this.force = force;
	}

}
