uniform bool useAlbedoTexture;
uniform bool useNormalMap;
uniform bool useDisplacementMap;
uniform bool useRoughnessTexture;
uniform bool useMetalnessTexture;
uniform bool useAoTexture;
uniform bool useEmissiveTexture;
uniform sampler2D albedoTexture;
uniform sampler2D normalMap;
uniform sampler2D displacementMap;
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
in vec3 inTangentPOV;
in vec3 inTangentPos;

out vec4 fragColor;

vec2 parallex(vec2 texCoords, vec3 viewDir) {
	float numLayers = mix(32.0, 8.0, max(dot(vec3(0.0, 0.0, 1.0), viewDir), 0.0));
	float layerDepth = 1.0 / numLayers;
	float currentLayerDepth = 0.0;
	vec2 P = viewDir.xy * 1.0;
	vec2 deltaTexCoords = P / numLayers;
	vec2 currentTexCoords = texCoords;
	float currentDepthMapValue = 1.0 - sampleTexture(displacementMap, currentTexCoords).g;
	while (currentLayerDepth < currentDepthMapValue) {
		currentTexCoords -= deltaTexCoords;
		currentDepthMapValue = 1.0 - sampleTexture(displacementMap, currentTexCoords).g;
		currentLayerDepth += layerDepth;
	}
	vec2 prevTexCoords = currentTexCoords + deltaTexCoords;
	float afterDepth  = currentDepthMapValue - currentLayerDepth;
	float beforeDepth = 1.0 - sampleTexture(displacementMap, prevTexCoords).g - currentLayerDepth + layerDepth;
	float weight = afterDepth / (afterDepth - beforeDepth);
	vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);
	return finalTexCoords;
}

void main() {
	vec2 texCoords = inTexCoords;
	if (useDisplacementMap) {
		texCoords = parallex(texCoords, normalize(inTangentPOV - inTangentPos));
		if(texCoords.x > 1.0 || texCoords.y > 1.0 || texCoords.x < 0.0 || texCoords.y < 0.0)
    		discard;
	}
	vec3 albedoColor = albedo;
	if (useAlbedoTexture) {
		albedoColor = sampleTextureGC(albedoTexture, texCoords).rgb;
	}
	vec3 normal = normalize(inNormal);
	if (useNormalMap) {
		normal = sampleTexture(normalMap, texCoords).rgb;
		normal = normal * 2.0 - 1.0;
		normal = normalize(inTBN * normal);
	}
	float roughnessLevel = roughness;
	if (useRoughnessTexture) {
		roughnessLevel = sampleTexture(roughnessTexture, texCoords).r;
	}
	float metalnessLevel = metalness;
	if (useMetalnessTexture) {
		metalnessLevel = sampleTexture(metalnessTexture, texCoords).r;
	}
	float aoLevel = ao;
	if (useAoTexture) {
		aoLevel = sampleTexture(aoTexture, texCoords).r;
	}
	vec3 emissiveColor = emissive;
	if (useEmissiveTexture) {
		emissiveColor = sampleTextureGC(emissiveTexture, texCoords).rgb;
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
		vec3 L = normalize(light.position - inPos);
		vec3 H = normalize(view + L);
		Lo += cookTorrance(normal, view, H, L, F0, albedoColor, radiance, roughnessLevel, metalnessLevel);
	}
	for (int i = 0; i < spotlightCount; i++) {
		Spotlight light = spotlights[i];
		vec3 radiance = calculateSpotlight(light, inPos);
		vec3 L = normalize(light.position - inPos);
		vec3 H = normalize(view + L);
		Lo += cookTorrance(normal, view, H, L, F0, albedoColor, radiance, roughnessLevel, metalnessLevel);
	}
	vec3 ambient = ambientLightLevel * albedoColor * aoLevel;
	vec3 color = ambient + Lo;
	color += emissiveColor;
	color = color / (color + vec3(1.0));
    color = pow(color, vec3(1.0/2.2));
    fragColor = vec4(color, 1.0);
}

