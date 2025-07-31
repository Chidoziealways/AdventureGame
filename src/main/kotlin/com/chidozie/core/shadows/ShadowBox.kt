package com.chidozie.core.shadows

import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.entity.Camera
import net.adventuregame.game.AdventureMain
import net.adventuregame.toolbox.Consts
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.tan

/**
 * Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be.
 *
 * @author Karl
 */
class ShadowBox(private val lightViewMatrix: Matrix4f, private val cam: Camera) {
    private var minX = 0f
    private var maxX = 0f
    private var minY = 0f
    private var maxY = 0f
    private var minZ = 0f
    private var maxZ = 0f
    private val window: WindowManager

    private var farHeight = 0f
    private var farWidth = 0f
    private var nearHeight = 0f
    private var nearWidth = 0f

    /**
     * Creates a new shadow box and calculates some initial values relating to
     * the camera's view frustum, namely the width and height of the near plane
     * and (possibly adjusted) far plane.
     *
     * @param lightViewMatrix
     * - basically the "view matrix" of the light. Can be used to
     * transform a point from world space into "light" space (i.e.
     * changes a point's coordinates from being in relation to the
     * world's axis to being in terms of the light's local axis).
     * @param cam
     * - the in-game camera.
     */
    init {
        window = AdventureMain.window
        calculateWidthsAndHeights()
    }

    /**
     * Updates the bounds of the shadow box based on the light direction and the
     * camera's view frustum, to make sure that the box covers the smallest area
     * possible while still ensuring that everything inside the camera's view
     * (within a certain range) will cast shadows.
     */
    fun update() {
        val rotation = calculateCameraRotationMatrix()
        val forwardVector = Vector3f()
        rotation.transformDirection(Vector3f(FORWARD.x, FORWARD.y, FORWARD.z), forwardVector)

        val toFar: Vector3f = Vector3f(forwardVector).mul(SHADOW_DISTANCE)
        val toNear = Vector3f(forwardVector).mul(Consts.NEAR_PLANE)
        val centerNear = Vector3f(toNear).add(cam.position)
        val centerFar = Vector3f(toFar).add(cam.position)

        val points = calculateFrustumVertices(rotation, forwardVector, centerNear, centerFar)

        var first = true
        for (point in points) {
            if (first) {
                minX = point.x
                maxX = point.x
                minY = point.y
                maxY = point.y
                minZ = point.z
                maxZ = point.z
                first = false
                continue
            }
            if (point.x > maxX) {
                maxX = point.x
            } else if (point.x < minX) {
                minX = point.x
            }
            if (point.y > maxY) {
                maxY = point.y
            } else if (point.y < minY) {
                minY = point.y
            }
            if (point.z > maxZ) {
                maxZ = point.z
            } else if (point.z < minZ) {
                minZ = point.z
            }
        }
        maxZ += OFFSET
    }


    val center: Vector3f
        /**
         * Calculates the center of the "view cuboid" in light space first, and then
         * converts this to world space using the inverse light's view matrix.
         *
         * @return The center of the "view cuboid" in world space.
         */
        get() {
            val x = (minX + maxX) / 2f
            val y = (minY + maxY) / 2f
            val z = (minZ + maxZ) / 2f
            val cen = Vector4f(x, y, z, 1f)
            val invertedLight = Matrix4f()
            lightViewMatrix.invert(invertedLight) // Invert the light view matrix
            val transformedCenter = invertedLight.transform(cen) // Transform the center
            return Vector3f(transformedCenter.x, transformedCenter.y, transformedCenter.z) // Convert to Vector3f
        }

    val width: Float
        /**
         * @return The width of the "view cuboid" (orthographic projection area).
         */
        get() = maxX - minX

    val height: Float
        /**
         * @return The height of the "view cuboid" (orthographic projection area).
         */
        get() = maxY - minY

    val length: Float
        /**
         * @return The length of the "view cuboid" (orthographic projection area).
         */
        get() = maxZ - minZ

    /**
     * Calculates the position of the vertex at each corner of the view frustum
     * in light space (8 vertices in total, so this returns 8 positions).
     *
     * @param rotation
     * - camera's rotation.
     * @param forwardVector
     * - the direction that the camera is aiming, and thus the
     * direction of the frustum.
     * @param centerNear
     * - the center point of the frustum's near plane.
     * @param centerFar
     * - the center point of the frustum's (possibly adjusted) far
     * plane.
     * @return The positions of the vertices of the frustum in light space.
     */
    private fun calculateFrustumVertices(
        rotation: Matrix4f, forwardVector: Vector3f,
        centerNear: Vector3f, centerFar: Vector3f
    ): Array<Vector4f> {
        // Convert UP from Vector4f to Vector3f

        val upVector = Vector3f(UP.x, UP.y, UP.z)
        rotation.transformDirection(upVector)

        val rightVector = forwardVector.cross(upVector, Vector3f())
        val downVector = Vector3f(-upVector.x, -upVector.y, -upVector.z)
        val leftVector = Vector3f(-rightVector.x, -rightVector.y, -rightVector.z)

        val farTop = Vector3f(centerFar).add(upVector.mul(farHeight, Vector3f()))
        val farBottom = Vector3f(centerFar).add(downVector.mul(farHeight, Vector3f()))
        val nearTop = Vector3f(centerNear).add(upVector.mul(nearHeight, Vector3f()))
        val nearBottom = Vector3f(centerNear).add(downVector.mul(nearHeight, Vector3f()))

        val points: Array<Vector4f> = arrayOfNulls<Vector4f>(8) as Array<Vector4f>
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth)
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth)
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth)
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth)
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth)
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth)
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth)
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth)

        return points
    }

    /**
     * Calculates one of the corner vertices of the view frustum in world space
     * and converts it to light space.
     *
     * @param startPoint
     * - the starting center point on the view frustum.
     * @param direction
     * - the direction of the corner from the start point.
     * @param width
     * - the distance of the corner from the start point.
     * @return - The relevant corner vertex of the view frustum in light space.
     */
    private fun calculateLightSpaceFrustumCorner(startPoint: Vector3f, direction: Vector3f, width: Float): Vector4f {
        val point = Vector3f(startPoint).add(direction.mul(width, Vector3f()))
        val point4f = Vector4f(point, 1f)
        lightViewMatrix.transform(point4f)
        return point4f
    }

    /**
     * @return The rotation of the camera represented as a matrix.
     */
    private fun calculateCameraRotationMatrix(): Matrix4f {
        val rotation = Matrix4f()
        rotation.rotate(Math.toRadians(-cam.yaw.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
        rotation.rotate(Math.toRadians(-cam.pitch.toDouble()).toFloat(), Vector3f(1f, 0f, 0f))
        return rotation
    }

    /**
     * Calculates the width and height of the near and far planes of the
     * camera's view frustum. However, this doesn't have to use the "actual" far
     * plane of the view frustum. It can use a shortened view frustum if desired
     * by bringing the far-plane closer, which would increase shadow resolution
     * but means that distant objects wouldn't cast shadows.
     */
    private fun calculateWidthsAndHeights() {
        farWidth = (SHADOW_DISTANCE * tan(Math.toRadians(Consts.FOV.toDouble()))).toFloat()
        nearWidth = (Consts.NEAR_PLANE
                * tan(Math.toRadians(Consts.FOV.toDouble()))).toFloat()
        farHeight = farWidth / this.aspectRatio
        nearHeight = nearWidth / this.aspectRatio
    }

    private val aspectRatio: Float
        /**
         * @return The aspect ratio of the display (width:height ratio).
         */
        get() = window.width.toFloat() / window.height.toFloat()

    companion object {
        private const val OFFSET = 30f
        private val UP = Vector4f(0f, 1f, 0f, 0f)
        private val FORWARD = Vector4f(0f, 0f, -1f, 0f)
        private const val SHADOW_DISTANCE = 150f
    }
}
