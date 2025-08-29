package com.adv.core.shadows

import com.adv.core.renderEngine.WindowManager
import net.adventuregame.game.AdventureMain
import org.lwjgl.opengl.*
import java.nio.ByteBuffer

/**
 * The frame buffer for the shadow pass. This class sets up the depth texture
 * which can be rendered to during the shadow render pass, producing a shadow
 * map.
 *
 * @author Karl
 */
class ShadowFrameBuffer(private val WIDTH: Int, private val HEIGHT: Int) {
    private var fbo = 0

    /**
     * @return The ID of the shadow map texture.
     */
    var shadowMap: Int = 0
        private set
    private val window: WindowManager

    /**
     * Initialises the frame buffer and shadow map of a certain size.
     *
     * @param WIDTH
     * - the width of the shadow map in pixels.
     * @param HEIGHT
     * - the height of the shadow map in pixels.
     */
    init {
        window = AdventureMain.window
        initialiseFrameBuffer()
    }

    /**
     * Deletes the frame buffer and shadow map texture when the game closes.
     */
    fun cleanUp() {
        GL30.glDeleteFramebuffers(fbo)
        GL11.glDeleteTextures(shadowMap)
    }

    /**
     * Binds the frame buffer, setting it as the current render target.
     */
    fun bindFrameBuffer() {
        bindFrameBuffer(fbo, WIDTH, HEIGHT)
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target.
     */
    fun unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glViewport(0, 0, window.width, window.height)
    }

    /**
     * Creates the frame buffer and adds its depth attachment texture.
     */
    private fun initialiseFrameBuffer() {
        fbo = createFrameBuffer()
        shadowMap = createDepthBufferAttachment(WIDTH, HEIGHT)
        unbindFrameBuffer()
    }

    companion object {
        /**
         * Binds the frame buffer as the current render target.
         *
         * @param frameBuffer
         * - the frame buffer.
         * @param width
         * - the width of the frame buffer.
         * @param height
         * - the height of the frame buffer.
         */
        private fun bindFrameBuffer(frameBuffer: Int, width: Int, height: Int) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer)
            GL11.glViewport(0, 0, width, height)
        }

        /**
         * Creates a frame buffer and binds it so that attachments can be added to
         * it. The draw buffer is set to none, indicating that there's no colour
         * buffer to be rendered to.
         *
         * @return The newly created frame buffer's ID.
         */
        private fun createFrameBuffer(): Int {
            val frameBuffer = GL30.glGenFramebuffers()
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
            GL11.glDrawBuffer(GL11.GL_NONE)
            GL11.glReadBuffer(GL11.GL_NONE)
            return frameBuffer
        }

        /**
         * Creates a depth buffer texture attachment.
         *
         * @param width
         * - the width of the texture.
         * @param height
         * - the height of the texture.
         * @return The ID of the depth texture.
         */
        private fun createDepthBufferAttachment(width: Int, height: Int): Int {
            val texture = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0,
                GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null as ByteBuffer?
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0)
            return texture
        }
    }
}
