package lander.glengine.tests.game;

import lander.glengine.asset.Asset;
import lander.glengine.engine.Window;
import lander.glengine.gl.texture.Texture2D;
import lander.glengine.scene.GameObject;
import lander.glengine.scene.Scene;
import lander.glengine.scene.components.Camera;
import lander.glengine.scene.components.Cube;
import lander.glengine.scene.components.FPCamera;
import lander.glengine.scene.components.Plane;

public class Level extends Scene {
	
	private TextureMaterial grass;
	private TextureMaterial road;
	private TextureMaterial roadEdge;
	private Texture2D grassTex;
	private Texture2D roadTex;
	private Texture2D roadEdgeTex;
	
	private FPCamera cam;
	
	public Level(Window window) {
		this.grassTex = new Texture2D(new Asset("resource:/testapp/grass.png"));
		this.grass = new TextureMaterial(this.grassTex, 50, 50, 0, 100f);
		this.roadTex = new Texture2D(new Asset("resource:/testapp/road.png"));
		this.road = new TextureMaterial(this.roadTex, 1, 25, 0, 25f);
		this.roadEdgeTex = new Texture2D(new Asset("resource:/testapp/road_edge.png"));
		this.roadEdge = new TextureMaterial(this.roadEdgeTex, 1, 250, 0, 1300f);
		GameObject ground = new GameObject();
		GameObject grassObj = new GameObject();
		GameObject roadObj = new GameObject();
		GameObject roadEdge1Obj = new GameObject();
		GameObject roadEdge2Obj = new GameObject();
		GameObject camObject = new GameObject();
		Actor player = new Actor(false);
		this.addObject(player);
		this.addObject(ground);
		ground.setRotationX((float) -(Math.PI / 2));
		//add grass
		ground.addChild(grassObj);
		grassObj.setScale(100, 100, 1);
		//this.addObject(grassObj);
		//add road
		ground.addChild(roadObj);
		roadObj.setPosition(0, 0, 0.05f);
		roadObj.setScale(4, 99, 1);
		//this.addObject(roadObj);
		//add road edges
		ground.addChild(roadEdge1Obj);
		roadEdge1Obj.setPosition(-2, 0, 0.01f);
		roadEdge1Obj.setScale(0.2f, 94, 0.2f);
		ground.addChild(roadEdge2Obj);
		roadEdge2Obj.setPosition(2, 0, 0.01f);
		roadEdge2Obj.setScale(0.2f, 94, 0.2f);
		//this.addObject(roadEdge1Obj);
		//this.addObject(roadEdge2Obj);
		//add grass plane
		Plane grassPlane = new Plane(this.grass);
		grassObj.addComponent(grassPlane);
		//add road plane
		Plane roadPlane = new Plane(this.road);
		roadObj.addComponent(roadPlane);
		//edd edge road cubes
		Cube edgeRoad1Cube = new Cube(this.roadEdge);
		Cube edgeRoad2Cube = new Cube(this.roadEdge);
		roadEdge1Obj.addComponent(edgeRoad1Cube);
		roadEdge2Obj.addComponent(edgeRoad2Cube);
		//Cam setup
		this.cam = new FPCamera((float) (Math.PI / 4), 100.0f);
		this.cam.setPitch((float) -(Math.PI / 12));
		this.addObject(camObject);
		camObject.addComponent(this.cam);
		camObject.setPosition(0, 2, 3);
	}
	
	public Camera getCamera() {
		return this.cam;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.grass.close();
		this.grassTex.close();
		this.road.close();
		this.roadTex.close();
		this.roadEdge.close();
		this.roadEdgeTex.close();
	}
	
}
