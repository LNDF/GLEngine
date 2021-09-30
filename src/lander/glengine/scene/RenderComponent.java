package lander.glengine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class RenderComponent extends Component {
	
	private boolean visible = true;
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public void setGLSettings() {
		//TODO: stub
	}
	
	public abstract void render(Matrix4f vp, Vector3f pov);
	
}
