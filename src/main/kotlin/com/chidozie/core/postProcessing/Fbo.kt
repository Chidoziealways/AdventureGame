package com.chidozie.core.postProcessing

import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.game.AdventureMain
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import java.nio.ByteBuffer

class Fbo {
    private val width: Int
    private val height: Int

    private var frameBuffer = 0

    private var multisampleAndMultiTarget = false

    /**
     * @return The ID of the texture containing the colour buffer of the FBO.
     */
    var colourTexture: Int = 0
        private set

    /**
     * @return The texture containing the FBOs depth buffer.
     */
    var depthTexture: Int = 0
        private set

    private var depthBuffer = 0
    private var colourBuffer = 0
    private var colourBuffer2 = 0

    private val window: WindowManager

    /**
     * Creates an FBO of a specified width and height, with the desired type of
     * depth buffer attachment.
     *
     * @param width
     * - the width of the FBO.
     * @param height
     * - the height of the FBO.
     * @param depthBufferType
     * - an int indicating the type of depth buffer attachment that
     * this FBO should use.
     */
    constructor(width: Int, height: Int, depthBufferType: Int) {
        this.width = width
        this.height = height
        window = AdventureMain.window
        initialiseFrameBuffer(depthBufferType)
    }

    /**
     * Creates a MultiSample FBO with the specified width and height with a Constant Depth Buffer Attachment
     * of DEPTH_RENDER_BUFFER
     * @param width -- width of the FBO
     * @param height -- height of the FBO
     */
    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        this.multisampleAndMultiTarget = true
        window = AdventureMain.window
        initialiseFrameBuffer(DEPTH_RENDER_BUFFER)
    }

    /**
     * Deletes the frame buffer and its attachments when the game closes.
     */
    fun cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer)
        GL11.glDeleteTextures(colourTexture)
        GL11.glDeleteTextures(depthTexture)
        GL30.glDeleteRenderbuffers(depthBuffer)
        GL30.glDeleteRenderbuffers(colourBuffer)
        GL30.glDeleteRenderbuffers(colourBuffer2)
    }

    /**
     * Binds the frame buffer, setting it as the current render target. Anything
     * rendered after this will be rendered to this FBO, and not to the screen.
     */
    fun bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer)
        GL11.glViewport(0, 0, width, height)
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target. Anything rendered after this will be rendered to the
     * screen, and not this FBO.
     */
    fun unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glViewport(0, 0, window.width, window.height)
    }

    /**
     * Binds the current FBO to be read from (not used in tutorial 43).
     */
    fun bindToRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer)
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0)
    }

    /**
     * Resolves a Multi-sample Fbo to the Specified Regular one
     * @param outputFbo -- The Fbo to be resolved to
     */
    fun resolveToFBO(readBuffer: Int, outputFbo: Fbo) {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, outputFbo.frameBuffer)
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer)
        GL11.glReadBuffer(readBuffer)
        GL30.glBlitFramebuffer(
            0, 0, width, height, 0, 0, outputFbo.width, outputFbo.height,
            GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST
        )
        this.unbindFrameBuffer()
    }

    /**
     * Resolves a Multi-sample Fbo to the Default Fbo
     */
    fun resolveToScreen() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer)
        GL11.glDrawBuffer(GL11.GL_BACK)
        GL30.glBlitFramebuffer(
            0, 0, width, height, 0, 0, window.width, window.height,
            GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST
        )
        this.unbindFrameBuffer()
    }

    /**
     * Creates the FBO along with a colour buffer texture attachment, and
     * possibly a depth buffer.
     *
     * @param type
     * - the type of depth buffer attachment to be attached to the
     * FBO.
     */
    private fun initialiseFrameBuffer(type: Int) {
        createFrameBuffer()
        if (multisampleAndMultiTarget) {
            colourBuffer = createMultisampleColourAttachment(GL30.GL_COLOR_ATTACHMENT0)
            colourBuffer2 = createMultisampleColourAttachment(GL30.GL_COLOR_ATTACHMENT1)
        } else {
            createTextureAttachment()
        }
        if (type == DEPTH_RENDER_BUFFER) {
            createDepthBufferAttachment()
        } else if (type == DEPTH_TEXTURE) {
            createDepthTextureAttachment()
        }
        unbindFrameBuffer()
    }

    /**
     * Creates a new frame buffer object and sets the buffer to which drawing
     * will occur - colour attachment 0. This is the attachment where the colour
     * buffer texture is.
     *
     */
    private fun createFrameBuffer() {
        frameBuffer = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        determineDrawBuffers()
    }

    private fun determineDrawBuffers() {
        val drawBuffers = BufferUtils.createIntBuffer(2)
        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0)
        if (this.multisampleAndMultiTarget) {
            drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1)
        }
        drawBuffers.flip()
        GL20.glDrawBuffers(drawBuffers)
    }

    /**
     * Creates a texture and sets it as the colour buffer attachment for this
     * FBO.
     */
    private fun createTextureAttachment() {
        colourTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
            null as ByteBuffer?
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        GL30.glFramebufferTexture2D(
            GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture,
            0
        )
    }

    /**
     * Adds a depth buffer to the FBO in the form of a texture, which can later
     * be sampled.
     */
    private fun createDepthTextureAttachment() {
        depthTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
            GL11.GL_FLOAT, null as ByteBuffer?
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0)
    }

    private fun createMultisampleColourAttachment(attachment: Int): Int {
        colourBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer)
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_RGBA8, width, height)
        GL30.glFramebufferRenderbuffer(
            GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER,
            colourBuffer
        )
        return colourBuffer
    }

    /**
     * Adds a depth buffer to the FBO in the form of a render buffer. This can't
     * be used for sampling in the shaders.
     */
    private fun createDepthBufferAttachment() {
        depthBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer)
        if (!multisampleAndMultiTarget) {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height)
        } else {
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL14.GL_DEPTH_COMPONENT24, width, height)
        }
        GL30.glFramebufferRenderbuffer(
            GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
            depthBuffer
        )
    }

    companion object {
        const val NONE: Int = 0
        const val DEPTH_TEXTURE: Int = 1
        const val DEPTH_RENDER_BUFFER: Int = 2
    }
}
