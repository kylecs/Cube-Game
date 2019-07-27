#version 330 core

layout(location = 1) in vec3 position;
layout(location = 2) in vec2 passTex;
layout(location = 3) in float passLight;

out vec2 texCords;
out float light;

uniform mat4 projectionMatrix;

void main()
{
	light = passLight;
	texCords = passTex;
    gl_Position = projectionMatrix * vec4(position, 1.0f);
}
