package net.chidozie.adventuregame;

import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Skybox implements Renderable{
    private int vaoId;
    private int vboId;
    private int eboId;
    private int textureId;

    public Skybox(String[] textureFiles) {
        float[] vertices = {
                // Vertex positions
                -1.0f,  1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f,  1.0f, -1.0f,
                -1.0f,  1.0f,  1.0f,
                -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,
        };

        int[] indices = {
                // Indices for the faces
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                0, 1, 5, 5, 4, 0,
                3, 2, 6, 6, 7, 3,
                0, 3, 7, 7, 4, 0,
                1, 2, 6, 6, 5, 1
        };

        // Initialize VAO, VBO, EBO
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(0);

        // Load textures
        textureId = loadCubeMap(textureFiles);
    }

    public void render(Matrix4f view, Matrix4f projection) {
        GL30.glBindVertexArray(vaoId);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);
        // Render the skybox...
        GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    private int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            // Load texture image
            TextureData textureData = loadTextureData(textureFiles[i]);

            if (textureData != null) {
                GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA,
                        textureData.width, textureData.height, 0, GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE, textureData.data);
            } else {
                System.err.println("Failed to load texture: " + textureFiles[i]);
            }
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);

        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);

        return texID;
    }

    public TextureData loadTextureData(String filePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer data = STBImage.stbi_load(filePath, width, height, channels, 4);
            if (data == null) {
                throw new RuntimeException("Failed to load texture: " + STBImage.stbi_failure_reason()); } return new TextureData(width.get(), height.get(), data); } }

}
