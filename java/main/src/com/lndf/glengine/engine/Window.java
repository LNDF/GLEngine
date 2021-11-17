package com.lndf.glengine.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import com.lndf.glengine.gl.Drawable;

public class Window {
	private String name;
	private int width;
	private int height;
	private boolean resizable;
	
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	private RunnableList runnables = new RunnableList();
	private static RunnableList terminateRunnables = new RunnableList();
	
	private static boolean destroyingResources = false;
	private static LinkedList<EngineResource> engineResources = new LinkedList<EngineResource>();
	private static LinkedList<EngineResource> removedEngineResources = new LinkedList<EngineResource>();
	
	private long windowId;
	
	private static Window window;
	
	private Window(String title, int width, int height, boolean resizable) {
		this.name = title;
		this.width = width;
		this.height = height;
		this.resizable = resizable;
		//GLFW init
		GLFWErrorCallback.createPrint(System.err);
		if (!glfwInit()) {
			throw new IllegalStateException("Couldn't init GLFW.");
		}
		this.init();
		Input.setWindow(this);
		PhysXManager.start();
	}
	
	public static void terminate() {
		Window.terminateRunnables.executeAll(false);
		Window.destroyingResources = true;
		for (EngineResource resource : Window.engineResources) {
			resource.destroy();
		}
		Window.destroyingResources = false;
		Window.engineResources.removeAll(Window.removedEngineResources);
		Window.removedEngineResources.clear();
		glfwTerminate();
		Window.window = null;
		Input.unsetWindow();
		PhysXManager.stop();
	}
	
	private void init() {
		//Window init
		glfwWindowHint(GLFW_RESIZABLE, this.resizable ? GLFW_TRUE : GLFW_FALSE);
		this.windowId = glfwCreateWindow(this.width, this.height, this.name, 0, 0);
		if (this.windowId == 0) {
			throw new RuntimeException("Couldn't create window");
		}
		glfwMakeContextCurrent(this.windowId);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glfwSetWindowSizeCallback(this.windowId, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int newWidth, int newHeight) {
				width = newWidth;
				height = newHeight;
				glViewport(0, 0, width, height);
			}
		});
	}
	
	public void mainLoop() {
		//main loop
		DeltaTime.set();
		while (!glfwWindowShouldClose(this.windowId)) {
			DeltaTime.set();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			for (Drawable drawable : this.drawables) {
				drawable.draw(this);
			}
			SceneManager.updateScenes();
			this.runnables.executeAll(true);
			glfwSwapBuffers(this.windowId);
			glfwPollEvents();
		}
	}
	
	public static Window createWindow(String title, int width, int height, boolean resizable) {
		if (Window.window != null) return null;
		Window.window = new Window(title, width, height, resizable);
		return Window.window;
	}
	
	public static Window createWindow(String title, int width, int height) {
		return Window.createWindow(title, width, height, false);
	}
	
	public static Window getWindow() {
		return Window.window;
	}
	
	public void setClose(boolean shouldClose) {
		glfwSetWindowShouldClose(this.windowId, shouldClose);
	}
	
	public long getWindowId() {
		return this.windowId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean closed() {
		return glfwWindowShouldClose(this.windowId);
	}
	
	public void addDrawable(Drawable drawable) {
		this.drawables.add(drawable);
	}
	
	public void removeDrawable(Drawable drawable) {
		this.drawables.remove(drawable);
	}
	
	public void addEndOfLoopRunnable(Runnable runnable) {
		this.runnables.addRunnable(runnable);
	}
	
	public static void addTerminateRunnable(Runnable runnable) {
		Window.terminateRunnables.addRunnable(runnable);
	}
	
	public static void removeTerminateRunnable(Runnable runnable) {
		Window.terminateRunnables.removeRunnable(runnable);
	}
	
	public static void addEngineResource(EngineResource resource) {
		if (Window.destroyingResources) {
			Window.removedEngineResources.remove(resource);
		}
		Window.engineResources.add(resource);
	}
	
	public static void removeEngineResource(EngineResource resource) {
		if (Window.destroyingResources) {
			Window.removedEngineResources.add(resource);
		} else {
			Window.engineResources.remove(resource);
		}
	}
}
