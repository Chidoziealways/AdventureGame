#version 440 core

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;       // top-left corner of text in pixels
uniform mat4 projectionMatrix;  // orthographic projection

void main(void){
    vec2 pos = position + translation;
    gl_Position = projectionMatrix * vec4(pos, 0.0, 1.0);
    pass_textureCoords = textureCoords;
}
