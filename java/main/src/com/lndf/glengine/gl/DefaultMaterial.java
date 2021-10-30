package com.lndf.glengine.gl;

import java.util.HashSet;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Window;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.gl.texture.Texture2DRoles;
import com.lndf.glengine.gl.texture.TextureRole;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Scene;
import com.lndf.glengine.scene.components.lighting.DirectionalLight;
import com.lndf.glengine.scene.components.lighting.PointLight;
import com.lndf.glengine.scene.components.lighting.Spotlight;

public class DefaultMaterial extends Material {

	private Texture2DRoles roles = new Texture2DRoles();
	
	public static Shader defaultShader = null;
	
	static {
		Window.addTerminateRunnable(() -> defaultShader = null);
	}
	
	public static Shader createDefaultShader() {
		if (defaultShader != null) return defaultShader;
		String[] vertex = new String[] {
				Shader.SHADER_VERSION,
				Shader.readShader(new Asset("resource:/assets/glengine/shader/vertex_default.glsl")),
		};
		String[] fragment = new String[] {
				Shader.SHADER_VERSION,
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_default_sampler.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_default_light_declarations.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_default_light.glsl")),
				Shader.readShader(new Asset("resource:/assets/glengine/shader/frag_default.glsl"))
		};
		defaultShader = new Shader(vertex, fragment, null);
		return defaultShader;
	}
	
	public DefaultMaterial(Shader shader, Texture2DRoles textures) {
		super(shader);
		this.roles = textures;
	}
	
	public DefaultMaterial(Texture2DRoles textures) {
		super(createDefaultShader());
		this.roles = textures;
	}
	
	public DefaultMaterial(Shader shader, Vector4f color, Vector4f specularColor, float shininess) {
		super(shader);
		this.roles.setDefaultColor(TextureRole.DIFFUSE, color);
		this.roles.setDefaultColor(TextureRole.SPECULAR, specularColor);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Shader shader, Vector4f color, Texture2D specularTexture, float shininess) {
		super(shader);
		this.roles.setDefaultColor(TextureRole.DIFFUSE, color);
		this.roles.addTexture(TextureRole.SPECULAR, specularTexture);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Shader shader, Texture2D texture, Vector4f specularColor, float shininess) {
		super(shader);
		this.roles.addTexture(TextureRole.DIFFUSE, texture);
		this.roles.setDefaultColor(TextureRole.SPECULAR, specularColor);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Shader shader, Texture2D texture, Texture2D specularTexture, float shininess) {
		super(shader);
		this.roles.addTexture(TextureRole.DIFFUSE, texture);
		this.roles.addTexture(TextureRole.SPECULAR, specularTexture);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Vector4f color, Vector4f specularColor, float shininess) {
		super(createDefaultShader());
		this.roles.setDefaultColor(TextureRole.DIFFUSE, color);
		this.roles.setDefaultColor(TextureRole.SPECULAR, specularColor);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Vector4f color, Texture2D specularTexture, float shininess) {
		super(createDefaultShader());
		this.roles.setDefaultColor(TextureRole.DIFFUSE, color);
		this.roles.addTexture(TextureRole.SPECULAR, specularTexture);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Texture2D texture, Vector4f specularColor, float shininess) {
		super(createDefaultShader());
		this.roles.addTexture(TextureRole.DIFFUSE, texture);
		this.roles.setDefaultColor(TextureRole.SPECULAR, specularColor);
		this.setShininess(shininess);
	}
	
	public DefaultMaterial(Texture2D texture, Texture2D specularTexture, float shininess) {
		super(createDefaultShader());
		this.roles.addTexture(TextureRole.DIFFUSE, texture);
		this.roles.addTexture(TextureRole.SPECULAR, specularTexture);
		this.setShininess(shininess);
	}
	
	public void setShininess(float shininess) {
		this.roles.setDefaultColor(TextureRole.SHININESS, new Vector4f(shininess, 0, 0, 0));
	}
	
	public Texture2DRoles getTextureRole() {
		return this.roles;
	}

	@Override
	public void setUniform(Matrix4f vp, GameObject obj, Vector3f pov) {
		Shader shader = this.getShader();
		Scene scene = obj.getScene();
		if (scene == null) return;
		this.setVMP(vp, scene, obj, pov);
		this.roles.setUniforms(shader);
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
