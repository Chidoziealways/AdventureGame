#version 440 core

in vec2 pass_textureCoords;

out vec4 out_Colour;

uniform vec3 colour;
uniform sampler2D fontAtlas;

const float width = 0.5;
const float edge = 0.1;

const float borderWidth = 0.4;
const float borderEdge = 0.5;

const vec2 offset = vec2(0.006, 0.006);

const vec3 outLineColour = vec3(1.0, 0.0, 0.0);

void main(void){

    float distance = texture(fontAtlas, pass_textureCoords).r;
    float alpha = smoothstep(width, width + edge, distance);

    float distance2 = texture(fontAtlas, pass_textureCoords + offset).r;
    float outLineAlpha = smoothstep(borderWidth, borderWidth + borderEdge, distance2);

    float overallAlpha = alpha + (1.0 - alpha) * outLineAlpha;
    vec3 overallColour = mix(outLineColour, colour, alpha / overallAlpha);

    out_Colour = vec4(overallColour, overallAlpha);

}