package adventuregame.net.chidozie.adventuregame;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Player implements Serializable, Renderable {
    private String username;
    private String gender;
    private double level;
    private double health;
    private Vector3f position;
    private Vector3f[] vertices;
    private int[] indices;
    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private static final long serialVersionUID = -6470090944414208496L;

    public Player(String username, String gender, double level, double health, Vector3f position, Vector3f[] vertices, int[] indices) {
        this.username = username;
        this.gender = gender;
        this.level = level;
        this.health = health;
        this.position = position;
        this.vertices = vertices;
        this.indices = indices;
        initializeBuffers();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
        initializeBuffers();
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
        initializeBuffers();
    }

    private void initializeBuffers() {
        if (vertices == null || indices == null) return;
        vertexBuffer = BufferUtils.createFloatBuffer(vertices.length * 3);
        for (Vector3f vertex : vertices) {
            vertexBuffer.put(vertex.x).put(vertex.y).put(vertex.z);
        }
        vertexBuffer.flip();
        indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices);
        indexBuffer.flip();
    }

    public void move(Vector3f delta) {
        position.add(delta);
    }

    @Override
    public void render(Matrix4f mvp) {
        Matrix4f model = new Matrix4f().translate(position);
        Matrix4f finalMVP = new Matrix4f(mvp).mul(model);

        GL11.glLoadMatrixf(finalMVP.get(new float[16]));

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer);

        GL11.glDrawElements(GL11.GL_TRIANGLES, indexBuffer);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
    }
}
