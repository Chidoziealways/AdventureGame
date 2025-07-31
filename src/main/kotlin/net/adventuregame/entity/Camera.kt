package net.adventuregame.entity

import net.adventuregame.game.AdventureMain
import net.adventuregame.player.Player
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class Camera(private val player: Player) {
    private var distanceFromPlayer = 0f // Set to 0 for FPS mode
    private var angleAroundPlayer = 0f

    val position: Vector3f
    var pitch: Float = 0f // Looking up and down
        private set
    var yaw: Float = 0f // Looking left and right
        private set
    val roll: Float = 0f

    private val window = AdventureMain.window

    var isFPSMode: Boolean = false // Start in FPS mode
        private set

    fun move() {
        if (isFPSMode) {
            updateFPSCamera()
        } else {
            calculateZoom()
            calculatePitch()
            calculateAngleAroundPlayer()
            val horizontalDistance = calculateHorizontalDistance()
            val verticalDistance = calculateVerticalDistance()
            calculateCameraPosition(horizontalDistance, verticalDistance)
            this.yaw = 180 - (player.rotY + angleAroundPlayer)
            yaw %= 360f
        }
    }

    fun invertPitch() {
        this.pitch = -pitch
    }

    private fun updateFPSCamera() {
        // Set camera position to player's head (slightly above player position)
        position.set(player.position).add(0f, 1.8f, 0f) // Adjust height for head level

        // Get mouse movement for looking around
        val dx = window.displsVec.y * 0.1f // Left/right (yaw)
        val dy = window.displsVec.x * 0.1f // Up/down (pitch)

        yaw += dx
        pitch -= dy

        // Clamp pitch to prevent looking too far up/down
        pitch = max(-90f, min(90f, pitch))
    }

    fun toggleCameraMode() {
        isFPSMode = !isFPSMode
        if (isFPSMode) {
            distanceFromPlayer = 0f
        } else {
            distanceFromPlayer = 50f // Default third-person distance
        }
    }

    private fun calculateCameraPosition(horizDistance: Float, verticDistance: Float) {
        val theta = player.rotY + angleAroundPlayer
        val offsetX = (horizDistance * sin(Math.toRadians(theta.toDouble()))).toFloat()
        val offsetZ = (horizDistance * cos(Math.toRadians(theta.toDouble()))).toFloat()
        position.x = player.position.x - offsetX
        position.z = player.position.z - offsetZ
        position.y = player.position.y + verticDistance
    }

    private fun calculateHorizontalDistance(): Float {
        return (distanceFromPlayer * cos(Math.toRadians(pitch.toDouble()))).toFloat()
    }

    private fun calculateVerticalDistance(): Float {
        return (distanceFromPlayer * sin(Math.toRadians(pitch.toDouble()))).toFloat()
    }

    private fun calculateZoom() {
        val zoomLevel = window.scrollOffset.toFloat() * 0.1f
        distanceFromPlayer -= zoomLevel

        if (distanceFromPlayer < MIN_DISTANCE_FROM_PLAYER) {
            distanceFromPlayer = MIN_DISTANCE_FROM_PLAYER
        } else if (distanceFromPlayer > MAX_DISTANCE_FROM_PLAYER) {
            distanceFromPlayer = MAX_DISTANCE_FROM_PLAYER
        }

        window.resetScrollOffset()
        updateCameraPosition()
    }

    private fun updateCameraPosition() {
        val horizontalDistance = (distanceFromPlayer * cos(Math.toRadians(pitch.toDouble()))).toFloat()
        val verticalDistance = (distanceFromPlayer * sin(Math.toRadians(pitch.toDouble()))).toFloat()

        val offsetX = (horizontalDistance * sin(Math.toRadians(angleAroundPlayer.toDouble()))).toFloat()
        val offsetZ = (horizontalDistance * cos(Math.toRadians(angleAroundPlayer.toDouble()))).toFloat()

        val cameraX = player.position.x - offsetX
        val cameraY = player.position.y + verticalDistance
        val cameraZ = player.position.z - offsetZ

        this.position.set(cameraX, cameraY, cameraZ)
    }

    init {
        this.position = Vector3f(player.position)
    }

    private fun calculatePitch() {
        val pitchChange = window.displsVec.x * 0.1f
        pitch -= pitchChange

        if (pitch < MIN_PITCH) {
            pitch = MIN_PITCH
        } else if (pitch > MAX_PITCH) {
            pitch = MAX_PITCH
        }

        updateCameraPosition()
    }

    private fun calculateAngleAroundPlayer() {
        val angleChange = window.displsVec.y * 0.3f
        angleAroundPlayer -= angleChange

        updateCameraPosition()
    }

    companion object {
        private const val MIN_DISTANCE_FROM_PLAYER = 2.0f
        private const val MAX_DISTANCE_FROM_PLAYER = 50.0f

        private const val MIN_PITCH = 5.0f
        private const val MAX_PITCH = 85.0f
    }
}
