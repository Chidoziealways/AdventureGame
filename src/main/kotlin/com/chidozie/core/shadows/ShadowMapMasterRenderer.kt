package com.chidozie.core.shadows

import net.adventuregame.entity.Camera
import net.adventuregame.entity.Entity
import net.adventuregame.entity.Light
import net.adventuregame.models.TexturedModel
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import kotlin.math.acos
import kotlin.math.atan

/**
 * This class is in charge of using all of the classes in the shadows package to
 * carry out the shadow render pass, i.e. rendering the scene to the shadow map
 * texture. This is the only class in the shadows package which needs to be
 * referenced from outside the shadows package.
 *
 * @author Karl
 */
class ShadowMapMasterRenderer(camera: Camera?) {
    private val shadowFbo: ShadowFrameBuffer
    private val shader: ShadowShader
    private val shadowBox: ShadowBox
    private val projectionMatrix = Matrix4f()

    /**
     * @return The light's "view" matrix.
     */
    protected val lightSpaceTransform: Matrix4f = Matrix4f()
    private val projectionViewMatrix = Matrix4f()
    private val offset: Matrix4f = createOffset()

    private val entityRenderer: ShadowMapEntityRenderer

    /**
     * Creates instances of the important objects needed for rendering the scene
     * to the shadow map. This includes the [ShadowBox] which calculates
     * the position and size of the "view cuboid", the simple renderer and
     * shader program that are used to render objects to the shadow map, and the
     * [ShadowFrameBuffer] to which the scene is rendered. The size of the
     * shadow map is determined here.
     *
     * @param camera
     * - the camera being used in the scene.
     */
    init {
        shader = ShadowShader()
        shadowBox = ShadowBox(this.lightSpaceTransform, camera!!)
        shadowFbo = ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE)
        entityRenderer = ShadowMapEntityRenderer(shader, projectionViewMatrix)
    }

    /**
     * Carries out the shadow render pass. This renders the entities to the
     * shadow map. First the shadow box is updated to calculate the size and
     * position of the "view cuboid". The light direction is assumed to be
     * "-lightPosition" which will be fairly accurate assuming that the light is
     * very far from the scene. It then prepares to render, renders the entities
     * to the shadow map, and finishes rendering.
     *
     * @param entities
     * - the lists of entities to be rendered. Each list is
     * associated with the [TexturedModel] that all of the
     * entities in that list use.
     * @param sun
     * - the light acting as the sun in the scene.
     */
    fun render(entities: MutableMap<TexturedModel, MutableList<Entity>>, sun: Light) {
        shadowBox.update()
        val sunPosition = sun.position
        val lightDirection = Vector3f(-sunPosition!!.x, -sunPosition.y, -sunPosition.z)
        prepare(lightDirection, shadowBox)
        entityRenderer.render(entities)
        finish()
    }

    val toShadowMapSpaceMatrix: Matrix4f?
        /**
         * This biased projection-view matrix is used to convert fragments into
         * "shadow map space" when rendering the main render pass. It converts a
         * world space position into a 2D coordinate on the shadow map. This is
         * needed for the second part of shadow mapping.
         *
         * @return The to-shadow-map-space matrix.
         */
        get() = Matrix4f(offset).mul(projectionViewMatrix)

    /**
     * Clean up the shader and FBO on closing.
     */
    fun cleanUp() {
        shader.cleanup()
        shadowFbo.cleanUp()
    }

    val shadowMap: Int
        /**
         * @return The ID of the shadow map texture. The ID will always stay the
         * same, even when the contents of the shadow map texture change
         * each frame.
         */
        get() = shadowFbo.shadowMap

    /**
     * Prepare for the shadow render pass. This first updates the dimensions of
     * the orthographic "view cuboid" based on the information that was
     * calculated in the [ShadowBox] class. The light's "view" matrix is
     * also calculated based on the light's direction and the center position of
     * the "view cuboid" which was also calculated in the [ShadowBox]
     * class. These two matrices are multiplied together to create the
     * projection-view matrix. This matrix determines the size, position, and
     * orientation of the "view cuboid" in the world. This method also binds the
     * shadows FBO so that everything rendered after this gets rendered to the
     * FBO. It also enables depth testing, and clears any data that is in the
     * FBOs depth attachment from last frame. The simple shader program is also
     * started.
     *
     * @param lightDirection
     * - the direction of the light rays coming from the sun.
     * @param box
     * - the shadow box, which contains all the info about the
     * "view cuboid".
     */
    private fun prepare(lightDirection: Vector3f, box: ShadowBox) {
        updateOrthoProjectionMatrix(box.width, box.height, box.length)
        updateLightViewMatrix(lightDirection, box.center)

        // Perform matrix multiplication using JOML
        projectionViewMatrix.set(projectionMatrix).mul(this.lightSpaceTransform)

        shadowFbo.bindFrameBuffer()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        shader.start()
    }

    /**
     * Finish the shadow render pass. Stops the shader and unbinds the shadow
     * FBO, so everything rendered after this point is rendered to the screen,
     * rather than to the shadow FBO.
     */
    private fun finish() {
        shader.stop()
        shadowFbo.unbindFrameBuffer()
    }

    /**
     * Updates the "view" matrix of the light. This creates a view matrix which
     * will line up the direction of the "view cuboid" with the direction of the
     * light. The light itself has no position, so the "view" matrix is centered
     * at the center of the "view cuboid". The created view matrix determines
     * where and how the "view cuboid" is positioned in the world. The size of
     * the view cuboid, however, is determined by the projection matrix.
     *
     * @param direction
     * - the light direction, and therefore the direction that the
     * "view cuboid" should be pointing.
     * @param center
     * - the center of the "view cuboid" in world space.
     */
    private fun updateLightViewMatrix(direction: Vector3f, center: Vector3f) {
        direction.normalize()
        center.negate()
        lightSpaceTransform.identity()

        val pitch = acos(Vector2f(direction.x, direction.z).length().toDouble()).toFloat()
        lightSpaceTransform.rotate(pitch, Vector3f(1f, 0f, 0f))

        var yaw = Math.toDegrees((atan((direction.x / direction.z).toDouble()).toFloat()).toDouble()).toFloat()
        yaw = if (direction.z > 0) yaw - 180 else yaw
        lightSpaceTransform.rotate(-Math.toRadians(yaw.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))

        lightSpaceTransform.translate(center)
    }

    /**
     * Creates the orthographic projection matrix. This projection matrix
     * basically sets the width, length and height of the "view cuboid", based
     * on the values that were calculated in the [ShadowBox] class.
     *
     * @param width
     * - shadow box width.
     * @param height
     * - shadow box height.
     * @param length
     * - shadow box length.
     */
    private fun updateOrthoProjectionMatrix(width: Float, height: Float, length: Float) {
        projectionMatrix.identity()
        projectionMatrix.m00(2f / width)
        projectionMatrix.m11(2f / height)
        projectionMatrix.m22(-2f / length)
        projectionMatrix.m33(1f)
    }

    companion object {
        const val SHADOW_MAP_SIZE: Int = 4096

        /**
         * Create the offset for part of the conversion to shadow map space. This
         * conversion is necessary to convert from one coordinate system to the
         * coordinate system that we can use to sample to shadow map.
         *
         * @return The offset as a matrix (so that it's easy to apply to other matrices).
         */
        private fun createOffset(): Matrix4f {
            val offset = Matrix4f()
            offset.translate(Vector3f(0.5f, 0.5f, 0.5f))
            offset.scale(Vector3f(0.5f, 0.5f, 0.5f))
            return offset
        }
    }
}
