#version 440 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

uniform mat4 mvpMatrix;
uniform float useFakeLighting;
uniform float numberOfRows;
uniform vec2 offset;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;

void main(void) {
    vec3 norm = normal;
    if (useFakeLighting > 0.5) {
        norm = vec3(0.0, 1.0, 0.0);
    }

    surfaceNormal = norm;
    pass_textureCoords = (textureCoords / numberOfRows) + offset;
    gl_Position = mvpMatrix * vec4(position, 1.0);
}
