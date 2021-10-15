 #version 330 core

layout(location = 0) out vec4 color;
uniform sampler2D uTexSlot;
uniform vec2 uTexCount;
uniform vec2 uTexOff;

in vec2 vTexCoords;

void main() {
	vec2 pos = vec2(vTexCoords.x * uTexCount.x, vTexCoords.y * uTexCount.y);
	color = texture(uTexSlot, mod(pos + uTexOff, vec2(1.0, 1.0)));
}