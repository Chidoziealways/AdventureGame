package net.adventuregame.entities;

import com.chidozie.core.renderEngine.MouseInput;
import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.game.AdventureMain;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(100 ,35,30);
    private float pitch = 20;
    private float yaw;
    private float roll;

    private Player player;

    private MouseInput mInput = new MouseInput();

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        mInput.input();
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
        yaw %= 360;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticDistance;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    private void calculateZoom() {
        // Use the scroll offset from MouseInput for zoom level
        float zoomLevel = (float) mInput.getScrollOffset() * 0.1f;
        distanceFromPlayer -= zoomLevel;
        // Reset scroll offset after applying zoom
        mInput.resetScrollOffset();
    }

    private void calculatePitch() {
        if(mInput.isRightButtonPress()) {
            // Get the displacement vector's Y component for pitch change
            float pitchChange = mInput.getDisplsVec().x * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if(mInput.isLeftButtonPress()) {
            // Get the displacement vector's X component for angle change
            float angleChange = mInput.getDisplsVec().y * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    public MouseInput getmInput() {
        return mInput;
    }
}
