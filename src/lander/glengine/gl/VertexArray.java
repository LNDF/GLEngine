package lander.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

import lander.glengine.engine.Task;
import lander.glengine.engine.Window;
import lander.glengine.gl.VertexArrayLayout.VertexArrayLayoutElement;

public class VertexArray {
	
	private int id;
	
	private boolean closed = false;
	
	protected static int boundVertexArray = 0;
	
	public VertexArray() {
		this.id = glGenVertexArrays();
	}
	
	public void close() {
		if (this.closed) return;
		this.closed = true;
		Window.getWindow().addEndOfLoopTask(new Task() {
			@Override
			public void execute() {
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
