struct DirLight {
	vec3 direction;
	vec3 color;
};

struct PointLight {
	vec3 position;
	vec3 color;
	float radius;
	float strength;
};

struct Spotlight {
	vec3 position;
	vec3 direction;
	vec3 color;
	vec3 minColor;
	float cosInnerCone;
	float cosOuterCone;
	float radius;
	float strength;
};
