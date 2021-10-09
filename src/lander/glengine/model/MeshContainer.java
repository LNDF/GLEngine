package lander.glengine.model;

import lander.glengine.gl.DefaultMaterial;
import lander.glengine.gl.Material;
import lander.glengine.gl.Mesh;
import lander.glengine.gl.texture.Texture2DRoles;

public class MeshContainer {
	
	private Mesh mesh;
	private Texture2DRoles textures;
	
	public MeshContainer(Mesh mesh, Texture2DRoles textures) {
		this.mesh = mesh;
		this.textures = textures;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public Texture2DRoles getTextures() {
		return textures;
	}
	
	public Material createMaterial() {
		return new DefaultMaterial(this.textures);
	}
	
}
