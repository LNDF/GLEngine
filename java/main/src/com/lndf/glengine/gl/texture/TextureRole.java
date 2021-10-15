package com.lndf.glengine.gl.texture;

public enum TextureRole {
	AMBIENT("ambient"),
	DIFFUSE("diffuse"),
	LIGHTMAP("lightmap"),
	NORMAL("normal"),
	SHININESS("shininess"),
	SPECULAR("specular");
	
	private String name;
	
	private TextureRole(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return this.name;
	}
	
}
