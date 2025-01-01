package adventuregame.net.chidozie.adventuregame.engine.io;

import adventuregame.net.chidozie.adventuregame.engine.maths.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Window {
    private int width, height;
    private String title;
    private long window;
    public int frames;
    public static long time;
    public Input input;
    private Vector3f background = new Vector3f(0, 0, 0);
    private GLFWWindowSizeCallback sizeCallback;
    private boolean isResized;
    private boolean isFullScreen;
    private int[] windowPosX = new int[1], windowPosY = new int[1];

    public Window(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create(){
         if(!GLFW.glfwInit()){
             System.err.println("Error: GLFW wasn't initialized");
             return;
         }

        input = new Input();
        window = GLFW.glfwCreateWindow(width, height, title, isFullScreen ?  GLFW.glfwGetPrimaryMonitor() : 0, 0);

         if(window == 0){
             System.out.println("Error: Window wasn't created");
             return;
         }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
         windowPosX[0] = (videoMode.width() - width) / 2;
         windowPosY[0] = (videoMode.height() - height) / 2;
         GLFW.glfwSetWindowPos(window, windowPosX[0] , windowPosY[0]);
         GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        createCallbacks();


         GLFW.glfwShowWindow(window);

         GLFW.glfwSwapInterval(1);

         time = System.currentTimeMillis();
    }

    private void createCallbacks(){
        sizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallBack());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
        GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallBack());
    }

    public void update() {
        if(isResized){
            GL11.glViewport(0, 0, width, height);
            isResized = false;
        }
        GL11.glClearColor(background.getX(), background.getY(), background.getZ(), 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GLFW.glfwPollEvents();
        frames++;
        if(System.currentTimeMillis() > time + 1000){
            GLFW.glfwSetWindowTitle(window, title + " | FPS: " + frames);
            time = System.currentTimeMillis();
            frames = 0;
        }
    }

    public void swapBuffers(){
        GLFW.glfwSwapBuffers(window);
    }

    public boolean shouldClose(){
        return GLFW.glfwWindowShouldClose(window);
    }

    public void destroy(){
        input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void setBackgroundColor(float r, float g, float b){
        background.set(r, g, b);
    }

    public static long getTime() {
        return time;
    }

    public float getBackgroundR() {
        return background.getX();
    }

    public float getBackgroundG() {
        return background.getY();
    }

    public float getBackgroundB() {
        return background.getZ();
    }

    public int getFrames() {
        return frames;
    }


    public Input getInput() {
        return input;
    }


    public boolean isResized() {
        return isResized;
    }


    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
        isResized = true;
        if(isFullScreen){
            GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, 0);
        } else{
            GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, 0);
        }
    }
}