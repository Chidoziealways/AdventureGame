package net.adventuregame.bloom;

import com.chidozie.core.shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bloom/simpleVertexShader.vert";
	private static final String FRAGMENT_FILE = "bloom/combineFragmentShader.frag";
	
	private int location_colourTexture;
	private int location_highlightTexture;
	
	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformLocation("colourTexture");
		location_highlightTexture = super.getUniformLocation("highlightTexture");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_highlightTexture, 1);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
