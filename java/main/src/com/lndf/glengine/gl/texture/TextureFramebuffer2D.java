package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL11.*;

import com.lndf.glengine.gl.Framebuffer;

public class TextureFramebuffer2D extends Texture2D {
	
	private TextureFramebufferType type;
	private Framebuffer boundFramebuffer;
	private int boundIndex;
	
	public TextureFramebuffer2D(TextureFramebufferType type) {
		super();
		this.type = type;
		this.setDefaultTextureSettings();
	}
	
	@Override
	public void destroy() {
		if (this.boundFramebuffer != null) {
			boundFramebuffer.detachTexture(type, boundIndex);
		}
		super.destroy();
	}
	
	public void allocateTexture(int width, int height) {
		this.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, this.type.getGlInternalFormat(), width, height, 0, this.type.getGlFormat(), this.type.getGlDataType(), 0);
		this.setMinFilter(minFilter, true);
		this.setMagFilter(magFilter, true);
	}
	
	public TextureFramebufferType getType() {
		return type;
	}

	public Framebuffer getBoundFramebuffer() {
		return boundFramebuffer;
	}

	public void setBoundFramebuffer(Framebuffer boundFramebuffer) {
		this.boundFramebuffer = boundFramebuffer;
	}

	public int getBoundIndex() {
		return boundIndex;
	}

	public void setBoundIndex(int boundIndex) {
		this.boundIndex = boundIndex;
	}
	
}
