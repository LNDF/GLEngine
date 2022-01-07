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
