package net.adventuregame.entities;

import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.models.TexturedModel;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

public class Player extends Entity {

    private WindowManager window = AdventureMain.getWindow();
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    public static final float GRAVITY = -90;
    private static final float JUMP_POWER = 30;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
                  float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain) {
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * window.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * window.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * window.getFrameTimeSeconds();
        super.increasePosition(0, upwardsSpeed * window.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs() {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
            if (window.isKeyPressed(GLFW_KEY_W) && window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                this.currentSpeed = RUN_SPEED * 5;
            }
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        }else {
            this.currentSpeed = 0;
        }

        if (window.isKeyPressed(GLFW_KEY_D) || window.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_A) || window.isKeyPressed(GLFW_KEY_LEFT)) {
            this.currentTurnSpeed = TURN_SPEED;
        }else {
            this.currentTurnSpeed = 0;
        }

        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            jump();
        }

    }


}
