package adventuregame.net.chidozie.adventuregame.map;

import org.lwjgl.BufferUtils;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class MapRenderer extends JPanel {

    private int tilemapTexture;
    private int tilemapWidth;
    private int tilemapHeight;
    private final int mapWidth = 10;
    private final int mapHeight = 10;
    private final int tileSize = 16;
    private int[][] tiles = {
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0}
    };
    private int vaoId;

    public MapRenderer() {
        init();
    }

    private void init() {
        try {
            tilemapTexture = loadTexture("C:/Users/uzowu/IdeaProjects/AdventureGame/src/resources/assets/tilemap.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tilemap texture", e);
        }
        createMesh();
    }

    public int getMapWidth(){
        return mapWidth;
    }
    public int getMapHeight() {
        return mapHeight;
    }
    public int getTileSize() {
        return tileSize;
    }

    private int loadTexture(String path) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Texture not found: " + path);
        }

        BufferedImage image = javax.imageio.ImageIO.read(inputStream);
        tilemapWidth = image.getWidth();
        tilemapHeight = image.getHeight();

        int[] pixels = new int[tilemapWidth * tilemapHeight];
        image.getRGB(0, 0, tilemapWidth, tilemapHeight, pixels, 0, tilemapWidth);

        ByteBuffer buffer = BufferUtils.createByteBuffer(tilemapWidth * tilemapHeight * 4);
        for (int y = 0; y < tilemapHeight; y++) {
            for (int x = 0; x < tilemapWidth; x++) {
                int pixel = pixels[y * tilemapWidth + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
            }
        }
        buffer.flip();

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, tilemapWidth, tilemapHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glBindTexture(GL_TEXTURE_2D, 0);

        return textureID;
    }

    private void createMesh() {
        float[] vertices = new float[mapWidth * mapHeight * 18]; // 6 vertices per quad, 3 coordinates per vertex
        float[] texCoords = new float[mapWidth * mapHeight * 12]; // 6 vertices per quad, 2 coordinates per vertex
        int vertexIndex = 0;
        int texCoordIndex = 0;

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileType = tiles[y][x];

                int tileX = tileType % (tilemapWidth / tileSize);
                int tileY = tileType / (tilemapWidth / tileSize);

                float u1 = (float) (tileX * tileSize) / tilemapWidth;
                float v1 = (float) (tileY * tileSize) / tilemapHeight;
                float u2 = (float) ((tileX + 1) * tileSize) / tilemapWidth;
                float v2 = (float) ((tileY + 1) * tileSize) / tilemapHeight;

                float xPos = x * tileSize;
                float yPos = y * tileSize;
                float zPos = 0.0f; // flat on the z plane

                // Define vertices for one quad (two triangles)
                vertices[vertexIndex++] = xPos; vertices[vertexIndex++] = yPos; vertices[vertexIndex++] = zPos;
                vertices[vertexIndex++] = xPos + tileSize; vertices[vertexIndex++] = yPos; vertices[vertexIndex++] = zPos;
                vertices[vertexIndex++] = xPos + tileSize; vertices[vertexIndex++] = yPos + tileSize; vertices[vertexIndex++] = zPos;
                vertices[vertexIndex++] = xPos; vertices[vertexIndex++] = yPos; vertices[vertexIndex++] = zPos;
                vertices[vertexIndex++] = xPos + tileSize; vertices[vertexIndex++] = yPos + tileSize; vertices[vertexIndex++] = zPos;
                vertices[vertexIndex++] = xPos; vertices[vertexIndex++] = yPos + tileSize; vertices[vertexIndex++] = zPos;

                // Texture coordinates for the same quad
                texCoords[texCoordIndex++] = u1; texCoords[texCoordIndex++] = v1;
                texCoords[texCoordIndex++] = u2; texCoords[texCoordIndex++] = v1;
                texCoords[texCoordIndex++] = u2; texCoords[texCoordIndex++] = v2;
                texCoords[texCoordIndex++] = u1; texCoords[texCoordIndex++] = v1;
                texCoords[texCoordIndex++] = u2; texCoords[texCoordIndex++] = v2;
                texCoords[texCoordIndex++] = u1; texCoords[texCoordIndex++] = v2;
            }
        }

        int vertexVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVbo);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        int texCoordVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texCoordVbo);
        FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.length);
        texCoordBuffer.put(texCoords).flip();
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_STATIC_DRAW);

        // Bind VAO
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Bind and set the vertex data
        glBindBuffer(GL_ARRAY_BUFFER, vertexVbo);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        // Bind and set the texture coordinate data
        glBindBuffer(GL_ARRAY_BUFFER, texCoordVbo);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        // Unbind VAO
        glBindVertexArray(0);
    }
    public void render() {
        // Bind the VAO
        glBindVertexArray(vaoId);

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, tilemapTexture);

        // Draw the tiles
        glDrawArrays(GL_TRIANGLES, 0, mapWidth * mapHeight * 6); // 6 vertices per tile

        // Unbind the texture and VAO
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
    }

}
