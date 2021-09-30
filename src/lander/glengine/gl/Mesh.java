package lander.glengine.gl;

public class Mesh {
	
	private IndexBuffer indexBuffer = null;
	private VertexBuffer vertexBuffer = null;
	private VertexArray vertexArray = null;
	private VertexArrayLayout vertexArrayLayout = null;
	
	private boolean created = false;
	private boolean closed = false;
	
	public Mesh() {
		this.vertexArrayLayout = new VertexArrayLayout();
		this.createVertexArrayLayout(this.vertexArrayLayout);
	}
	
	public Mesh(float[] vertices, int[] indices) {
		this();
		this.create(vertices, indices);
	}
	
	public void create(float[] vertices, int[] indices) {
		if (this.created || this.closed) return;
		VertexArray vertexArray = new VertexArray();
		VertexBuffer vertexBuffer = new VertexBuffer(vertices, true);
		IndexBuffer indexBuffer = new IndexBuffer(indices, true);
		vertexArray.addVertexBuffers(this.vertexArrayLayout, vertexBuffer);
		this.vertexArray = vertexArray;
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
		this.created = true;
	}
	
	private void createVertexArrayLayout(VertexArrayLayout vertexArrayLayout) {
		vertexArrayLayout.pushFloat(3);
		vertexArrayLayout.pushFloat(2);
		vertexArrayLayout.pushFloat(3);
	}
	
	public int getVertexSize() {
		return this.vertexArrayLayout.getStride();
	}
	
	public int getVertexElementCount() {
		return this.vertexArrayLayout.getElementCount();
	}
	
	public IndexBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public VertexBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	public VertexArray getVertexArray() {
		return vertexArray;
	}
	
	public void draw() {
		this.vertexArray.bind();
		this.indexBuffer.draw();
	}
	
	public void close() {
		if (this.closed || !this.created) return;
		this.closed = true;
		this.vertexArray.close();
		this.vertexBuffer.close();
		this.indexBuffer.close();
	}
	
	@Override
	protected void finalize() {
		this.close();
	}
	
}
