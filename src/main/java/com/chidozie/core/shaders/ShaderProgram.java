package com.chidozie.core.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public abstract class ShaderProgram {

    private static final Logger log = LoggerFactory.getLogger(ShaderProgram.class);
    private int programId, vertexShaderId, geometryShaderId, fragmentShaderId;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderId = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        bindAttributes();
        glLinkProgram(programId);
        glValidateProgram(programId);
        getAllUniformLocations();
    }

    public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile) {
        vertexShaderId = loadShader(vertexFile, GL_VERTEX_SHADER);
        geometryShaderId = loadShader(geometryFile, GL_GEOMETRY_SHADER);
        fragmentShaderId = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, geometryShaderId);
        glAttachShader(programId, fragmentShaderId);
        bindAttributes();
        glLinkProgram(programId);
        glValidateProgram(programId);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programId, uniformName);
    }

    public void start() {
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void cleanup() {
        stop();
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, geometryShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(geometryShaderId);
        glDeleteShader(fragmentShaderId);
        glDeleteProgram(programId);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programId, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    protected void loadVector4f(int location, Vector4f vector4f) {
        glUniform4f(location, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    protected void loadVector3f(int location, Vector3f vector3f) {
        glUniform3f(location, vector3f.x, vector3f.y, vector3f.z);
    }

    protected void loadVector2f(int location, Vector2f vector2f) {
        glUniform2f(location, vector2f.x, vector2f.y);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            matrix.get(matrixBuffer);
            glUniformMatrix4fv(location, false, matrixBuffer);
        }
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            InputStream inputStream = ShaderProgram.class.getClassLoader().getResourceAsStream("assets/adventuregame/shaders/" + file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            log.error("Could not Read File");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            log.error(glGetShaderInfoLog(shaderId, 500));
            log.error("Could not Compile Shader." + shaderId);
            System.exit(-1);
        }
        return shaderId;

    }

}
