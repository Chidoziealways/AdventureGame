package adventuregame.net.chidozie.adventuregame;

import adventuregame.net.chidozie.adventuregame.engine.graphics.*;
import adventuregame.net.chidozie.adventuregame.engine.io.Input;
import adventuregame.net.chidozie.adventuregame.engine.io.Window;
import adventuregame.net.chidozie.adventuregame.engine.maths.Vector2f;
import adventuregame.net.chidozie.adventuregame.engine.maths.Vector3f;
import adventuregame.updater.UpdateClient;
import org.lwjgl.glfw.GLFW;

public class AdventureGame implements Runnable{
    public Thread mainGame;
    public static Window window;
    public Renderer renderer;
    public Shader shader;
    public static final int WIDTH = 1280, HEIGHT = 760;

    public Mesh mesh = new Mesh(new Vertex[] {
        new Vertex(new Vector3f(-0.5f,  0.5f, 0.0f), new Vector3f(1.0f, 0.0f, 1.0f), new Vector2f(0.0f, 0.0f)),
        new Vertex(new Vector3f( 0.5f,  0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 1.0f), new Vector2f(0.0f, 1.0f)),
        new Vertex(new Vector3f( 0.5f, -0.5f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector2f(1.0f, 1.0f)),
        new Vertex(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector2f(1.0f, 0.0f))
    }, new int[] {
        0, 1, 2,
        0, 3, 2
    }, new Material("/assets/textures/tilemap.png"));

    public void start(){
        mainGame = new Thread(this, "mainGame");
        mainGame.start();
    }

    public void init(){
        System.out.println("INITIALIZING THIS GAME!");
        window = new Window(WIDTH, HEIGHT, "Adventure Game");
        System.out.println(System.getProperty("java.class.path"));
        shader = new Shader("/assets/shaders/mainVertex.glsl", "/assets/shaders/mainFragment.glsl");
        renderer = new Renderer(shader);
        window.setBackgroundColor(1.0f, 0, 0);
        window.create();
        mesh.create();
        shader.create();
    }

    public void run(){
        init();
        while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
            update();
            render();
            if(Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullScreen(!window.isFullScreen());
        }
        close();
    }

    private void update(){
        //System.out.println("UPDATING GAME GRAPHICS");
        window.update();
        if(Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) System.out.println("X: " + Input.getScrollX() + ", Y: " + Input.getScrollY());

    }

    private void render(){
        renderer.renderMesh(mesh);
        window.swapBuffers();
    }

    private void close() {
        window.destroy();
        mesh.destroy();
        shader.destroy();
    }

    public static void main(String[] args){
        UpdateClient.updateJar();
        new AdventureGame().start();
    }
}