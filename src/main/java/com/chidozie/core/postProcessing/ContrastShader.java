package com.chidozie.core.postProcessing;

import com.chidozie.core.shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "contrast/contrastVertexShader.vert";
	private static final String FRAGMENT_FILE = "contrast/contrastFragmentShader.frag";
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
