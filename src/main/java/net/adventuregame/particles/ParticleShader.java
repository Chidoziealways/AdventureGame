package net.adventuregame.particles;

import com.chidozie.core.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particles/particleVertexShader.vert";
	private static final String FRAGMENT_FILE = "particles/particleFragmentShader.frag";
	private static final Logger log = LoggerFactory.getLogger(ParticleShader.class);

	private int location_numberOfRows;
	private int location_projectionMatrix;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = getUniformLocation("numberOfRows");
		log.info("numberOfRowsLocation: " + location_numberOfRows);
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		log.info("projectionMatrixLocation: " + location_projectionMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	protected void loadNumberOfRows(float numberOfRows) {
		loadFloat(location_numberOfRows, numberOfRows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
