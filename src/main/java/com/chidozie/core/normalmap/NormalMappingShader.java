package com.chidozie.core.normalmap;

import com.chidozie.core.shaders.ShaderProgram;
import net.adventuregame.entities.Light;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;


public class NormalMappingShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "normalmap/normalMapVertexShader.vert";
	private static final String FRAGMENT_FILE = "normalmap/normalMapFragmentShader.frag";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPositionEyeSpace[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_modelTexture;
	private int location_normalMap;
	private int location_specularMap;
	private int location_usesSpecularMap;

	public NormalMappingShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_specularMap = super.getUniformLocation("specularMap");
		location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_modelTexture = super.getUniformLocation("modelTexture");
		location_normalMap = super.getUniformLocation("normalMap");
		
		location_lightPositionEyeSpace = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0;i<MAX_LIGHTS;i++){
			location_lightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_normalMap, 1);
		loadInt(location_specularMap, 2);
	}

	public void loadUseSpecularMap(boolean useMap) {
		loadBoolean(location_usesSpecularMap, useMap);
	}
	
	protected void loadClipPlane(Vector4f plane){
		super.loadVector4f(location_plane, plane);
	}
	
	protected void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	protected void loadOffset(float x, float y){
		super.loadVector2f(location_offset, new Vector2f(x,y));
	}
	
	protected void loadSkyColour(float r, float g, float b){
		super.loadVector3f(location_skyColour, new Vector3f(r,g,b));
	}
	
	protected void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	protected void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	protected void loadLights(List<Light> lights, Matrix4f viewMatrix){
		for(int i=0;i<MAX_LIGHTS;i++){
			if(i<lights.size()){
				super.loadVector3f(location_lightPositionEyeSpace[i], getEyeSpacePosition(lights.get(i), viewMatrix));
				super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
				super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
			}else{
				super.loadVector3f(location_lightPositionEyeSpace[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector3f(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	protected void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	protected void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}

	private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix) {
		Vector3f position = light.getPosition();
		Vector4f eyeSpacePos = new Vector4f(position, 1.0f);
		viewMatrix.transform(eyeSpacePos);
		return new Vector3f(eyeSpacePos.x, eyeSpacePos.y, eyeSpacePos.z);
	}
	
	

}
