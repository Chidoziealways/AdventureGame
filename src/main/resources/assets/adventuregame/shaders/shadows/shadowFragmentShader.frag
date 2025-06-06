#version 440 core

in vec2 textureCoords;

out vec4 out_colour;

uniform sampler2D modelTexture;

void main(void){

	float alpha = texture(modelTexture, textureCoords).a;
	if(alpha < 0.5) {
		discard;
	}

	out_colour = vec4(1.0);
	
}