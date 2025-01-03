package adventuregame.net.chidozie.adventuregame.engine.graphics;

import adventuregame.net.chidozie.adventuregame.engine.maths.*;

public class Vertex {
    private Vector3f position, color;
    private Vector2f textureCoord;

    public Vertex(Vector3f position, Vector3f color, Vector2f textureCoord){
        this.position = position;
        this.color = color;
        this.textureCoord = textureCoord;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector2f getTextureCoord() {
        return textureCoord;
    }
}