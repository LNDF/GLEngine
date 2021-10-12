package com.lndf.glengine.engine;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	
	protected static long currentWindowId;
	
	public static void setWindow(Window window) {
		Input.currentWindowId = window.getWindowId();
	}
	
	public static void unsetWindow() {
		Input.currentWindowId = -1;
	}
	
	public static boolean getKey(int keyCode, long windowId) {
		return glfwGetKey(windowId, keyCode) == 1 ? true : false;
	}
	
	public static boolean getKey(int keyCode) {
		if (Input.currentWindowId == -1) return false;
		return Input.getKey(keyCode, Input.currentWindowId);
	}
	
}
