package com.lndf.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

import com.lndf.glengine.engine.Window;
import com.lndf.glengine.gl.VertexArrayLayout.VertexArrayLayoutElement;

public class VertexArray {
	
	private int id;
	
	private boolean closed = false;
	
	private Runnable closeRunnable = () -> this.close();
	
	protected static int boundVertexArray = 0;
	
	static {
		Window.addTerminateRunnable(() -> boundVertexArray = 0);
	}
	
	public VertexArray() {
		Window.addTerminateRunnable(closeRunnable);
		this.id = glGenVertexArrays();
	}
	
	public void close() {
		if (this.closed) return;
		this.closed = true;
		Window.removeTerminateRunnable(closeRunnable);
		Window.getWindow().addEndOfLoopRunnable(new Runnable() {
			@Override
			public void run() {
				glDeleteVertexArrays(VertexArray.this.id);
			}
		});
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
