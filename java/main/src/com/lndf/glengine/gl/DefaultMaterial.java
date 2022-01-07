package com.lndf.glengine.gl;

import java.util.HashSet;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Engine;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.gl.texture.Texture2DRoles;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.lighting.PointLight;
import com.lndf.glengine.scene.components.lighting.Spotlight;

public class DefaultMaterial extends Material {
	
	private Texture2DRoles roles = new Texture2DRoles();
	
	public static Shader defaultShader = null;
	
	static {
		Engine.addTerminateRunnable(() -> defaultShader = null);
	}
	
	public static Shader createDefaultShader() {
		if (defaultShader != null) return defaultShader;
		String[] vertex = new String[] {
				Shader.SHADER_VERSION,
				Shader.readShader(new Asset("resource:/assets/glengine/shader/vertex.glsl")),
		};
		String[] fragment = new String[] {
				Shader.SHADER_VERSION,
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_sampler.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_light_declarations.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_light.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_cooktorrance.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_pbr.glsl"))
		};
		defaultShader = new Shader(vertex, fragment, null);
		return defaultShader;
	}
	
	public DefaultMaterial(Shader shader) {
		super(shader);
	}
	
	public DefaultMaterial() {
		super(createDefaultShader());
	}

	public Texture2DRoles getRoles() {
		return roles;
	}

	public void setRoles(Texture2DRoles roles) {
		this.roles = roles;
	}

	public Texture2D getAlbedoTexture() {
		return this.roles.getAlbedoTexture();
	}

	public void setAlbedoTexture(Texture2D albedoTexture) {
		this.roles.setAlbedoTexture(albedoTexture);
	}

	public Texture2D getNormalMap() {
		return this.roles.getNormalMap();
	}

	public void setNormalMap(Texture2D normalMap) {
		this.roles.setNormalMap(normalMap);
	}

	public Texture2D getRoughnessTexture() {
		return this.roles.getRoughnessTexture();
	}

	public void setRoughnessTexture(Texture2D roughnessTexture) {
		this.roles.setRoughnessTexture(roughnessTexture);
	}

	public Texture2D getMetalnessTexture() {
		return this.roles.getMetalnessTexture();
	}

	public void setMetalnessTexture(Texture2D metalnessTexture) {
		this.roles.setMetalnessTexture(metalnessTexture);
	}

	public Texture2D getAoTexture() {
		return this.roles.getAoTexture();
	}

	public void setAoTexture(Texture2D aoTexture) {
		this.setAoTexture(aoTexture);
	}

	public Texture2D getEmissiveTexture() {
		return this.roles.getEmissiveTexture();
	}

	public void setEmissiveTexture(Texture2D emissiveTexture) {
		this.roles.setEmissiveTexture(emissiveTexture);
	}

	public Vector3f getAlbedoColor() {
		return this.roles.getAlbedoColor();
	}

	public void setAlbedoColor(Vector3f albedoColor) {
		this.roles.setAlbedoColor(albedoColor);
	}

	public float getAo() {
		return this.roles.getAo();
	}

	public void setAo(float ao) {
		this.roles.setAo(ao);
	}

	public Vector3f getEmissiveColor() {
		return this.roles.getEmissiveColor();
	}

	public void setEmissiveColor(Vector3f emissiveColor) {
		this.roles.setEmissiveColor(emissiveColor);
	}

	public float getRoughness() {
		return this.roles.getRoughness();
	}

	public void setRoughness(float roughness) {
		this.roles.setRoughness(roughness);
	}

	public float getMetalness() {
		return this.getMetalness();
	}

	public void setMetalness(float metalness) {
		this.roles.setMetalness(metalness);
	}

	@Override
	public void setUniform(Matrix4f vp, GameObject obj, Vector3f pov) {
		Shader shader = this.shader;
		Scene scene = obj.getScene();
		if (scene == null) return;
		this.setVMP(vp, scene, obj, pov);
		this.roles.setUniforms(this.shader);
		//lighting
		HashSet<DirectionalLight> dLights = scene.getDirectionalLights();
		HashSet<PointLight> pLights = scene.getPointLights();
		HashSet<Spotlight> sLights = scene.getSpotlights();
		int counter = 0;
		shader.setUniform("ambientLightLevel", scene.getAmbientLight());
		//directional
		for (DirectionalLight light : dLights) {
			Vector3f color = light.getColor();
			Vector3f direction = light.getGameObject().getTransform().getFront();
			shader.setUniform("dirLights[" + counter + "].color", color.x, color.y, color.z);
			shader.setUniform("dirLights[" + counter + "].direction", direction.x, direction.y, direction.z);
			counter++;
		}
		shader.setUniform("dirLightCount", counter);
		//point light
		counter = 0;
		for (PointLight light : pLights) {
			Vector3f color = light.getColor();
			Vector3f position = light.getGameObject().getTransform().getWorldPosition();
			shader.setUniform("pointLights[" + counter + "].color", color.x, color.y, color.z);
			shader.setUniform("pointLights[" + counter + "].position", position.x, position.y, position.z);
			shader.setUniform("pointLights[" + counter + "].atConstant", light.getAtConstant());
			shader.setUniform("pointLights[" + counter + "].atLinear", light.getAtLinear());
			shader.setUniform("pointLights[" + counter + "].atQuadratic", light.getAtQuadratic());
			counter++;
		}
		shader.setUniform("pointLightCount", counter);
		//spotlight
		counter = 0;
		for (Spotlight light : sLights) {
			Vector3f color = light.getColor();
			Vector3f direction = light.getGameObject().getTransform().getFront();
			Vector3f position = light.getGameObject().getTransform().getWorldPosition();
			shader.setUniform("spotlights[" + counter + "].color", color.x, color.y, color.z);
			shader.setUniform("spotlights[" + counter + "].position", position.x, position.y, position.z);
			shader.setUniform("spotlights[" + counter + "].direction", direction.x, direction.y, direction.z);
			shader.setUniform("spotlights[" + counter + "].atConstant", light.getAtConstant());
			shader.setUniform("spotlights[" + counter + "].atLinear", light.getAtLinear());
			shader.setUniform("spotlights[" + counter + "].atQuadratic", light.getAtQuadratic());
			shader.setUniform("spotlights[" + counter + "].cosInnerCone", (float) Math.cos(light.getInnerCone()));
			shader.setUniform("spotlights[" + counter + "].cosOuterCone", (float) Math.cos(light.getOuterCone()));
			counter++;
		}
		shader.setUniform("spotlightCount", counter);
	}
	
}
