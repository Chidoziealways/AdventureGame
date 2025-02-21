package net.adventuregame.bloom;

import com.chidozie.core.shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "bloom/simpleVertexShader.vert";
	private static final String FRAGMENT_FILE = "bloom/brightFilterFragmentShader.frag";
	
	public BrightFilterShader() {
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
