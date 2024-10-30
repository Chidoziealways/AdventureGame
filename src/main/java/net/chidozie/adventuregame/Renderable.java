package net.chidozie.adventuregame;

import org.joml.Matrix4f;

public interface Renderable {
    void render(Matrix4f viewMatrix, Matrix4f projectionMatrix);
}
