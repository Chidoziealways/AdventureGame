#version 440 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform float isSelected; // optional highlight

void main(void) {
    vec4 color = texture(modelTexture, pass_textureCoords);

    if (color.a < 0.1) discard;

    if (isSelected > 0.5) {
        color.rgb = mix(color.rgb, vec3(1.0, 1.0, 0.3), 0.4); // highlight
    }

    out_Color = color;
}
