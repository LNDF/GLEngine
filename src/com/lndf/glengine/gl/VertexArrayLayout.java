package com.lndf.glengine.gl;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class VertexArrayLayout {
	public class VertexArrayLayoutElement {
		 public int type;
		 public int count;
		 public boolean normalized;
		 public int size;
		 
		 public VertexArrayLayoutElement(int type, int count, boolean normalized, int size) {
			 this.type = type;
			 this.count = count;
			 this.normalized = normalized;
			 this.size = size;
		 }
	}
	
	private int elementCount = 0;
	private int stride = 0;
	private ArrayList<VertexArrayLayoutElement> elements = new ArrayList<VertexArrayLayoutElement>();
	
	public void pushFloat(int count) {
		this.elementCount += count;
		int size = Float.SIZE / 8;
		VertexArrayLayoutElement e = new VertexArrayLayoutElement(GL_FLOAT, count, false, size);
		this.elements.add(e);
		this.stride += size * count;
	}
	
	public void pushInt(int count) {
		this.elementCount += count;
		int size = Integer.SIZE / 8;
		VertexArrayLayoutElement e = new VertexArrayLayoutElement(GL_INT, count, false, size);
		this.elements.add(e);
		this.stride += size * count;
	}
	
	public int getStride() {
		return this.stride;
	}
	
	public int getElementCount() {
		return this.elementCount;
	}
	
	public ArrayList<VertexArrayLayoutElement> getElements() {
		return this.elements;
	}

}