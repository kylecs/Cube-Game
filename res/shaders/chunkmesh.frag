#version 330 core

in vec2 texCords;
in float light;
in vec3 passPosition;
out vec4 outColor;

uniform sampler2D tex;

uniform float fogDensity = 0.004f;
uniform vec3 fogColor = vec3(0.5f, 0.5f, 0.5f);

vec4 calcFog(vec3 pos, vec4 colour)
{
    float distance = length(pos);
    float fogFactor = 1.0 / exp( (distance * fogDensity)* (distance * fogDensity));
    fogFactor = clamp( fogFactor, 0.0, 1.0 );

    vec3 resultColour = mix(fogColor, colour.xyz, fogFactor);
    return vec4(resultColour.xyz, colour.w);
}

void main()
{
    vec4 tempColor = texture(tex, texCords) * vec4(light, light, light, 1.0f);
    outColor = calcFog(passPosition, tempColor);
}
