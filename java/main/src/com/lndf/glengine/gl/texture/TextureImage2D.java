package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.system.MemoryStack;

import com.lndf.glengine.asset.Asset;

public class TextureImage2D extends Texture2D {
	
	private HashMap<Integer, Integer> width = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> height = new HashMap<Integer, Integer>();
	
	private String path;
	
	private TextureFilter minMipmapFilter = TextureFilter.LINEAR;
	
	protected static boolean STBImageVerticalFlipMode = false;
	
	public TextureImage2D() {
		super();
	}
	
	public TextureImage2D(Asset asset) {
		this();
		this.setTexture(asset, 0);
		this.setDefaultTextureSettings();
	}
	
	public TextureImage2D(ByteBuffer input) {
		this();
		this.setTexture(input, 0);
		this.setDefaultTextureSettings();
	}
	
	public void setDefaultTextureSettings() {
		this.autoGenerateMipmaps();
		super.setDefaultTextureSettings();
	}
	
	public void setTexture(Asset asset, int mipmapLevel) {
		try {
			this.path = asset.toString();
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
			TextureImage2D.setSTBImageVerticalFlipMode(true);
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer c = stack.mallocInt(1); //Not used
			ByteBuffer img = stbi_load_from_memory(input, w, h, c, 4);
			if (img == null) {
				throw new RuntimeException("Couldn't load texture" + (this.path != null ? " " + path : "") + ". Reason: " + stbi_failure_reason());
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

	public TextureFilter getMinMipmapFilter() {
		return minMipmapFilter;
	}

	public void setMinMipmapFilter(TextureFilter minMipmapFilter) {
		if (this.minMipmapFilter == minMipmapFilter) {
			this.minMipmapFilter = minMipmapFilter;
			this.setMinFilters();
		}
	}

	public static void setSTBImageVerticalFlipMode(boolean sTBImageVerticalFlipMode) {
		if (sTBImageVerticalFlipMode != STBImageVerticalFlipMode) {
			STBImageVerticalFlipMode = sTBImageVerticalFlipMode;
			stbi_set_flip_vertically_on_load(sTBImageVerticalFlipMode);
		}
	}
	
	protected void setMinFilters() {
		this.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, TextureFilter.getGLFilters(this.minFilter, this.minMipmapFilter));
	}
}
