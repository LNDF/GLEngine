struct DirLight {
	vec3 direction;
	vec3 color;
};

struct PointLight {
	vec3 position;
	vec3 color;
	float atConstant;
	float atLinear;
	float atQuadratic;
};

struct Spotlight {
	vec3 position;
	vec3 direction;
	vec3 color;
	float cosInnerCone;
	float cosOuterCone;
	float atConstant;
	float atLinear;
	float atQuadratic;
};

vec3 calculateDirLight(DirLight light, vec3 normal, vec3 pov, vec3 color, vec3 specularColor, float shininess);
vec3 calculatePointLight(PointLight light, vec3 normal, vec3 pos, vec3 pov, vec3 color, vec3 specularColor, float shininess);
vec3 calculateSpotlight(Spotlight light, vec3 normal, vec3 pos, vec3 pov, vec3 color, vec3 specularColor, float shininess);
