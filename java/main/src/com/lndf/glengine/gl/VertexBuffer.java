package com.lndf.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import com.lndf.glengine.engine.Task;
import com.lndf.glengine.engine.Window;

public class VertexBuffer {
	
	private int id;
	private boolean isStatic;
	
	private boolean closed = false;
	
	protected static int boundVertexBuffer = 0;
	
	protected VertexBuffer(boolean isStatic) {
		this.id = glGenBuffers();
		this.isStatic = isStatic;
		this.bind();
	}
	
	public VertexBuffer(int[] buffer, boolean isStatic) {
		this(isStatic);
		glBufferData(GL_ARRAY_BUFFER, buffer, isStatic ? GL_STATIC_DRAW : GL_DYNAMIC_DRAW);
	}
	
	public VertexBuffer(float[] buffer, boolean isStatic) {
		this(isStatic);
		glBufferData(GL_ARRAY_BUFFER, buffer, isStatic ? GL_STATIC_DRAW : GL_DYNAMIC_DRAW);
	}
	
	public void close() {
		if (this.closed) return;
		this.closed = true;
		Window.getWindow().addEndOfLoopTask(new Task() {
			@Override
			public void execute() {
				glDeleteBuffers(VertexBuffer.this.id);
			}
		});
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean getIsStatic() {
		return this.isStatic;
	}
	
	public void bind() {
		if (VertexBuffer.boundVertexBuffer == this.id) return;
		VertexBuffer.boundVertexBuffer = this.id;
		glBindBuffer(GL_ARRAY_BUFFER, this.id);
	}
	
	public static void unbind() {
		if (VertexBuffer.boundVertexBuffer == 0) return;
		VertexBuffer.boundVertexBuffer = 0;
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
}
