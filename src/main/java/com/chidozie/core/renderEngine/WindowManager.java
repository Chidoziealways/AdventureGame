package com.chidozie.core.renderEngine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static net.adventuregame.toolbox.Consts.*;

public class WindowManager {
    private static final Logger log = LoggerFactory.getLogger(WindowManager.class);
    private long window, cursor;
    private int width, height, fps_cap;
    private String title;
    private boolean resized, vSync;

    private static long lastFrameTime;
    private static float delta;

    private KeyInputManager keyInputManager;

    public WindowManager(String title, boolean vSync) {
        width = WIDTH;
        height = HEIGHT;
        fps_cap = FPS_CAP;
        this.title = title;
        this.vSync = vSync;
        init();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err);

        if (!glfwInit())
            throw new IllegalStateException("Unable to Initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        boolean maximized = false;
        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximized = true;
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to Create GLFW Window");
        }

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);
        });

        if (maximized)
            glfwMaximizeWindow(window);
        else {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidMode.width() - width) / 2,
                    (vidMode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Initialize the KeyInputManager
        keyInputManager = new KeyInputManager(this);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_CAPTURED);
        cursor = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
        glfwSetCursor(window, cursor);

        if (isvSync())
            glfwSwapInterval(1);

        glViewport(0, 0, width, height);
        lastFrameTime = getCurrentTime();

        glfwShowWindow(window);
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void cleanup() {
        glfwDestroyWindow(window);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwSwapInterval(fps_cap);
        glfwPollEvents();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(window, keyCode) == GLFW_PRESS;
    }

    public KeyInputManager getKeyInputManager() {
        return keyInputManager;
    }


    private static long getCurrentTime() {
        return (System.nanoTime() / 1_000_000);
    }

    public long getWindow() {
        return window;
    }

    public boolean isResized() {
        return resized;
    }
}

