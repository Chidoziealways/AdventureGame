package com.chidozie.core.shaders;

import net.adventuregame.entities.Camera;
import net.adventuregame.entities.Light;
import net.adventuregame.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class TerrainShader extends ShaderProgram{

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "terrains/terrainVertexShader.vert";
    private static final String FRAGMENT_FILE = "terrains/terrainFragmentShader.frag";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_attenuation[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColour;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_plane;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_mapSize;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_plane = super.getUniformLocation("plane");
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
        location_mapSize = super.getUniformLocation("mapSize");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
        super.loadInt(location_shadowMap, 5);
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

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector3f(location_skyColour, new Vector3f(r, g ,b));
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
