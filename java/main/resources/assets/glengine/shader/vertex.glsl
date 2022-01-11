uniform mat4 vp;
uniform mat4 model;
uniform vec3 pov;

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoords;
layout(location = 2) in vec3 normal;
layout(location = 3) in vec3 tangent;

out mat3 inTBN;
out vec3 inNormal;
out vec3 inPOV;
out vec3 inPos;
out vec2 inTexCoords;
out vec3 inTangentPOV;
out vec3 inTangentPos;

void main() {
	vec3 T = normalize(vec3(model * vec4(tangent, 0.0)));
	vec3 N = normalize(vec3(model * vec4(normal, 0.0)));
	T = normalize(T - dot(T, N) * N);
	vec3 B = cross(N, T);
	inNormal = mat3(model) * normal;
	inPOV = pov;
	inPos = (model * position).xyz;
	inTexCoords = texCoords;
	inTBN = mat3(T, B, N);
	inTangentPOV = inTBN * inPOV;
	inTangentPos = inTBN * inPos;
	gl_Position = vp * model * position;
}
