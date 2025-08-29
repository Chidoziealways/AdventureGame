#version 440 core

in vec2 pass_textureCoords;
out vec4 out_Colour;

uniform vec3 colour;
uniform sampler2D fontAtlas;

const float smoothing = 0.04;
const vec3 outLineColour = vec3(1.0, 0.0, 0.0);

void main(void){
    float distance = texture(fontAtlas, pass_textureCoords).r;
    float alpha = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
    float outlineAlpha = smoothstep(0.5 - 2.0 * smoothing, 0.5 + 2.0 * smoothing, distance);
    vec3 finalColour = mix(outLineColour, colour, alpha);
    float finalAlpha = max(alpha, outlineAlpha);
    out_Colour = vec4(finalColour, finalAlpha);
}
