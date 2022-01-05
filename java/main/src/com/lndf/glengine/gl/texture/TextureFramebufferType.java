package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL33.*;

public enum TextureFramebufferType {
	COLOR(GL_COLOR_ATTACHMENT0, GL_RGB, GL_UNSIGNED_BYTE),
	DEPTH(GL_DEPTH_ATTACHMENT, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT),
	STENCIL(GL_STENCIL_ATTACHMENT, GL_STENCIL_INDEX, GL_UNSIGNED_BYTE),
	DEPTH_STENCIL(GL_DEPTH_STENCIL_ATTACHMENT, GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8);
	
	private int glType;
	private int glFormat;
	private int glInternalFormat;
	private int glDataType;
	
	private TextureFramebufferType(int glType, int glFormat, int glDataType) {
		this(glType, glFormat, glFormat, glDataType);
	}
	
	private TextureFramebufferType(int glType, int glFormat, int glInternalFormat, int glDataType) {
		this.glType = glType;
		this.glFormat = glFormat;
		this.glInternalFormat = glInternalFormat;
		this.glDataType = glDataType;
	}
	
	public int getGlType() {
		return glType;
	}
	
	public int getGlFormat() {
		return glFormat;
	}
	
	public int getGlInternalFormat() {
		return glInternalFormat;
	}
	
	public int getGlDataType() {
		return glDataType;
	}
}
