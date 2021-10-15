package com.lndf.glengine.gl.texture;

import static org.lwjgl.opengl.GL33.*;

public enum TextureFilter {
	LINEAR, NEAREST;
	
	public static int getGLFilters(TextureFilter filter, TextureFilter mipmapFilter) {
		if (mipmapFilter == null) {
			switch (filter) {
				case LINEAR:
					return GL_LINEAR;
				case NEAREST:
					return GL_NEAREST;
			}
		} else if (filter == TextureFilter.LINEAR && mipmapFilter == TextureFilter.LINEAR) {
			return GL_LINEAR_MIPMAP_LINEAR;
		} else if (filter == TextureFilter.LINEAR && mipmapFilter == TextureFilter.NEAREST) {
			return GL_LINEAR_MIPMAP_NEAREST;
		} else if (filter == TextureFilter.NEAREST && mipmapFilter == TextureFilter.LINEAR) {
			return GL_NEAREST_MIPMAP_LINEAR;
		} else if (filter == TextureFilter.NEAREST && mipmapFilter == TextureFilter.NEAREST) {
			return GL_NEAREST_MIPMAP_NEAREST;
		}
		return -1;
	}
}
