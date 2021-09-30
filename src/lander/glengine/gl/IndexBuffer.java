package lander.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import lander.glengine.engine.Task;
import lander.glengine.engine.Window;

public class IndexBuffer {
	
	private long count;
	private int id;
	private boolean isStatic;
	
	private boolean closed = false;
	
	//protected static int boundIndexBuffer = 0;
	
	public IndexBuffer(int[] buffer, boolean isStatic) {
		this.count = buffer.length;
		this.id = glGenBuffers();
		this.bind();
		this.isStatic = isStatic;
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, isStatic ? GL_STATIC_DRAW : GL_DYNAMIC_DRAW);
	}
	
	public void close() {
		if (this.closed) return;
		this.closed = true;
		Window.getWindow().addEndOfLoopTask(new Task() {
			@Override
			public void execute() {
				glDeleteBuffers(IndexBuffer.this.id);
			}
		});
	}
	
	public long getCount() {
		return this.count;
	}
	
	public int getId() {
		return this.id;
	}

	public boolean getIsStatic() {
		return this.isStatic;
	}
	
	public void draw() {
		this.bind();
		glDrawElements(GL_TRIANGLES, (int) this.count, GL_UNSIGNED_INT, 0);
	}
	
	public void bind() {
		//if (this.id == IndexBuffer.boundIndexBuffer) return;
		//IndexBuffer.boundIndexBuffer = this.id;
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
	}
	
	public static void unbind() {
		//if (IndexBuffer.boundIndexBuffer == 0) return;
		//IndexBuffer.boundIndexBuffer = 0;
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
}
