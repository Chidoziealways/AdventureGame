package net.adventuregame.hud;

import com.chidozie.core.shaders.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class HudShader extends ShaderProgram {

    private static final String VERTEX_FILE = "hud/hudVertexShader.vert";
    private static final String FRAGMENT_FILE = "hud/hudFragmentShader.frag";

    private int location_mvpMatrix;
    private int location_useFakeLighting;
    private int location_numberOfRows;
    private int location_offset;
    private int location_isSelected;

    public HudShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = getUniformLocation("mvpMatrix");
        location_useFakeLighting = getUniformLocation("useFakeLighting");
        location_numberOfRows = getUniformLocation("numberOfRows");
        location_offset = getUniformLocation("offset");
        location_isSelected = getUniformLocation("isSelected");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "normal");
    }

    public void loadMvpMatrix(Matrix4f matrix) {
        loadMatrix(location_mvpMatrix, matrix);
    }

    public void loadUseFakeLighting(boolean useFake) {
        loadBoolean(location_useFakeLighting, useFake);
    }

    public void loadNumberOfRows(float rows) {
        loadFloat(location_numberOfRows, rows);
    }

    public void loadOffset(float x, float y) {
        loadVector2f(location_offset, new Vector2f(x, y));
    }

    public void loadIsSelected(boolean isSelected) {
        loadBoolean(location_isSelected, isSelected);
    }
}

