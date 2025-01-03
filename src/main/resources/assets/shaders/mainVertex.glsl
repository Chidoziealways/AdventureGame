#version 430 core

in vec3 position;
in vec3 color;
in vec2 textureCoord;

out vec3 passColor;
out vec2 passTextureCoord;

uniform float scale;

void main() {
    gl_Position = vec4(position, 1.0) * vec4(scale, scale, scale, 1);
    passColor = color;
    passTextureCoord = textureCoord;
}