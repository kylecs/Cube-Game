#version 330 core

in vec2 texCords;
in float light;
out vec4 outColor;

uniform sampler2D tex;

void main()
{
    outColor = texture(tex, texCords) * vec4(light, light, light, 1.0f);
}
