package com.lndf.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.gl.texture.TextureFramebuffer2D;
import com.lndf.glengine.gl.texture.TextureFramebufferType;

public class Framebuffer implements EngineResource {
	
	private int id;
	private int width;
	private int height;
	
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	private boolean closed = false;
	
	private TextureFramebuffer2D[] colorTextures = new TextureFramebuffer2D[32];
	private TextureFramebuffer2D depthTexture;
	private TextureFramebuffer2D stencilTexture;
	private TextureFramebuffer2D depthStencilTexture;
	
	private static int boundFramebuffer = 0;
	
	private ArrayList<Integer> drawBuffers = new ArrayList<>();
	private boolean drawBuffersChanged = true;
	
	public Framebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.id = glGenFramebuffers();
		
	}
	
	@Override
	public void destroy() {
		if (this.closed) return;
		this.closed = true;
		Engine.removeEngineResource(this);
		for (int i = 0; i < this.colorTextures.length; i++) {
			this.detachTexture(TextureFramebufferType.COLOR, i);
		}
		this.detachTexture(TextureFramebufferType.DEPTH);
		this.detachTexture(TextureFramebufferType.DEPTH_STENCIL);
		this.detachTexture(TextureFramebufferType.STENCIL);
		glDeleteFramebuffers(this.id);
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (!this.closed) Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	public int getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		for (TextureFramebuffer2D color : this.colorTextures) {
			if (color != null) color.allocateTexture(width, height);
		}
		if (this.depthTexture != null) this.depthTexture.allocateTexture(width, height);
		if (this.stencilTexture != null) this.stencilTexture.allocateTexture(width, height);
		if (this.depthStencilTexture != null) this.depthStencilTexture.allocateTexture(width, height);
	}
	
	private void setGLDrawBuffers() {
		if (this.drawBuffers.size() < 0) {
			glDrawBuffers(GL_NONE);
		} else {
			int[] array = new int[this.drawBuffers.size()];
			for (int i = 0; i < this.drawBuffers.size(); i++) {
				array[i] = this.drawBuffers.get(i);
			}
			glDrawBuffers(array);
		}
	}
	
	public void setTextureToDrawBuffer(int bufferIndex, int textureIndex) {
		this.drawBuffersChanged = true;
		this.drawBuffers.add(bufferIndex, textureIndex);
	}
	
	public void unsetTextureFromDrawBuffer(int bufferIndex) {
		this.drawBuffersChanged = true;
		this.drawBuffers.remove(bufferIndex);
	}
	
	public void attachTexture(TextureFramebuffer2D texture) {
		this.attachTexture(texture, 0);
	}
	
	public void attachTexture(TextureFramebuffer2D texture, int index) {
		this.bind();
		TextureFramebufferType type = texture.getType();
		int texId = texture.getId();
		int target = type.getGlDataType();
		switch (type) {
			case COLOR:
				this.colorTextures[index] = texture;
				target += index;
				break;
			case DEPTH:
				this.depthTexture = texture;
				break;
			case DEPTH_STENCIL:
				this.depthStencilTexture = texture;
				break;
			case STENCIL:
				this.stencilTexture = texture;
				break;
		}
		texture.setBoundFramebuffer(this);
		texture.setBoundIndex(index);
		glFramebufferTexture2D(GL_FRAMEBUFFER, target, GL_TEXTURE_2D, texId, 0);
	}
	
	public void detachTexture(TextureFramebufferType type) {
		this.detachTexture(type, 0);
	}
	
	public void detachTexture(TextureFramebufferType type, int index) {
		TextureFramebuffer2D texture = null;
		int target = type.getGlType();
		switch (type) {
			case COLOR:
				texture = this.colorTextures[index];
				this.colorTextures[index] = null;
				target += index;
				break;
			case DEPTH:
				texture = this.depthTexture;
				this.depthTexture = null;
				break;
			case DEPTH_STENCIL:
				texture = this.depthStencilTexture;
				texture = this.depthStencilTexture = null;
				break;
			case STENCIL:
				texture = this.stencilTexture;
				this.stencilTexture = null;
				break;
		}
		if (texture != null) {
			texture.setBoundFramebuffer(null);
			glFramebufferTexture2D(GL_FRAMEBUFFER, target, GL_TEXTURE_2D, 0, 0);
		}
	}
	
	public void addDrawable(Drawable drawable) {
		this.drawables.add(drawable);
	}
	
	public void removeDrawable(Drawable drawable) {
		this.drawables.remove(drawable);
	}
	
	public void render() {
		this.bind();
		if (this.drawBuffersChanged) {
			this.drawBuffersChanged = false;
			this.setGLDrawBuffers();
		}
		Engine.setViewport(width, height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		for (Drawable drawable : this.drawables) {
			drawable.draw();
		}
	}
	
	public void bind() {
		if (this.id == Framebuffer.boundFramebuffer) return;
		Framebuffer.boundFramebuffer = this.id;
		glBindFramebuffer(GL_FRAMEBUFFER, this.id);
	}
	
	public static void unbind() {
		if (Framebuffer.boundFramebuffer == 0) return;
		Framebuffer.boundFramebuffer = 0;
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

}
