package com.adv.core.renderEngine

import net.adventuregame.game.GameState.Companion.getInstance
import net.adventuregame.save.SaveManager.saveGame
import net.adventuregame.toolbox.Consts
import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The class that is in charge of creating a GLFW window and setting up everything to do with GLFW
 * @author Chidozie Derek Chidozie-Uzowulu
 * @since 0.0.1
 */
class WindowManager @JvmOverloads constructor(
    private val vSync: Boolean,
    var width: Int = Consts.WIDTH,
    var height: Int = Consts.HEIGHT
) {
    var window: Long = 0
        private set
    private var cursor: Long = 0
    private val fps_cap: Int
    private val title: String
    var isResized: Boolean = false

    var keyInputManager: KeyInputManager? = null
        private set

    // Mouse-related fields
    private val previousMousePos = Vector2d(-1.0, -1.0)
    private val currentMousePos = Vector2d(0.0, 0.0)
    val displsVec: Vector2f = Vector2f()
    private var mouseInWindow = false
    var scrollOffset: Double = 0.0
        private set

    var maximized: AtomicBoolean = AtomicBoolean(false)

    init {
        this.fps_cap = Consts.FPS_CAP
        this.title = Consts.TITLE
        init()
    }

    fun init() {
        GLFWErrorCallback.createPrint(System.err)

        check(GLFW.glfwInit()) { "Unable to Initialize GLFW" }

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)

        if (width == 0 || height == 0) {
            width = 100
            height = 100
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE)
            maximized.set(true)
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) throw RuntimeException("Failed to Create GLFW Window")

        setWindowIcon(window, "icon")

        // Setup callbacks
        GLFW.glfwSetFramebufferSizeCallback(
            window
        ) { w: Long, newWidth: Int, newHeight: Int ->
            this.width = newWidth
            this.height = newHeight
            this.isResized = true
        }

        GLFW.glfwSetKeyCallback(window) { w: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                saveGame(getInstance())
                GLFW.glfwSetWindowShouldClose(w, true)
            }
            if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_RELEASE) {
                maximized.set(!maximized.get())
            }
            if (key == GLFW.GLFW_KEY_S && action == GLFW.GLFW_PRESS && (mods and GLFW.GLFW_MOD_CONTROL) != 0) {
                saveGame(getInstance())
            }
        }

        // Mouse callbacks
        GLFW.glfwSetCursorPosCallback(window) { w: Long, xpos: Double, ypos: Double ->
            currentMousePos.x = xpos
            currentMousePos.y = ypos
        }

        GLFW.glfwSetCursorEnterCallback(
            window
        ) { w: Long, entered: Boolean -> mouseInWindow = entered }

        GLFW.glfwSetMouseButtonCallback(
            window
        ) { w: Long, button: Int, action: Int, mods: Int -> }

        GLFW.glfwSetScrollCallback(window, object : GLFWScrollCallback() {
            override fun invoke(w: Long, xOffset: Double, yOffset: Double) {
                scrollOffset = yOffset
            }
        })

        if (maximized.get()) GLFW.glfwMaximizeWindow(window)
        else {
            val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            GLFW.glfwSetWindowPos(window, (vidMode!!.width() - width) / 2, (vidMode.height() - height) / 2)
        }

        GLFW.glfwMakeContextCurrent(window)
        GL.createCapabilities()

        keyInputManager = KeyInputManager(this)
        toggleMouseLock()

        cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        GLFW.glfwSetCursor(window, cursor)

        if (isvSync()) GLFW.glfwSwapInterval(1)

        GL11.glViewport(0, 0, width, height)
        lastFrameTime = currentTime
        GLFW.glfwShowWindow(window)
    }

    fun update() {
        GLFW.glfwSwapBuffers(window)
        GLFW.glfwSwapInterval(fps_cap)
        GLFW.glfwPollEvents()
        updateMouseInput()
        val currentFrameTime: Long = currentTime
        frameTimeSeconds = (currentFrameTime - lastFrameTime) / 1000f
        lastFrameTime = currentFrameTime

        if (maximized.get()) GLFW.glfwMaximizeWindow(window)
        else {
            val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            GLFW.glfwSetWindowPos(window, (vidMode!!.width() - width) / 2, (vidMode.height() - height) / 2)
        }
    }

    private fun updateMouseInput() {
        displsVec.x = 0f
        displsVec.y = 0f
        if (previousMousePos.x > 0 && previousMousePos.y > 0 && mouseInWindow) {
            val dx = currentMousePos.x - previousMousePos.x
            val dy = currentMousePos.y - previousMousePos.y
            if (dx != 0.0) displsVec.y = dx.toFloat()
            if (dy != 0.0) displsVec.x = dy.toFloat()
        }
        previousMousePos.set(currentMousePos)
    }

    fun resetScrollOffset() {
        scrollOffset = 0.0
    }

    fun windowShouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(window)
    }

    fun isvSync(): Boolean {
        return vSync
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS
    }

    fun isButtonPressed(button: Int): Boolean {
        return GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_PRESS
    }

    fun toggleMouseLock() {
        val current = GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR)
        GLFW.glfwSetInputMode(
            window, GLFW.GLFW_CURSOR,
            if (current == GLFW.GLFW_CURSOR_DISABLED) GLFW.GLFW_CURSOR_NORMAL else GLFW.GLFW_CURSOR_DISABLED
        )
    }

    fun cleanup() {
        GLFW.glfwDestroyCursor(cursor)
        GLFW.glfwDestroyWindow(window)
    }

    companion object {
        private val log: Logger? = LoggerFactory.getLogger(WindowManager::class.java)
        private var lastFrameTime: Long = 0
        var frameTimeSeconds: Float = 0f
            private set

        private val currentTime: Long
            get() = System.nanoTime() / 1000000

        fun setWindowIcon(window: Long, iconPath: String?) {
            // Load image as ByteBuffer from classpath
            val imageBuffer: ByteBuffer?
            try {
                Loader::class.java.classLoader
                    .getResourceAsStream("assets/adventuregame/textures/icon/$iconPath.png").use { stream ->
                        if (stream == null) {
                            throw RuntimeException("Resource not found: $iconPath")
                        }
                        // Read image into ByteBuffer
                        val bytes = stream.readAllBytes()
                        imageBuffer = ByteBuffer.allocateDirect(bytes.size)
                        imageBuffer.put(bytes)
                        imageBuffer.flip()
                    }
            } catch (e: Exception) {
                throw RuntimeException("Failed to load resource: $iconPath", e)
            }

            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channels = stack.mallocInt(1)

                // Load image data
                val buffer = STBImage.stbi_load_from_memory(imageBuffer!!, w, h, channels, 4)
                if (buffer == null) {
                    throw RuntimeException("Failed to load icon image: " + STBImage.stbi_failure_reason())
                }

                // Create GLFWImage and buffer
                val icon = GLFWImage.malloc(stack)
                icon.set(w.get(), h.get(), buffer)

                val icons = GLFWImage.malloc(1, stack)
                icons.put(0, icon)

                // Set the icon
                GLFW.glfwSetWindowIcon(window, icons)

                // Free image memory after setting
                STBImage.stbi_image_free(buffer)
            }
        }
    }
}
