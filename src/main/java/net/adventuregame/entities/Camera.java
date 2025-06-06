package net.adventuregame.entities;

import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.player.Player;
import org.joml.Vector3f;

public class Camera {
    private float distanceFromPlayer = 0; // Set to 0 for FPS mode
    private float angleAroundPlayer = 0;

    private Vector3f position;
    private float pitch = 0; // Looking up and down
    private float yaw = 0; // Looking left and right
    private float roll;

    private WindowManager window = AdventureMain.getWindow();

    private final Player player;

    private boolean isFPSMode = false; // Start in FPS mode

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f(player.getPosition());
    }

    public void move() {
        if (isFPSMode) {
            updateFPSCamera();
        } else {
            calculateZoom();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(horizontalDistance, verticalDistance);
            this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
            yaw %= 360;
        }
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

    private void updateFPSCamera() {
        // Set camera position to player's head (slightly above player position)
        position.set(player.getPosition()).add(0, 1.8f, 0); // Adjust height for head level

        // Get mouse movement for looking around
        float dx = window.getDisplsVec().y * 0.1f; // Left/right (yaw)
        float dy = window.getDisplsVec().x * 0.1f; // Up/down (pitch)

        yaw += dx;
        pitch -= dy;

        // Clamp pitch to prevent looking too far up/down
        pitch = Math.max(-90, Math.min(90, pitch));
    }

    public void toggleCameraMode() {
        isFPSMode = !isFPSMode;
        if (isFPSMode) {
            distanceFromPlayer = 0;
        } else {
            distanceFromPlayer = 50; // Default third-person distance
        }
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

    private static final float MIN_DISTANCE_FROM_PLAYER = 2.0f;
    private static final float MAX_DISTANCE_FROM_PLAYER = 50.0f;

    private void calculateZoom() {
        float zoomLevel = (float) window.getScrollOffset() * 0.1f;
        distanceFromPlayer -= zoomLevel;

        if (distanceFromPlayer < MIN_DISTANCE_FROM_PLAYER) {
            distanceFromPlayer = MIN_DISTANCE_FROM_PLAYER;
        } else if (distanceFromPlayer > MAX_DISTANCE_FROM_PLAYER) {
            distanceFromPlayer = MAX_DISTANCE_FROM_PLAYER;
        }

        window.resetScrollOffset();
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
        float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));

        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(angleAroundPlayer)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(angleAroundPlayer)));

        float cameraX = player.getPosition().x - offsetX;
        float cameraY = player.getPosition().y + verticalDistance;
        float cameraZ = player.getPosition().z - offsetZ;

        this.position.set(cameraX, cameraY, cameraZ);
    }

    private static final float MIN_PITCH = 5.0f;
    private static final float MAX_PITCH = 85.0f;

    private void calculatePitch() {
        float pitchChange = window.getDisplsVec().x * 0.1f;
        pitch -= pitchChange;

        if (pitch < MIN_PITCH) {
            pitch = MIN_PITCH;
        } else if (pitch > MAX_PITCH) {
            pitch = MAX_PITCH;
        }

        updateCameraPosition();
    }

    private void calculateAngleAroundPlayer() {
        float angleChange = window.getDisplsVec().y * 0.3f;
        angleAroundPlayer -= angleChange;

        updateCameraPosition();
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

    public boolean isFPSMode() {
        return isFPSMode;
    }
}
