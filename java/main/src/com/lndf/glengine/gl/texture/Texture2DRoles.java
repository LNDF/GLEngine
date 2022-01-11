package com.lndf.glengine.gl.texture;

import org.joml.Vector3f;
import com.lndf.glengine.gl.Shader;

public class Texture2DRoles {

	private Texture2D albedoTexture;
	private Texture2D normalMap;
	private Texture2D roughnessTexture;
	private Texture2D metalnessTexture;
	private Texture2D aoTexture;
	private Texture2D emissiveTexture;

	private Vector3f albedoColor = new Vector3f(0.6f);
	private float ao = 1.0f;
	private Vector3f emissiveColor = new Vector3f(0);
	private float roughness = 0;
	private float metalness = 0;

	public Texture2D getAlbedoTexture() {
		return albedoTexture;
	}

	public void setAlbedoTexture(Texture2D albedoTexture) {
		this.albedoTexture = albedoTexture;
	}

	public Texture2D getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(Texture2D normalMap) {
		this.normalMap = normalMap;
	}

	public Texture2D getRoughnessTexture() {
		return roughnessTexture;
	}

	public void setRoughnessTexture(Texture2D roughnessTexture) {
		this.roughnessTexture = roughnessTexture;
	}

	public Texture2D getMetalnessTexture() {
		return metalnessTexture;
	}

	public void setMetalnessTexture(Texture2D metalnessTexture) {
		this.metalnessTexture = metalnessTexture;
	}

	public Texture2D getAoTexture() {
		return aoTexture;
	}

	public void setAoTexture(Texture2D aoTexture) {
		this.aoTexture = aoTexture;
	}

	public Texture2D getEmissiveTexture() {
		return emissiveTexture;
	}

	public void setEmissiveTexture(Texture2D emissiveTexture) {
		this.emissiveTexture = emissiveTexture;
	}

	public Vector3f getAlbedoColor() {
		return albedoColor;
	}

	public void setAlbedoColor(Vector3f albedoColor) {
		this.albedoColor = albedoColor;
	}

	public float getAo() {
		return ao;
	}

	public void setAo(float ao) {
		this.ao = ao;
	}

	public Vector3f getEmissiveColor() {
		return emissiveColor;
	}

	public void setEmissiveColor(Vector3f emissiveColor) {
		this.emissiveColor = emissiveColor;
	}

	public float getRoughness() {
		return roughness;
	}

	public void setRoughness(float roughness) {
		this.roughness = roughness;
	}

	public float getMetalness() {
		return metalness;
	}

	public void setMetalness(float metalness) {
		this.metalness = metalness;
	}

	public void setUniforms(Shader shader) {
		shader.setUniform("useAlbedoTexture", this.albedoTexture != null);
		shader.setUniform("useNormalMap", this.normalMap != null);
		shader.setUniform("useRoughnessTexture", this.roughnessTexture != null);
		shader.setUniform("useMetalnessTexture", this.metalnessTexture != null);
		shader.setUniform("useAoTexture", this.aoTexture != null);
		shader.setUniform("useEmissiveTexture", this.emissiveTexture != null);
		if (this.albedoTexture != null) {
			this.albedoTexture.bind(0);
			shader.setUniform("albedoTexture", 0);
		} else {
			shader.setUniform("albedo", this.albedoColor);
		}
		if (this.normalMap != null) {
			this.normalMap.bind(1);
			shader.setUniform("normalMap", 1);
		}
		if (this.roughnessTexture != null) {
			this.roughnessTexture.bind(2);
			shader.setUniform("roughnessTexture", 2);
		} else {
			shader.setUniform("roughness", this.roughness);
		}
		if (this.metalnessTexture != null) {
			this.metalnessTexture.bind(3);
			shader.setUniform("metalnessTexture", 3);
		} else {
			shader.setUniform("metalness", this.metalness);
		}
		if (this.aoTexture != null) {
			this.aoTexture.bind(4);
			shader.setUniform("aoTexture", 4);
		} else {
			shader.setUniform("ao", this.ao);
		}
		if (this.emissiveTexture != null) {
			this.emissiveTexture.bind(5);
			shader.setUniform("emissiveTexture", 5);
		} else {
			shader.setUniform("emissive", this.emissiveColor);
		}
	}

}
