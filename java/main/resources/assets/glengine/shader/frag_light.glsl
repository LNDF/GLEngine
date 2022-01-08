vec3 calculatePointLight(PointLight light, vec3 pos) {
	//Attenuation
	float distance = length(light.position - pos);
	float attenuation = clamp(1.0 - pow(distance, light.strength)/pow(light.radius, light.strength), 0.0, 1.0);
	attenuation *= attenuation;
	return light.color * attenuation;
}

vec3 calculateSpotlight(Spotlight light, vec3 pos) {
	vec3 lightDir = normalize(light.position - pos);
	float distance = length(light.position - pos);
	float attenuation = clamp(1.0 - pow(distance, light.strength)/pow(light.radius, light.strength), 0.0, 1.0);
	attenuation *= attenuation;
	//Spotlight
	float angle = dot(lightDir, normalize(-light.direction));
	float angleDiff = light.cosInnerCone - light.cosOuterCone;
	float spotlight = clamp((angle - light.cosOuterCone) / angleDiff, 0.0, 1.0);
	return attenuation * spotlight * light.color;
}
