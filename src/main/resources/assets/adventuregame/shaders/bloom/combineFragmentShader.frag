#version 440 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;
uniform sampler2D highlightTexture;

void main(void){

    vec4 sceneColour = texture(colourTexture, textureCoords);
    vec4 highlightColour = texture(highlightTexture, textureCoords);
    out_Colour = sceneColour + highlightColour * 0.99089;

}