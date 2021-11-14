package com.lndf.glengine.tests.gameobject;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.lndf.glengine.scene.GameObject;

public class Main {
	
	public static void main(String[] args) {
		GameObject parent = new GameObject();
		GameObject child = new GameObject();
		parent.addChild(child);
		System.out.println("Position");
		parent.getTransform().setPosition(new Vector3f(1, 0, 0));
		parent.getTransform().setScale(new Vector3f(2, 1, 1));
		parent.getTransform().setRotation(new Quaternionf(0.1f, 0, 0, 1));
		child.getTransform().setPosition(new Vector3f(1, 0, 0));
		System.out.println(child.getTransform().getWorldPosition());
		child.getTransform().setWorldPosition(new Vector3f(0, 0, 0));
		System.out.println(child.getTransform().getPosition());
		System.out.println(child.getTransform().getWorldPosition());
		System.out.println("Scale");
		child.getTransform().setWorldScale(new Vector3f(1, 1, 1));
		System.out.println(child.getTransform().getScale());
		System.out.println(child.getTransform().getWorldScale());
		System.out.println("Rotation");
		System.out.println(child.getTransform().getRotation());
		System.out.println(child.getTransform().getWorldRotation());
		child.getTransform().setWorldRotation(new Quaternionf(0, 0, 0, 1));
		System.out.println(child.getTransform().getRotation());
		System.out.println(child.getTransform().getWorldRotation());
	}
	
}
