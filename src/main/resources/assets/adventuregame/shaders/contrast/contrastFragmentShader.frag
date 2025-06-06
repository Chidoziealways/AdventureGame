#version 440 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

const float contrast = 0.3;

void main(void){

	out_Colour = texture(colourTexture, textureCoords);
	out_Colour.rgb = (out_Colour.rgb - 0.5) * (1.0 + contrast) + 0.5;

}