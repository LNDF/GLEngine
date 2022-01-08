uniform bool useAlbedoTexture;
uniform bool useNormalMap;
uniform bool useRoughnessTexture;
uniform bool useMetalnessTexture;
uniform bool useAoTexture;
uniform bool useEmissiveTexture;
uniform sampler2D albedoTexture;
uniform sampler2D normalMap;
uniform sampler2D roughnessTexture;
uniform sampler2D metalnessTexture;
uniform sampler2D aoTexture;
uniform sampler2D emissiveTexture;
uniform vec3 albedo;
uniform float roughness;
uniform float metalness;
uniform float ao;
uniform vec3 emissive;

uniform float ambientLightLevel;
uniform int spotlightCount;
uniform int pointLightCount;
uniform int dirLightCount;
uniform Spotlight spotlights[32];
uniform PointLight pointLights[64];
uniform DirLight dirLights[8];

in vec3 inNormal;
in vec3 inPOV;
in vec3 inPos;
in vec2 inTexCoords;
in mat3 inTBN;

out vec4 fragColor;

void main() {
	vec3 albedoColor = albedo;
	if (useAlbedoTexture) {
		albedoColor = sampleTexture(albedoTexture, inTexCoords).rgb;
	}
	vec3 normal = normalize(inNormal);
	if (useNormalMap) {
		normal = sampleTexture(normalMap, inTexCoords).rgb;
		normal = normal * 2.0 - 1.0;
		normal = normalize(inTBN * normal);
	}
	float roughnessLevel = roughness;
	if (useRoughnessTexture) {
		roughnessLevel = sampleTexture(roughnessTexture, inTexCoords).r;
	}
	float metalnessLevel = metalness;
	if (useMetalnessTexture) {
		metalnessLevel = sampleTexture(metalnessTexture, inTexCoords).r;
	}
	float aoLevel = ao;
	if (useAoTexture) {
		aoLevel = sampleTexture(aoTexture, inTexCoords).r;
	}
	vec3 emissiveColor = emissive;
	if (useEmissiveTexture) {
		emissiveColor = sampleTexture(emissiveTexture, inTexCoords).rgb;
	}
	vec3 view = normalize(inPOV - inPos);
	vec3 F0 = vec3(0.04);
	F0 = mix(F0, albedoColor, metalnessLevel);
	vec3 Lo = vec3(0.0);
	for (int i = 0; i < dirLightCount; i++) {
		DirLight light = dirLights[i];
		vec3 radiance = light.color;
		vec3 L = normalize(-light.direction);
		vec3 H = normalize(view + L);
		Lo += cookTorrance(normal, view, H, L, F0, albedoColor, radiance, roughnessLevel, metalnessLevel);
	}
	for (int i = 0; i < pointLightCount; i++) {
		PointLight light = pointLights[i];
		vec3 radiance = calculatePointLight(light, inPos);
		if (radiance.x > 0 || radiance.y > 0 || radiance.z > 0) {
			vec3 L = normalize(light.position - inPos);
			vec3 H = normalize(view + L);
			Lo += cookTorrance(normal, view, H, L, F0, albedoColor, radiance, roughnessLevel, metalnessLevel);
		}
	}
	for (int i = 0; i < spotlightCount; i++) {
		Spotlight light = spotlights[i];
		vec3 radiance = calculateSpotlight(light, inPos);
		if (radiance.x > 0 || radiance.y > 0 || radiance.z > 0) {
			vec3 L = normalize(light.position - inPos);
			vec3 H = normalize(view + L);
			Lo += cookTorrance(normal, view, H, L, F0, albedoColor, radiance, roughnessLevel, metalnessLevel);
		}
	}
	vec3 ambient = ambientLightLevel * albedoColor * aoLevel;
	vec3 color = ambient + Lo;
	color += emissiveColor;
	color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));
    fragColor = vec4(color, 1.0);
}

