package com.lndf.glengine.engine;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	
	public static boolean getKey(int keyCode) {
		if (Engine.getWindowId() == -1) return false;
		return glfwGetKey(Engine.getWindowId(), keyCode) == 1 ? true : false;
	}
	
}
