package com.lndf.glengine.tests.game;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.primitives.Cube;
import com.lndf.glengine.primitives.Plane;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.Camera;
import com.lndf.glengine.scene.components.FPCamera;

public class Level extends Scene {
	
	private TextureMaterial grass;
	private TextureMaterial road;
	private TextureMaterial roadEdge;
	private Texture2D grassTex;
	private Texture2D roadTex;
	private Texture2D roadEdgeTex;
	
	private FPCamera cam;
	
	public Level() {
		this.grassTex = new Texture2D(new Asset("resource:/testapp/grass.png"));
		this.grass = new TextureMaterial(this.grassTex, 50, 50, 0, 100f);
		this.roadTex = new Texture2D(new Asset("resource:/testapp/road.png"));
		this.road = new TextureMaterial(this.roadTex, 1, 25, 0, 25f);
		this.roadEdgeTex = new Texture2D(new Asset("resource:/testapp/road_edge.png"));
		this.roadEdge = new TextureMaterial(this.roadEdgeTex, 1, 250, 0, 1300f);
		GameObject ground = new GameObject();
		Plane grassObj = new Plane(this.grass);
		Plane roadObj = new Plane(this.road);
		Cube roadEdge1Obj = new Cube(this.roadEdge);
		Cube roadEdge2Obj = new Cube(this.roadEdge);
		GameObject camObject = new GameObject();
		Actor player = new Actor(false);
		this.addObject(player);
		this.addObject(ground);
		ground.getTransform().rotateEuler(new Vector3f((float) -(Math.PI / 2), 0, 0));
		//add grass
		ground.addChild(grassObj);
		grassObj.getTransform().setScale(new Vector3f(100, 100, 1));
		//add road
		ground.addChild(roadObj);
		roadObj.getTransform().setPosition(new Vector3f(0, 0, 0.05f));
		roadObj.getTransform().setScale(new Vector3f(4, 99, 1));
		//this.addObject(roadObj);
		//add road edges
		ground.addChild(roadEdge1Obj);
		roadEdge1Obj.getTransform().setPosition(new Vector3f(-2, 0, 0.01f));
		roadEdge1Obj.getTransform().setScale(new Vector3f(0.2f, 94, 0.2f));
		ground.addChild(roadEdge2Obj);
		roadEdge2Obj.getTransform().setPosition(new Vector3f(2, 0, 0.01f));
		roadEdge2Obj.getTransform().setScale(new Vector3f(0.2f, 94, 0.2f));
		//Cam setup
		this.cam = new FPCamera((float) (Math.PI / 4), 100.0f);
		this.addObject(camObject);
		camObject.addComponent(this.cam);
		Quaternionf camRotation = camObject.getTransform().getRotation().rotateAxis((float) (Math.PI) / -12, 1, 0, 0);
		camObject.getTransform().setRotation(camRotation);
		camObject.getTransform().setPosition(new Vector3f(0, 2, 3));
	}
	
	public Camera getCamera() {
		return this.cam;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.grass.destroy();
		this.grassTex.destroy();
		this.road.destroy();
		this.roadTex.destroy();
		this.roadEdge.destroy();
		this.roadEdgeTex.destroy();
	}
	
}
