uniform mat4 vp;
uniform mat4 model;
uniform vec3 pov;

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec3 normal;

out vec3 inNormal;
out vec3 inPOV;
out vec3 inPos;
out vec2 inTexCoords;

void main() {
	inNormal = mat3(model) * normal;
	inPOV = pov;
	inPos = (model * position).xyz;
	inTexCoords = texCoords;
	gl_Position = vp * model * position;
}
