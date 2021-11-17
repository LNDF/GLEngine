package com.lndf.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.VertexArrayLayout.VertexArrayLayoutElement;

public class VertexArray implements EngineResource {
	
	private int id;
	
	private boolean closed = false;
	
	protected static int boundVertexArray = 0;
	
	static {
		Engine.addTerminateRunnable(() -> boundVertexArray = 0);
	}
	
	public VertexArray() {
		Engine.addEngineResource(this);
		this.id = glGenVertexArrays();
	}
	
	public void destroy() {
		if (this.closed) return;
		this.closed = true;
		Engine.removeEngineResource(this);
		glDeleteVertexArrays(VertexArray.this.id);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	public int getId() {
		return this.id;
	}
	
	public void bind() {
		if (VertexArray.boundVertexArray == this.id) return;
		VertexArray.boundVertexArray = this.id;
		glBindVertexArray(this.id);
	}
	
	public static void unbind() {
		if (VertexArray.boundVertexArray == 0) return;
		VertexArray.boundVertexArray = 0;
		glBindVertexArray(0);
	}
	
	public void addVertexBuffers(VertexArrayLayout layout, VertexBuffer buffer) {
		this.bind();
		buffer.bind();
		ArrayList<VertexArrayLayoutElement> elements = layout.getElements();
		int stride = layout.getStride();
		long ptr = 0;
		for (int i = 0; i < elements.size(); i++) {
			VertexArrayLayoutElement element = elements.get(i);
			glEnableVertexAttribArray(i);
			glVertexAttribPointer(i, element.count, element.type, element.normalized, stride, ptr);
			ptr += element.size * element.count;
		}
	}
	
}
