package adventuregame.net.chidozie.adventuregame;

import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;

public class PlayerModel implements Renderable {
    private float[] vertices;
    private float[] colors;

    public PlayerModel() {
        // Define vertices for a simple player adventuregame.model (e.g., a cube)
        vertices = new float[]{
                // Front face
                -0.5f, 0.5f, 0.5f,   // Top-left
                -0.5f, -0.5f, 0.5f,  // Bottom-left
                0.5f, -0.5f, 0.5f,   // Bottom-right
                0.5f, 0.5f, 0.5f,    // Top-right
                // Back face
                -0.5f, 0.5f, -0.5f,  // Top-left
                -0.5f, -0.5f, -0.5f, // Bottom-left
                0.5f, -0.5f, -0.5f,  // Bottom-right
                0.5f, 0.5f, -0.5f    // Top-right
        };

        colors = new float[]{
                // Colors for each vertex
                1.0f, 0.0f, 0.0f,  // Red
                0.0f, 1.0f, 0.0f,  // Green
                0.0f, 0.0f, 1.0f,  // Blue
                1.0f, 1.0f, 0.0f,  // Yellow
                1.0f, 0.0f, 1.0f,  // Magenta
                0.0f, 1.0f, 1.0f,  // Cyan
                1.0f, 1.0f, 1.0f,  // White
                0.5f, 0.5f, 0.5f   // Gray
        };
    }

    @Override
    public void render(Matrix4f mvp) {
        GL11.glBegin(GL11.GL_QUADS); // Using GL_QUADS for the cube
        for (int i = 0; i < vertices.length / 3; i++) {
            GL11.glColor3f(colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2]);
            GL11.glVertex3f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
        }
        GL11.glEnd();
    }
}
