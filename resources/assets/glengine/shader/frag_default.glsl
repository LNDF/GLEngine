uniform vec4 color_shininess;
uniform vec4 color_diffuse;
uniform vec4 color_specular;
uniform int count_specular;
uniform sampler2D texture_specular0;
uniform int count_diffuse;
uniform sampler2D texture_diffuse0;

uniform float ambientLightLevel;
uniform int spotlightCount;
uniform int pointLightCount;
uniform int dirLightCount;
uniform Spotlight spotlights[64];
uniform PointLight pointLights[128];
uniform DirLight dirLights[16];

in vec3 inNormal;
in vec3 inPOV;
in vec3 inPos;
in vec2 inTexCoords;

layout(location = 0) out vec4 outColor;

void main() {
	//base color
	vec4 texColor = color_diffuse;
	if (count_diffuse > 0) {
		texColor = sampleTexture(texture_diffuse0, inTexCoords);
	}
	vec3 specularColor = color_specular.xyz;
	if (count_specular > 0) {
		specularColor = sampleTexture(texture_specular0, inTexCoords).xyz;
	}
	vec3 baseColor = texColor.xyz;
	//light
	vec3 norm = normalize(inNormal);
	vec3 povDir = normalize(inPOV - inPos);
	//Ambient
	vec3 finalColor = baseColor * ambientLightLevel;
	//Directional
	for (int i = 0; i < dirLightCount; i++) {
		finalColor += calculateDirLight(dirLights[i], norm, povDir, baseColor, specularColor, color_shininess.x);

		//gl_FragColor = vec4(a, 1.0);
		//return;
	}
	//Point light
	for (int i = 0; i < pointLightCount; i++) {
		finalColor += calculatePointLight(pointLights[i], norm, inPos, povDir, baseColor, specularColor, color_shininess.x);
	}
	//Spotlights
	for (int i = 0; i < spotlightCount; i++) {
		finalColor += calculateSpotlight(spotlights[i], norm, inPos, povDir, baseColor, specularColor, color_shininess.x);
	}
	outColor = vec4(finalColor, texColor.w);
}
