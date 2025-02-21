package com.chidozie.core.shaders;

import net.adventuregame.entities.Camera;
import net.adventuregame.entities.Light;
import net.adventuregame.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StaticShader extends ShaderProgram{

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "entities/vertexShader.vert";
    private static final String FRAGMENT_FILE = "entities/fragmentShader.frag";
    private static final Logger log = LoggerFactory.getLogger(StaticShader.class);

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_attenuation[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;
    private int location_plane;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_mapSize;
    private int location_specularMap;
    private int location_usesSpecularMap;
    private int location_modelTexture;
    private int location_isSelected;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_specularMap = super.getUniformLocation("specularMap");
        location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
        location_modelTexture = super.getUniformLocation("modelTexture");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_plane = super.getUniformLocation("plane");
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
        location_mapSize = super.getUniformLocation("mapSize");
        location_isSelected = getUniformLocation("isSelected");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void connectTextureUnits() {
        loadInt(location_shadowMap, 5);
        loadInt(location_modelTexture, 0);
        loadInt(location_specularMap, 1);
    }

    public void loadIsSelected(boolean isSelected) {
        loadBoolean(location_isSelected, isSelected);
    }

    public void loadUseSpecularMap(boolean useMap) {
        loadBoolean(location_usesSpecularMap, useMap);
    }

    public void loadMapSize(float size) {
        loadFloat(location_mapSize, size);
    }

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        loadMatrix(location_toShadowMapSpace, matrix);
    }


    public void loadClipPlane(Vector4f plane) {
        super.loadVector4f(location_plane, plane);
    }

    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y) {
        super.loadVector2f(location_offset, new Vector2f(x, y));
    }

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector3f(location_skyColour, new Vector3f(r, g ,b));
    }

    public void loadFakeLightingVariable(boolean useFake) {
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix4f);
    }

    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
                super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
                super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
            }else {
                super.loadVector3f(location_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_lightColour[i], new Vector3f(0, 0, 0));
                super.loadVector3f(location_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
