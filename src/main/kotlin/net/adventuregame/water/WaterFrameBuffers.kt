package net.adventuregame.water

import com.adv.core.renderEngine.WindowManager
import net.adventuregame.game.AdventureMain
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import java.nio.ByteBuffer

class WaterFrameBuffers {
    private var reflectionFrameBuffer = 0

    //get the resulting texture
    var reflectionTexture: Int = 0
        private set
    private var reflectionDepthBuffer = 0

    private var refractionFrameBuffer = 0

    //get the resulting texture
    var refractionTexture: Int = 0
        private set

    //get the resulting depth texture
    var refractionDepthTexture: Int = 0
        private set

    private val window: WindowManager


    init {
        //call when loading the game
        window = AdventureMain.window
        initialiseReflectionFrameBuffer()
        initialiseRefractionFrameBuffer()
    }


    fun cleanUp() { //call when closing the game
        GL30.glDeleteFramebuffers(reflectionFrameBuffer)
        GL11.glDeleteTextures(reflectionTexture)
        GL30.glDeleteRenderbuffers(reflectionDepthBuffer)
        GL30.glDeleteFramebuffers(refractionFrameBuffer)
        GL11.glDeleteTextures(refractionTexture)
        GL11.glDeleteTextures(refractionDepthTexture)
    }


    fun bindReflectionFrameBuffer() { //call before rendering to this FBO
        bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT)
    }

    fun bindRefractionFrameBuffer() { //call before rendering to this FBO
        bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT)
    }

    fun unbindCurrentFrameBuffer() { //call to switch to default frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glViewport(0, 0, window.width, window.height)
    }


    private fun initialiseReflectionFrameBuffer() {
        reflectionFrameBuffer = createFrameBuffer()
        reflectionTexture = createTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT)
        reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT)
        unbindCurrentFrameBuffer()
    }

    private fun initialiseRefractionFrameBuffer() {
        refractionFrameBuffer = createFrameBuffer()
        refractionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT)
        refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT)
        unbindCurrentFrameBuffer()
    }

    private fun bindFrameBuffer(frameBuffer: Int, width: Int, height: Int) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0) //To make sure the texture isn't bound
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        GL11.glViewport(0, 0, width, height)
    }


    private fun createFrameBuffer(): Int {
        val frameBuffer = GL30.glGenFramebuffers()
        //generate name for frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        //create the framebuffer
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
        //indicate that we will always render to color attachment 0
        return frameBuffer
    }


    private fun createTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
            0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, null as ByteBuffer?
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(
            GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
            texture, 0
        )
        return texture
    }

    private fun createDepthTextureAttachment(width: Int, height: Int): Int {
        val texture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
            0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null as ByteBuffer?
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL32.glFramebufferTexture(
            GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
            texture, 0
        )
        return texture
    }


    private fun createDepthBufferAttachment(width: Int, height: Int): Int {
        val depthBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer)
        GL30.glRenderbufferStorage(
            GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
            height
        )
        GL30.glFramebufferRenderbuffer(
            GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
            GL30.GL_RENDERBUFFER, depthBuffer
        )
        return depthBuffer
    }


    companion object {
        protected const val REFLECTION_WIDTH: Int = 320
        private const val REFLECTION_HEIGHT = 180

        protected const val REFRACTION_WIDTH: Int = 1280
        private const val REFRACTION_HEIGHT = 720
    }
}

