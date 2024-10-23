package net.chidozie.adventuregame;

import model.ModelLoader;
import net.chidozie.adventuregame.jpanel.ImagePanel;
import updater.UpdateClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class AdventureGame extends JFrame implements ActionListener, Serializable, GLEventListener {
    private Player player;
    private JButton button;
    public static JTextField textField;
    private GLCanvas canvas;

    public AdventureGame(Player player) {
        setTitle("Adventure Game Prologue");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon("");
        ImagePanel panel = new ImagePanel(icon.getImage());

        button = new JButton("Start");
        JLabel label = new JLabel("Hello");
        label.setSize(10, 10);

        button.setBounds(180, 80, 1000, 100);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setVisible(false);

        textField = new JTextField(20);
        textField.setBounds(180, 500, 80, 90);

        JButton button1 = new JButton("SUBMIT");
        button1.setBounds(180, 80, 100, 100);

        panel.add(button);
        panel.add(label);
        panel.add(textField);
        panel.add(button1);

        button1.addActionListener(e -> {
            String jarPath = textField.getText();
            new Thread(() -> {
                try {
                    UpdateClient.updateJar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        add(panel);

        canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
        canvas.addGLEventListener(this);
        getContentPane().add(canvas);

        Animator animator = new Animator(canvas);
        animator.start();

        setVisible(true);
        this.player = player;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            Start();
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL.GL_DEPTH_TEST);
        try {
            ModelLoader.loadModel("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Render your 3D model here
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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

    public static void main(String[] args) throws IOException {
        AdventureGame adventureGame = new AdventureGame(new Player());
        UpdateClient.updateJar();
    }

    public void Start() {
        System.out.println("hello");
        button.setVisible(false);
        button.setText("Hello");
        button.setVisible(true);
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

    public void MainGame() {
        // Implementation for the main game logic
    }
}
