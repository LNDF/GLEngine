package com.lndf.glengine.gl;

public class Mesh {
	
	private IndexBuffer indexBuffer = null;
	private VertexBuffer vertexBuffer = null;
	private VertexArray vertexArray = null;
	private VertexArrayLayout vertexArrayLayout = null;
	
	private float[] positions;
	private float[] normals;
	private float[] texCoords;
	private int[] indices;
	
	public Mesh() {
		this.vertexArrayLayout = new VertexArrayLayout();
		this.createVertexArrayLayout(this.vertexArrayLayout);
	}
	
	public Mesh(float[] positions, float[] normals, float[] texCoords, int[] indices) {
		this();
		this.positions = positions;
		this.normals = normals;
		this.texCoords = texCoords;;
		this.indices = indices;
	}
	
	public static Mesh combine(Mesh[] meshes) {
		int pl = 0, nl = 0, tl = 0, il = 0;
		if (meshes == null) return null;
		for (Mesh mesh : meshes) {
			pl += mesh.getPositions().length;
			nl += mesh.getNormals().length;
			tl += mesh.getTexCoords().length;
			il += mesh.getIndices().length;
		}
		float[] positions = new float[pl];
		float[] normals = new float[nl];
		float[] texCoords = new float[tl];
		int[] indices = new int[il];
		int pOffset = 0, tOffset = 0, indicesOffset = 0, vertex = 0;
		for (Mesh mesh : meshes) {
			float[] srcpositions = mesh.getPositions();
			float[] srcnormals = mesh.getNormals();
			float[] srctexCoords = mesh.getTexCoords();
			int[] srcindices = mesh.getIndices();
			System.arraycopy(srcpositions, 0, positions, pOffset, srcpositions.length);
			System.arraycopy(srcnormals, 0, normals, pOffset, srcnormals.length);
			System.arraycopy(srctexCoords, 0, texCoords, tOffset, srctexCoords.length);
			for (int index : srcindices) {
				indices[indicesOffset++] = index + vertex;
			}
			pOffset += srcpositions.length;
			tOffset += srctexCoords.length;
			vertex += srcpositions.length / 3;
		}
		return new Mesh(positions, normals, texCoords, indices);
	}
	
	public void upload() {
		if (this.isUploaded()) return;
		int vsize = this.getVertexElementCount();
		int vlen = this.positions.length + this.normals.length + this.texCoords.length;
		float[] vertices = new float[vlen];
		if (this.positions != null) {
			for (int i = 0; i < this.positions.length / 3; i++) {
				System.arraycopy(this.positions, i * 3, vertices, i * vsize, 3);
			}
		}
		if (this.texCoords != null) {
			for (int i = 0; i < this.texCoords.length / 2; i++) {
				System.arraycopy(this.texCoords, i * 2, vertices, i * vsize + 3, 2);
			}
		}
		if (this.normals != null) {
			for (int i = 0; i < this.normals.length / 3; i++) {
				System.arraycopy(this.normals, i * 3, vertices, i * vsize + 5, 3);
			}
		}
		VertexArray vertexArray = new VertexArray();
		VertexBuffer vertexBuffer = new VertexBuffer(vertices, true);
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
	
	public float[] getPositions() {
		return positions;
	}
	
	public void setPositions(float[] positions) {
		if (!this.isUploaded()) this.positions = positions;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public void setNormals(float[] normals) {
		if (!this.isUploaded()) this.normals = normals;
	}
	
	public float[] getTexCoords() {
		return texCoords;
	}
	
	public void setTexCoords(float[] texCoords) {
		if (!this.isUploaded()) this.texCoords = texCoords;
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
