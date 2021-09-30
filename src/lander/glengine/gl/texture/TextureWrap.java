package lander.glengine.gl.texture;

import static org.lwjgl.opengl.GL33.*;

public enum TextureWrap {
	REPEAT(GL_REPEAT), MIRRORED_REPEAT(GL_MIRRORED_REPEAT), CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE), CLAM_TO_BORDER(GL_CLAMP_TO_BORDER);
	
	private int glTextureWrap;
	
	private TextureWrap(int glTextureWrap) {
		this.glTextureWrap = glTextureWrap;
	}
	
	public int getGlTextureWrap() {
		return glTextureWrap;
	}
}
