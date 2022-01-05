package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL13.*;

import java.awt.Color;
import java.util.HashMap;

import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.Utils;

public abstract class Texture2D implements EngineResource {
	
	protected int id;
	protected boolean closed = false;
	
	protected TextureFilter minFilter = TextureFilter.NEAREST;
	protected TextureFilter magFilter = TextureFilter.NEAREST;
	protected TextureWrap wrapHorizontal;
	protected TextureWrap wrapVertical;
	protected Color borderColor;
	
	protected static HashMap<Integer, Integer> boundTextures = new HashMap<Integer, Integer>();
	
	static {
		for (int i = 0; i < 32; i++) {
			TextureImage2D.boundTextures.put(i, 0);
		}
		Engine.addTerminateRunnable(() -> {
			for (int i = 0; i < 32; i++) {
				TextureImage2D.boundTextures.put(i, 0);
			}
		});
	}
	
	protected Texture2D() {
		Engine.addEngineResource(this);
		this.id = glGenTextures();
	}
	
	public int getId() {
		return id;
	}

	public TextureWrap getWrapHorizontal() {
		return wrapHorizontal;
	}
	
	public void setDefaultTextureSettings() {
		this.setMinFilters();
		this.setMagFilters();
		this.setWrapHorizontal(TextureWrap.REPEAT);
		this.setWrapVertical(TextureWrap.REPEAT);
		this.setBorderColor(Color.BLACK);
	}
	
	public void setWrapHorizontal(TextureWrap wrapHorizontal) {
		this.setWrapHorizontal(wrapHorizontal, false);
	}
	
	public void setWrapHorizontal(TextureWrap wrapHorizontal, boolean force) {
		if (this.wrapHorizontal == wrapHorizontal || force) {
			this.wrapHorizontal = wrapHorizontal;
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapHorizontal.getGlTextureWrap());
		}
	}

	public TextureWrap getWrapVertical() {
		return wrapVertical;
	}
	
	public void setWrapVertical(TextureWrap wrapVertical) {
		this.setWrapVertical(wrapVertical, false);
	}
	
	public void setWrapVertical(TextureWrap wrapVertical, boolean force) {
		if (this.wrapVertical == wrapVertical || force) {
			this.wrapVertical = wrapVertical;
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapHorizontal.getGlTextureWrap());
		}
	}

	public Color getBorderColor() {
		return borderColor;
	}
	
	public void setBorderColor(Color borderColor) {
		this.setBorderColor(borderColor, false);
	}
	
	public void setBorderColor(Color borderColor, boolean force) {
		if (!borderColor.equals(this.borderColor) || force) {
			this.borderColor = borderColor;
			float[] f = Utils.getColorAsRGBA8FloatArray(borderColor);
			glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, f);
		}
	}
	
	public TextureFilter getMinFilter() {
		return minFilter;
	}
	
	public void setMinFilter(TextureFilter minFilter) {
		this.setMinFilter(minFilter, false);
	}
	
	public void setMinFilter(TextureFilter minFilter, boolean force) {
		if (this.minFilter != minFilter || force) {
			this.minFilter = minFilter;
			this.setMinFilters();
		}
	}

	public TextureFilter getMagFilter() {
		return magFilter;
	}
	
	public void setMagFilter(TextureFilter magFilter) {
		this.setMagFilter(magFilter, false);
	}
	
	public void setMagFilter(TextureFilter magFilter, boolean force) {
		if (this.magFilter == magFilter || force) {
			this.magFilter = magFilter;
			this.setMagFilters();
		}
	}
	
	protected void setMinFilters() {
		this.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, TextureFilter.getGLFilters(this.minFilter, null));
	}
	
	protected void setMagFilters() {
		this.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, TextureFilter.getGLFilters(this.magFilter, null));
	}
	
	public void destroy() {
		if (this.closed) return;
		this.closed = true;
		Engine.removeEngineResource(this);
		glDeleteTextures(this.id);
	}
	
	@Override
	protected void finalize() {
		if (!this.closed) Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	public void bind(int slot) {
		if (TextureImage2D.boundTextures.get(slot) == this.id) return;
		TextureImage2D.boundTextures.put(slot, this.id);
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, this.id);
	}
	
	public void bind() {
		this.bind(0);
	}
	
	public static void unbind(int slot) {
		if (TextureImage2D.boundTextures.get(slot) == 0) return;
		TextureImage2D.boundTextures.put(slot, 0);
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void unbind() {
		TextureImage2D.unbind(0);
	}

}
