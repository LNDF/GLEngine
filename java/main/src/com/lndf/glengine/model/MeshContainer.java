package com.lndf.glengine.model;

import com.lndf.glengine.gl.DefaultMaterial;
import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.gl.texture.Texture2DRoles;

public class MeshContainer {
	
	private String name;
	private Mesh mesh;
	private Texture2DRoles textures;
	
	public MeshContainer(String name, Mesh mesh, Texture2DRoles textures) {
		this.name = name;
		this.mesh = mesh;
		this.textures = textures;
	}

	public String getName() {
		return name;
	}
	
	public Mesh getMesh() {
		return mesh;
	}

	public Texture2DRoles getTextures() {
		return textures;
	}
	
	public Material createMaterial() {
		DefaultMaterial mat = new DefaultMaterial(this.textures);
		mat.setShininess(Float.MAX_VALUE);
		return mat;
	}
	
}
