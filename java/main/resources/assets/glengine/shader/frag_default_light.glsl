vec3 calculateDirLight(DirLight light, vec3 normal, vec3 pov, vec3 color, vec3 specularColor, float shininess) {
	vec3 lightDir = normalize(-light.direction);
	//diffuse
	float diffuse = max(dot(normal, lightDir), 0.0);
	//specular
	vec3 reflectionDir = reflect(-lightDir, normal);
	float specular = pow(max(dot(pov, reflectionDir), 0.0), shininess);
	//final
	vec3 diffuseFinal = light.color * diffuse * color;
	vec3 specularFinal = light.color * specular * specularColor;
	return (diffuseFinal + specularFinal);
}

vec3 calculatePointLight(PointLight light, vec3 normal, vec3 pos, vec3 pov, vec3 color, vec3 specularColor, float shininess) {
	vec3 lightDir = normalize(light.position - pos);
	//diffuse
	float diffuse = max(dot(normal, lightDir), 0.0);
	//specular
	vec3 reflectionDir = reflect(-lightDir, normal);
	float specular = pow(max(dot(pov, reflectionDir), 0.0), shininess);
	//final
	vec3 diffuseFinal = light.color * diffuse * color;
	vec3 specularFinal = light.color * specular * specularColor;
	//Attenuation
	float distance = length(light.position - pos);
	float attenuation = 1.0 / (light.atConstant + light.atLinear * distance + light.atQuadratic * (distance * distance));
	diffuseFinal *= attenuation;
	specularFinal *= attenuation;
	return (diffuseFinal + specularFinal);
}

vec3 calculateSpotlight(Spotlight light, vec3 normal, vec3 pos, vec3 pov, vec3 color, vec3 specularColor, float shininess) {
	vec3 lightDir = normalize(light.position - pos);
	//diffuse
	float diffuse = max(dot(normal, lightDir), 0.0);
	//specular
	vec3 reflectionDir = reflect(-lightDir, normal);
	float specular = pow(max(dot(pov, reflectionDir), 0.0), shininess);
	//final
	vec3 diffuseFinal = light.color * diffuse * color;
	vec3 specularFinal = light.color * specular * specularColor;
	//Attenuation
	float distance = length(light.position - pos);
	float attenuation = 1.0 / (light.atConstant + light.atLinear * distance + light.atQuadratic * (distance * distance));
	//Spotlight
	float angle = dot(lightDir, normalize(-light.direction));
	float angleDiff = light.cosInnerCone - light.cosOuterCone;
	float spotlight = clamp((angle - light.cosOuterCone) / angleDiff, 0.0, 1.0);
	//Intensity
	diffuseFinal *= attenuation * spotlight;
	specularFinal *= attenuation * spotlight;
	return (diffuseFinal + specularFinal);
}
