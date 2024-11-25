package adventuregame.net.chidozie.adventuregame;

import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;

public class Triangle implements Renderable {
    @Override
    public void render(Matrix4f mvp) {
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3f(-0.5f, -0.5f, 0);
        GL11.glVertex3f(0.5f, -0.5f, 0);
        GL11.glVertex3f(0.0f, 0.5f, 0);
        GL11.glEnd();
    }
}
