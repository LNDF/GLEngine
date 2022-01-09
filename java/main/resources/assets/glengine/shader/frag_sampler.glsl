vec4 sampleTextureGC(sampler2D textureId, vec2 coords) {
	vec4 c = texture(textureId, coords);
	c = vec4(pow(c.rgb, vec3(2.2)), c.a);
	return c;
}

vec4 sampleTexture(sampler2D textureId, vec2 coords) {
	return texture(textureId, coords);
}