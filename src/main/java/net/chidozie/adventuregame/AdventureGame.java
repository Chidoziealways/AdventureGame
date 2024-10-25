package net.chidozie.adventuregame;

import com.jogamp.nativewindow.util.PixelFormat;
import model.ModelLoader;
import net.chidozie.adventuregame.jpanel.ImagePanel;
import updater.UpdateClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.PixelGrabber;
import java.io.*;
// import com.jogamp.opengl.*; // Commented out
// import com.jogamp.opengl.awt.GLCanvas; // Commented out
// import com.jogamp.opengl.util.Animator; // Commented out

public class AdventureGame extends JFrame implements ActionListener, Serializable /*, GLEventListener */ {
    private Player player;
    private JButton button;
    public static JTextField textField;
    // private GLCanvas canvas; // Commented out
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JProgressBar progressBar;
    private JLabel jlabel;


    public AdventureGame(Player player) throws IOException {
        try {
            System.out.println("Setting up JFrame...");

            setTitle("Adventure Game Prologue");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Initialize CardLayout and CardPanel
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);

            // Create panels for different screens
            JPanel screen1 = createScreen1();
            JPanel screen2 = createScreen2();

            // Add panels to CardPanel
            cardPanel.add(screen1, "Screen 1");
            cardPanel.add(screen2, "Screen 2");

            // Add CardPanel to JFrame
            setContentPane(cardPanel);

            /* // Initialize OpenGL profile and canvas - Commented out
            GLProfile.initSingleton();
            GLProfile profile = GLProfile.get(GLProfile.GL2);
            GLCapabilities capabilities = new GLCapabilities(profile);
            canvas = new GLCanvas(capabilities);
            canvas.addGLEventListener(this);
            getContentPane().add(canvas, BorderLayout.SOUTH);
            Animator animator = new Animator(canvas);
            animator.start();
            */

            // Make JFrame visible after everything is set up
            setVisible(true);
            this.player = player;

            // Start Progress Bar
            startProgressBar();

            System.out.println("JFrame setup complete.");
        } catch (Exception e) {
            System.out.println("Error during JFrame setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createScreen1() {
        JPanel screen1 = new JPanel();
        screen1.setLayout(null);

        // Components
        Font pixel = new Font("Monospaced", Font.BOLD, 100);
        JLabel loadingTitle = new JLabel("ADVENTURE GAME");
        loadingTitle.setFont(pixel);
        loadingTitle.setBounds(200, 200, 1000, 100);

        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(130, 500, 1000, 30);
        jlabel = new JLabel();
        jlabel.setBounds(600, 550, 300, 30);
        screen1.add(progressBar);
        screen1.add(jlabel);
        screen1.add(loadingTitle);

        return screen1;
    }

    private JPanel createScreen2() {
        JPanel screen2 = new JPanel();
        screen2.setLayout(null);
        screen2.setBackground(Color.BLUE);
        screen2.add(new JLabel("Screen 2"));
        ImageIcon icon = new ImageIcon(""); // Provide the correct path to your image
        ImagePanel imagePanel = new ImagePanel(icon.getImage());
        imagePanel.setLayout(null);
        button = new JButton("Start");
        JLabel label = new JLabel("Hello");
        label.setBounds(10, 10, 80, 20);
        button.setBounds(180, 80, 100, 30);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        textField = new JTextField(20);
        textField.setBounds(180, 500, 80, 30);
        JButton button1 = new JButton("SUBMIT");
        button1.setBounds(180, 150, 100, 30);
        button1.addActionListener(e -> {
            new Thread(() -> {
                try {
                    UpdateClient.updateJar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        imagePanel.add(button);
        imagePanel.add(label);
        imagePanel.add(textField);
        imagePanel.add(button1);
        screen2.add(imagePanel);
        return screen2;
    }

    /*
    private void createGLCanvas() { // Commented out
        canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
        canvas.addGLEventListener(this);
        getContentPane().add(canvas, BorderLayout.SOUTH);
        Animator animator = new Animator(canvas);
        animator.start();
    }
    */

    private void startProgressBar() {
        Timer timer = new Timer(100, new ActionListener() {
            private int progress = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 1;
                progressBar.setValue(progress);
                jlabel.setText(progress + "%");
                if (progress >= 100) {
                    ((Timer) e.getSource()).stop();
                    fadeToScreen("Screen 2");
                }
            }
        });
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            fadeToScreen("Screen 2");
        }
    }

    public void fadeToScreen(String screenName) {
        Timer timer = new Timer(30, new ActionListener() {
            private float alpha = 1f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f;
                if (alpha <= 0) {
                    alpha = 0;
                    ((Timer) e.getSource()).stop();
                    cardLayout.show(cardPanel, screenName);
                }
            }
        });
        timer.start();
    }

    /*
    @Override
    public void init(GLAutoDrawable drawable) { // Commented out
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST);
        try {
            ModelLoader.loadModel("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) { // Commented out
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Render your 3D model here
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {} // Commented out

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { // Commented out
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0) height = 1;
        float aspect = (float) width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(-aspect, aspect, -1.0, 1.0, 1.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    */

    public static void main(String[] args) throws IOException {
        new AdventureGame(new Player());
        UpdateClient.updateJar();
    }

    public void Save(AdventureGame adventureGame) throws FileNotFoundException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("BinarySaves/Game.svr");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(adventureGame);
        } catch (IOException e) {
            System.out.println("There was an Exception: " + e);
        }
    }

    public void Load() {
        // Implementation for loading the game
    }

    public void MainGame(){
        //Implementation for the Main Game
    }
}
