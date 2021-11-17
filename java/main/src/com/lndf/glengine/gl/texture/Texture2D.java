package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.EngineResource;
import com.lndf.glengine.engine.Utils;
import com.lndf.glengine.engine.Engine;

public class Texture2D implements EngineResource {
	
	private int id;
	private HashMap<Integer, Integer> width = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> height = new HashMap<Integer, Integer>();
	
	private TextureFilter minFilter = TextureFilter.NEAREST;
	private TextureFilter magFilter = TextureFilter.NEAREST;
	private TextureFilter minMipmapFilter = TextureFilter.LINEAR;
	
	private TextureWrap wrapHorizontal;
	private TextureWrap wrapVertical;
	private Color borderColor;
	
	private boolean closed = false;
	
	protected static boolean STBImageVerticalFlipMode = false;
	protected static HashMap<Integer, Integer> boundTextures = new HashMap<Integer, Integer>();
	
	static {
		for (int i = 0; i < 32; i++) {
			Texture2D.boundTextures.put(i, 0);
		}
		Engine.addTerminateRunnable(() -> {
			for (int i = 0; i < 32; i++) {
				Texture2D.boundTextures.put(i, 0);
			}
		});
	}
	
	public Texture2D() {
		Engine.addEngineResource(this);
		this.id = glGenTextures();
	}
	
	public Texture2D(Asset asset) {
		this();
		this.setTexture(asset, 0);
		this.setDefaultTextureSettings();
	}
	
	public Texture2D(ByteBuffer input) {
		this();
		this.setTexture(input, 0);
		this.setDefaultTextureSettings();
	}
	
	public void setDefaultTextureSettings() {
		this.autoGenerateMipmaps();
		this.setMinFilters();
		this.setMagFilters();
		this.setWrapHorizontal(TextureWrap.REPEAT);
		this.setWrapVertical(TextureWrap.REPEAT);
		this.setBorderColor(Color.BLACK);
	}
	
	public void setTexture(Asset asset, int mipmapLevel) {
		try {
			this.setTexture(asset.getByteBuffer(), mipmapLevel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setUncompressedTexture(ByteBuffer input, int mipmapLevel, int width, int height, int format, int pixelSize) {
		this.width.put(mipmapLevel, width);
		this.height.put(mipmapLevel, height);
		this.bind();
		glTexImage2D(GL_TEXTURE_2D, mipmapLevel, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, input);
	}
	
	public void setTexture(ByteBuffer input, int mipmapLevel) {
		try (MemoryStack stack = MemoryStack.stackPush();) {
			Texture2D.setSTBImageVerticalFlipMode(true);
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1); //Not used
			ByteBuffer img = stbi_load_from_memory(input, w, h, c, 4);
			if (img == null) {
				throw new RuntimeException("Couldn't load texture. Reason: " + stbi_failure_reason());
			}
			int width = w.get(0);
			int height = h.get(0);
			this.setUncompressedTexture(img, mipmapLevel, width, height, GL_RGBA, GL_UNSIGNED_BYTE);
			stbi_image_free(img);
		}
	}
	
	public void autoGenerateMipmaps() {
		this.bind();
		glGenerateMipmap(GL_TEXTURE_2D);
	}
	
	public int getWidth(int mipmapLevel) {
		Integer i = this.width.get(mipmapLevel);
		return i == null ? -1 : i;
	}
	
	public int getWidth() {
		return this.getWidth(0);
	}
	
	public int getHeight(int mipmapLevel) {
		Integer i = this.height.get(mipmapLevel);
		return i == null ? -1 : i;
	}
	
	public int getHeight() {
		return this.getHeight(0);
	}

	public TextureFilter getMinFilter() {
		return minFilter;
	}

	public void setMinFilter(TextureFilter minFilter) {
		if (this.minFilter != minFilter) {
			this.minFilter = minFilter;
			this.setMinFilters();
		}
	}

	public TextureFilter getMagFilter() {
		return magFilter;
	}

	public void setMagFilter(TextureFilter magFilter) {
		if (this.magFilter == magFilter) {
			this.magFilter = magFilter;
			this.setMagFilters();
		}
	}

	public TextureFilter getMinMipmapFilter() {
		return minMipmapFilter;
	}

	public void setMinMipmapFilter(TextureFilter minMipmapFilter) {
		if (this.minMipmapFilter == minMipmapFilter) {
			this.minMipmapFilter = minMipmapFilter;
			this.setMinFilters();
		}
	}

	public TextureWrap getWrapHorizontal() {
		return wrapHorizontal;
	}

	public void setWrapHorizontal(TextureWrap wrapHorizontal) {
		if (this.wrapHorizontal == wrapHorizontal) {
			this.wrapHorizontal = wrapHorizontal;
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapHorizontal.getGlTextureWrap());
		}
	}

	public TextureWrap getWrapVertical() {
		return wrapVertical;
	}

	public void setWrapVertical(TextureWrap wrapVertical) {
		if (this.wrapVertical == wrapVertical) {
			this.wrapVertical = wrapVertical;
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapHorizontal.getGlTextureWrap());
		}
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		if (!borderColor.equals(this.borderColor)) {
			this.borderColor = borderColor;
			float[] f = Utils.getColorAsRGBA8FloatArray(borderColor);
			glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, f);
		}
	}

	public static void setSTBImageVerticalFlipMode(boolean sTBImageVerticalFlipMode) {
		if (sTBImageVerticalFlipMode != STBImageVerticalFlipMode) {
			STBImageVerticalFlipMode = sTBImageVerticalFlipMode;
			stbi_set_flip_vertically_on_load(sTBImageVerticalFlipMode);
		}
	}

	public void destroy() {
		if (this.closed) return;
		this.closed = true;
		Engine.removeEngineResource(this);
		glDeleteTextures(Texture2D.this.id);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Engine.addEndOfLoopRunnable(() -> this.destroy());
	}
	
	public void bind(int slot) {
		if (Texture2D.boundTextures.get(slot) == this.id) return;
		Texture2D.boundTextures.put(slot, this.id);
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, this.id);
	}
	
	public void bind() {
		this.bind(0);
	}
	
	public static void unbind(int slot) {
		if (Texture2D.boundTextures.get(slot) == 0) return;
		Texture2D.boundTextures.put(slot, 0);
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void unbind() {
		Texture2D.unbind(0);
	}
	
	private void setMinFilters() {
		this.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, TextureFilter.getGLFilters(this.minFilter, this.minMipmapFilter));
	}
	
	private void setMagFilters() {
		this.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, TextureFilter.getGLFilters(this.magFilter, null));
	}
}
