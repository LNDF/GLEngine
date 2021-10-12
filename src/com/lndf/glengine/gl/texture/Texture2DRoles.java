package com.lndf.glengine.gl.texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector4f;

import com.lndf.glengine.gl.Shader;

public class Texture2DRoles extends HashMap<TextureRole, ArrayList<Texture2D>> {

	private static final long serialVersionUID = -1184317033443075100L;
	
	private HashMap<TextureRole, Vector4f> defaultColors = new HashMap<TextureRole, Vector4f>();
	
	public boolean addTexture(TextureRole role, Texture2D texture) {
		if (!this.containsKey(role))
			this.put(role, new ArrayList<Texture2D>());
		return this.get(role).add(texture);
	}
	
	public boolean removeTexture(TextureRole role, Texture2D texture) {
		if (!this.containsKey(role)) return false;
		return this.get(role).remove(texture);
	}
	
	public void setDefaultColor(TextureRole role, Vector4f color) {
		this.defaultColors.put(role, color);
	}
	
	public void removeDefaultColor(TextureRole role) {
		this.defaultColors.remove(role);
	}
	
	public Vector4f getDefaultColor(TextureRole role) {
		return this.defaultColors.get(role);
	}
	
	public HashMap<TextureRole, Vector4f> getDefaultColors() {
		return this.defaultColors;
	}
	
	public void setUniforms(Shader shader) {
		for (Map.Entry<TextureRole, Vector4f> color : this.defaultColors.entrySet()) {
			TextureRole key = color.getKey();
			Vector4f value = color.getValue();
			shader.setUniform("color_" + key.getName(), value.x, value.y, value.z, value.w);
		}
		int bindCount = 0;
		for (TextureRole role : TextureRole.values()) {
			int count = 0;
			if (this.containsKey(role)) {
				ArrayList<Texture2D> textures = this.get(role);
				count = textures.size();
				for (int i = 0; i < textures.size(); i++) {
					textures.get(i).bind(++bindCount);
					
					shader.setUniform("texture_" + role.getName() + i, bindCount);
				}
				
			}
			shader.setUniform("count_" + role.getName(), count);
		}
	}
	
	@Override
	public ArrayList<Texture2D> put(TextureRole key, ArrayList<Texture2D> value) {
		if (value != null) return super.put(key, value);
		return null;
	}
	
}
