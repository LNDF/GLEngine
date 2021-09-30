package lander.glengine.scene;

public abstract class Component {
	
	private GameObject gameObject = null;
	
	public abstract void start();
	public abstract void update();
	public abstract void destroy();
	
	public GameObject getGameObject() {
		return this.gameObject;
	}
	
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	public Scene getScene() {
		GameObject go = this.getGameObject();
		return go == null ? null : go.getScene();
	}
	
}
