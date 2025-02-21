package com.chidozie.core.font;

import com.chidozie.core.shaders.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "font/fontVertexShader.vert";
	private static final String FRAGMENT_FILE = "font/fontFragmentShader.frag";

	private int location_translation;
	private int location_colour;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = getUniformLocation("colour");
		location_translation = getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "textureCoords");
	}

	protected void loadColour(Vector3f colour) {
		loadVector3f(location_colour, colour);
	}

	protected void loadTranslation(Vector2f translation) {
		loadVector2f(location_translation, translation);
	}

}
