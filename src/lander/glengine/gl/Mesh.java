package lander.glengine.gl;

public class Mesh {
	
	private IndexBuffer indexBuffer = null;
	private VertexBuffer vertexBuffer = null;
	private VertexArray vertexArray = null;
	private VertexArrayLayout vertexArrayLayout = null;
	
	private float[] vertices;
	private int[] indices;
	
	public Mesh() {
		this.vertexArrayLayout = new VertexArrayLayout();
		this.createVertexArrayLayout(this.vertexArrayLayout);
	}
	
	public Mesh(float[] vertices, int[] indices) {
		this();
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public static Mesh combine(Mesh[] meshes) {
		int vl = 0, il = 0;
		if (meshes == null) return null;
		for (Mesh mesh : meshes) {
			vl += mesh.getVertices().length;
			il += mesh.getIndices().length;
		}
		float[] vertices = new float[vl];
		int[] indices = new int[il];
		int offset = 0, indicesOffset = 0;
		for (Mesh mesh : meshes) {
			float[] srcvertices = mesh.getVertices();
			int[] srcindices = mesh.getIndices();
			System.arraycopy(srcvertices, 0, vertices, offset, srcvertices.length);
			for (int index : srcindices) {
				indices[indicesOffset++] = index + offset;
			}
			offset += srcvertices.length;
		}
		return new Mesh(vertices, indices);
	}
	
	public void upload() {
		if (this.isUploaded()) return;
		VertexArray vertexArray = new VertexArray();
		VertexBuffer vertexBuffer = new VertexBuffer(this.vertices, true);
		IndexBuffer indexBuffer = new IndexBuffer(this.indices, true);
		vertexArray.addVertexBuffers(this.vertexArrayLayout, vertexBuffer);
		this.vertexArray = vertexArray;
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
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
	
	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		if (!this.isUploaded()) this.vertices = vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		if (!this.isUploaded()) this.indices = indices;
	}
	
	public boolean isUploaded() {
		return this.vertexArray != null || this.vertexBuffer != null || this.indexBuffer != null;
	}

	public void draw() {
		if (!this.isUploaded()) this.upload();
		this.vertexArray.bind();
		this.indexBuffer.draw();
	}
	
	public void close() {
		if (!this.isUploaded()) return;
		this.vertexArray.close();
		this.vertexBuffer.close();
		this.indexBuffer.close();
		this.vertexArray = null;
		this.vertexBuffer = null;
		this.indexBuffer = null;
	}
	
	@Override
	protected void finalize() {
		this.close();
	}
	
}
