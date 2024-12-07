package com.lndf.glengine.scene;

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
		throw new UnsupportedOperationException("Function not implemented.");
	}
	
	public abstract void render(Matrix4f vp, Vector3f pov);
	
}
