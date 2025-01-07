package com.lndf.glengine.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import com.lndf.glengine.gl.Drawable;
import com.lndf.glengine.gl.Framebuffer;

public class Engine {
	private static String name;
	private static int width;
	private static int height;
	private static boolean resizable;
	private static boolean vsync = true;
	private static double frameTimeLimit = 0.0;
	
	private static int lastViewportWidth = -1;
	private static int lastViewportHeight = -1;
	
	private static ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	private static RunnableList runnables = new RunnableList();
	private static RunnableList terminateRunnables = new RunnableList();
	
	private static boolean destroyingResources = false;
	private static LinkedList<EngineResource> engineResources = new LinkedList<EngineResource>();
	private static LinkedList<EngineResource> removedEngineResources = new LinkedList<EngineResource>();
	
	private static long windowId = -1;
	
	public static void terminate() {
		Engine.runnables.executeAll(true);
		Engine.terminateRunnables.executeAll(false);
		Engine.destroyingResources = true;
		for (EngineResource resource : Engine.engineResources) {
			resource.destroy();
		}
		Engine.destroyingResources = false;
		Engine.engineResources.removeAll(Engine.removedEngineResources);
		Engine.removedEngineResources.clear();
		glfwTerminate();
		Engine.windowId = -1;
		PhysXManager.stop();
	}
	
	private static void init() {
		//Window init
		glfwWindowHint(GLFW_RESIZABLE, Engine.resizable ? GLFW_TRUE : GLFW_FALSE);
		Engine.windowId = glfwCreateWindow(Engine.width, Engine.height, Engine.name, 0, 0);
		if (Engine.windowId == 0) {
			throw new RuntimeException("Couldn't create window");
		}
		glfwMakeContextCurrent(Engine.windowId);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glfwSetWindowSizeCallback(Engine.windowId, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int newWidth, int newHeight) {
				width = newWidth;
				height = newHeight;
			}
		});
	}
	
	public static void mainLoop() {
		//main loop
		DeltaTime.set();
		Engine.runnables.clear();
		while (!glfwWindowShouldClose(Engine.windowId)) {
			DeltaTime.set();
			SceneManager.updateScenes();
			Engine.runnables.executeAll(true);
			Framebuffer.unbind();
			Engine.setViewport(width, height);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
			for (Drawable drawable : Engine.drawables) {
				drawable.draw();
			}
			glfwSwapBuffers(Engine.windowId);
			glfwPollEvents();
			if (Engine.frameTimeLimit > 0.0) {
				double frameTime = DeltaTime.get();
				if (frameTime < Engine.frameTimeLimit) {
					try {
						Thread.sleep((long) ((Engine.frameTimeLimit - frameTime) * 1000.0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void createWindow(String title, int width, int height, boolean resizable) {
		if (Engine.windowId != -1) return;
		Engine.name = title;
		Engine.width = width;
		Engine.height = height;
		Engine.resizable = resizable;
		//GLFW init
		GLFWErrorCallback.createPrint(System.err);
		if (!glfwInit()) {
			throw new IllegalStateException("Couldn't init GLFW.");
		}
		Engine.init();
		PhysXManager.start();
	}
	
	public static void setViewport(int width, int height) {
		if (width == lastViewportWidth && height == lastViewportHeight) return;
		lastViewportWidth = width;
		lastViewportHeight = height;
		glViewport(0, 0, width, height);
	}

	public static int getFPSLimit() {
		return (int) (1.0 / Engine.frameTimeLimit);
	}

	public static void setFPSLimit(int fpsLimit) {
		Engine.frameTimeLimit = fpsLimit == 0 ? 0.0 : 1.0 / fpsLimit;
	}

	public static boolean isVsyncEnabled() {
		return Engine.vsync;
	}

	public static void setVsync(boolean vsync) {
		Engine.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);
	}
	
	public static void createWindow(String title, int width, int height) {
		Engine.createWindow(title, width, height, false);
	}
	
	public static void setClose(boolean shouldClose) {
		glfwSetWindowShouldClose(Engine.windowId, shouldClose);
	}
	
	public static long getWindowId() {
		return Engine.windowId;
	}
	
	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		Engine.name = name;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		Engine.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		Engine.height = height;
	}
	
	public static boolean closed() {
		if (Engine.windowId == -1) return true;
		return glfwWindowShouldClose(Engine.windowId);
	}
	
	public static void addDrawable(Drawable drawable) {
		Engine.drawables.add(drawable);
	}
	
	public static void removeDrawable(Drawable drawable) {
		Engine.drawables.remove(drawable);
	}
	
	public static void addEndOfLoopRunnable(Runnable runnable) {
		Engine.runnables.addRunnable(runnable);
	}
	
	public static void addTerminateRunnable(Runnable runnable) {
		Engine.terminateRunnables.addRunnable(runnable);
	}
	
	public static void removeTerminateRunnable(Runnable runnable) {
		Engine.terminateRunnables.removeRunnable(runnable);
	}
	
	public static void addEngineResource(EngineResource resource) {
		if (Engine.destroyingResources) {
			Engine.removedEngineResources.remove(resource);
		}
		Engine.engineResources.add(resource);
	}
	
	public static void removeEngineResource(EngineResource resource) {
		if (Engine.destroyingResources) {
			Engine.removedEngineResources.add(resource);
		} else {
			Engine.engineResources.remove(resource);
		}
	}
}
