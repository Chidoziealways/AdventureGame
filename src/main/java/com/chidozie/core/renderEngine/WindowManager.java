package com.chidozie.core.renderEngine;

import net.adventuregame.game.GameInit;
import net.adventuregame.game.GameState;
import net.adventuregame.save.SaveManager;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static net.adventuregame.toolbox.Consts.*;

/**
 * The class that is in charge of creating a GLFW window and setting up everything to do with GLFW
 * @author Chidozie Derek Chidozie-Uzowulu
 * @since 0.0.1
 */
public class WindowManager {
    private static final Logger log = LoggerFactory.getLogger(WindowManager.class);
    private long window, cursor;
    private int width, height, fps_cap;
    private String title;
    private boolean resized, vSync;

    private static long lastFrameTime;
    private static float delta;

    private KeyInputManager keyInputManager;

    // Mouse-related fields
    private final Vector2d previousMousePos = new Vector2d(-1, -1);
    private final Vector2d currentMousePos = new Vector2d(0, 0);
    private final Vector2f mouseDisplacement = new Vector2f();
    private boolean mouseInWindow = false;
    private double scrollOffset = 0.0;

    AtomicBoolean maximized = new AtomicBoolean(false);

    public WindowManager(boolean vSync) {
        this(vSync, WIDTH, HEIGHT);
    }

    public WindowManager(boolean vSync, int width, int height) {
        this.width = width;
        this.height = height;
        this.fps_cap = FPS_CAP;
        this.title = TITLE;
        this.vSync = vSync;
        init();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err);

        if (!glfwInit())
            throw new IllegalStateException("Unable to Initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized.set(true);
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to Create GLFW Window");

        setWindowIcon(window, "icon");

        // Setup callbacks
        glfwSetFramebufferSizeCallback(window, (w, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            this.setResized(true);
        });

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                SaveManager.INSTANCE.saveGame(GameState.getInstance());
                glfwSetWindowShouldClose(w, true);
            }
            if (key == GLFW_KEY_F11 && action == GLFW_RELEASE) {
                maximized.set(!maximized.get());
            }
            if (key == GLFW_KEY_S && action == GLFW_PRESS && (mods & GLFW_MOD_CONTROL) != 0) {
                SaveManager.INSTANCE.saveGame(GameState.getInstance());
            }
        });

        // Mouse callbacks
        glfwSetCursorPosCallback(window, (w, xpos, ypos) -> {
            currentMousePos.x = xpos;
            currentMousePos.y = ypos;
        });

        glfwSetCursorEnterCallback(window, (w, entered) -> mouseInWindow = entered);

        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {

        });

        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long w, double xOffset, double yOffset) {
                scrollOffset = yOffset;
            }
        });

        if (maximized.get())
            glfwMaximizeWindow(window);
        else {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        keyInputManager = new KeyInputManager(this);
        toggleMouseLock();

        cursor = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
        glfwSetCursor(window, cursor);

        if (isvSync())
            glfwSwapInterval(1);

        glViewport(0, 0, width, height);
        lastFrameTime = getCurrentTime();
        glfwShowWindow(window);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwSwapInterval(fps_cap);
        glfwPollEvents();
        updateMouseInput();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;

        if (maximized.get())
            glfwMaximizeWindow(window);
        else {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }
    }

    private void updateMouseInput() {
        mouseDisplacement.x = 0;
        mouseDisplacement.y = 0;
        if (previousMousePos.x > 0 && previousMousePos.y > 0 && mouseInWindow) {
            double dx = currentMousePos.x - previousMousePos.x;
            double dy = currentMousePos.y - previousMousePos.y;
            if (dx != 0)
                mouseDisplacement.y = (float) dx;
            if (dy != 0)
                mouseDisplacement.x = (float) dy;
        }
        previousMousePos.set(currentMousePos);
    }

    public Vector2f getDisplsVec() {
        return mouseDisplacement;
    }

    public double getScrollOffset() {
        return scrollOffset;
    }

    public void resetScrollOffset() {
        scrollOffset = 0.0;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isResized() {
        return resized;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public boolean isButtonPressed(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public KeyInputManager getKeyInputManager() {
        return keyInputManager;
    }

    public long getWindow() {
        return window;
    }

    public void toggleMouseLock() {
        int current = glfwGetInputMode(window, GLFW_CURSOR);
        glfwSetInputMode(window, GLFW_CURSOR,
                current == GLFW_CURSOR_DISABLED ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public void cleanup() {
        glfwDestroyCursor(cursor);
        glfwDestroyWindow(window);
    }

    private static long getCurrentTime() {
        return System.nanoTime() / 1_000_000;
    }

    public static void setWindowIcon(long window, String iconPath) {
        // Load image as ByteBuffer from classpath
        ByteBuffer imageBuffer;
        try (InputStream stream = Loader.class.getClassLoader().getResourceAsStream("assets/adventuregame/textures/icon/" + iconPath + ".png")) {
            if (stream == null) {
                throw new RuntimeException("Resource not found: " + iconPath);
            }

            // Read image into ByteBuffer
            byte[] bytes = stream.readAllBytes();
            imageBuffer = ByteBuffer.allocateDirect(bytes.length);
            imageBuffer.put(bytes);
            imageBuffer.flip();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + iconPath, e);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Load image data
            ByteBuffer buffer = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (buffer == null) {
                throw new RuntimeException("Failed to load icon image: " + STBImage.stbi_failure_reason());
            }

            // Create GLFWImage and buffer
            GLFWImage icon = GLFWImage.malloc(stack);
            icon.set(w.get(), h.get(), buffer);

            GLFWImage.Buffer icons = GLFWImage.malloc(1, stack);
            icons.put(0, icon);

            // Set the icon
            glfwSetWindowIcon(window, icons);

            // Free image memory after setting
            STBImage.stbi_image_free(buffer);
        }
    }

}
