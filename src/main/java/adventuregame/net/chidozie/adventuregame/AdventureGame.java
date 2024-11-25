package adventuregame.net.chidozie.adventuregame;

import adventuregame.com.chidozie.MapSerializer;
import adventuregame.net.chidozie.adventuregame.game.GameWindow;
import adventuregame.net.chidozie.adventuregame.map.MapRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jogamp.opengl.*;
import create.net.chidozie.create.Create;
import com.jogamp.opengl.awt.GLCanvas;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import adventuregame.updater.UpdateClient;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicProgressBarUI;


public class AdventureGame extends JFrame implements ActionListener, Serializable {
    public static final String PROPERTIES_FILE = "Run/run.properties";
    private static final String SAVE_DIR = "Saves";
    private static final String SAVE_FILE_PATH = SAVE_DIR + "/savefile.json";
    private static double health = 100.0;
    private static double level = 0.0;
    private static Map<String, String> playerData = new HashMap<>();
    private Map<String, Object> screenState = new HashMap<>();
    private static Player player;
    private JButton button;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JProgressBar progressBar;
    private SlowTyper stext = new SlowTyper();
    private Map<String, JPanel> screens = new HashMap<>();
    private JLabel jlabel;
    private static String uname;
    private static String gender;
    private static GLCanvas canvas = new GLCanvas();
    private Font titleFont;
    private Font labelfont;
    Vector3f[] vertices = {
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, 0.5f, -0.5f)
    };
    int[] indices = {
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            0, 1, 5, 5, 4, 0,
            3, 2, 6, 6, 7, 3,
            0, 3, 7, 7, 4, 0,
            1, 2, 6, 6, 5, 1
    };
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private CountDownLatch latch = new CountDownLatch(1);


    public AdventureGame(Player player) throws IOException, InterruptedException {
        checkFirstRun();
        setupOpenGLContext(new Triangle());
        System.out.println(System.getProperty("java.library.path"));

        System.out.println("Setting up JFrame...");
        setTitle("Adventure Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //TODO: Render Models properly using the same methods minecraft used

        // Set the icon image using a relative path
        String iconPath = "additional/assets/images/Scorpion.png";
        File iconFile = new File(iconPath);

        add(canvas);


        // Check if the icon file exists
        if (!iconFile.exists()) {
            try {
                // URL of the icon file on OneDrive
                URL oneDriveUrl = new URL("https://1drv.ms/i/s!Ah_0Zcex0RSTh_5iXBPDWYy7j7NEkw?e=ClwspF");
                // Download and save the icon
                try (InputStream in = oneDriveUrl.openStream()) {
                    Files.copy(in, iconFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageIcon icon = new ImageIcon(iconPath);
        setIconImage(icon.getImage());


        // Initialize CardLayout and CardPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);


        // Add CardPanel to JFrame
        setContentPane(cardPanel);

        setupScreens();
        // Start Progress Bar
        startProgressBar(1);






        // Make JFrame visible after everything is set up

        System.out.println("JFrame setup complete.");

        setVisible(true);


    }



        private JPanel createLoadingScreen () {
            JPanel loadingScreen = new JPanel();
            loadingScreen.setLayout(null);

            // Components
            titleFont = new Font("Pixel", Font.BOLD, 100);
            JLabel loadingTitle = new JLabel("ADVENTURE GAME");
            loadingTitle.setFont(titleFont);
            loadingTitle.setBounds(200, 200, 1000, 100);

            progressBar = new JProgressBar(0, 100);
            progressBar.setBounds(130, 500, 1000, 30);
            progressBar.setUI(new BasicProgressBarUI() {
                protected Color getSelectionBackground() {
                    return Color.WHITE;
                }

                protected Color getSelectionForeground() {
                    return Color.RED;
                }
            });
            progressBar.setForeground(Color.RED);
            progressBar.setBackground(Color.BLACK);

            jlabel = new JLabel();
            jlabel.setBounds(600, 550, 300, 30);

            loadingScreen.add(progressBar);
            loadingScreen.add(jlabel);
            loadingScreen.add(loadingTitle);

            return loadingScreen;
        }

        private JPanel createTitleScreen () {
            JPanel titleScreen = null;
            titleScreen = getjPanel(titleScreen);

            JLabel label = new JLabel("Adventure Game");
            label.setBounds(400, 10, 1000, 90);
            label.setFont(titleFont);

            button = new JButton("Start");
            button.setBounds(400, 150, 600, 90);
            button.addActionListener(this);
            // Load the image
            ImageIcon startButtonIcon = new ImageIcon("additional/assets/images/Scorpion.png");
            button.setIcon(startButtonIcon);
            // Optional:
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.CENTER);

            JButton closeButton = new JButton("Quit Game");
            closeButton.setBounds(400, 550, 600, 90);
            closeButton.addActionListener(e -> dispose());

            titleScreen.add(label);
            titleScreen.add(button);

            titleScreen.add(closeButton);

            return titleScreen;
        }

        private JPanel getjPanel (JPanel screen) {
            screen = new JPanel() {
                BufferedImage image;

                {
                    try {
                        image = ImageIO.read(new File("additional/assets/images/Scorpion.png"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (image != null) {
                        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                    }
                }
            };
            screen.setLayout(null);
            screen.setBackground(Color.LIGHT_GRAY);
            return screen;
        }

        private JPanel createFirstTime () {
            JPanel firstTime = new JPanel();
            firstTime.setLayout(null);

            JLabel jLabel = new JLabel();
            jLabel.setBounds(200, 100, 1000, 100);
            labelfont = new Font("Pixel", Font.BOLD, 30);
            jLabel.setFont(labelfont);
            jLabel.setVisible(true);

            JTextField jTextField = new JTextField();
            jTextField.setBounds(200, 400, 1000, 50);
            jTextField.setVisible(false);

            JButton submitButton = new JButton("Submit");
            submitButton.setBounds(200, 300, 200, 30);
            submitButton.setVisible(false);

            JCheckBox jCheckBox = new JCheckBox("Male");
            jCheckBox.setBounds(100, 400, 90, 60);
            jCheckBox.setVisible(false);

            JCheckBox jCheckBox2 = new JCheckBox("Female");
            jCheckBox2.setBounds(400, 400, 90, 60);
            jCheckBox2.setVisible(false);


            firstTime.addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {

                }

                @Override
                public void componentMoved(ComponentEvent e) {

                }

                @Override
                public void componentShown(ComponentEvent e) {
                    String text = "Ah... It looks like you're new here. What's your name?";
                    System.out.println("ASKING NAME");
                    stext.type(jLabel, text, 200, Arrays.asList(
                            () -> jTextField.setVisible(true),
                            () -> submitButton.setVisible(true)
                    ));
                }

                @Override
                public void componentHidden(ComponentEvent e) {

                }
            });


            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    uname = jTextField.getText();
                    jTextField.setVisible(false);
                    submitButton.setVisible(false);
                    jLabel.setText(" ");
                    String text = "Hello " + uname + " What is your gender?";
                    stext.type(jLabel, text, 100, Arrays.asList(
                            () -> jCheckBox.setVisible(true),
                            () -> jCheckBox2.setVisible(true)
                    ));
                }
            });
            JButton done = new JButton("DONE");
            done.setBounds(100, 500, 100, 50);
            done.setVisible(false);

            ActionListener checkBoxListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (jCheckBox.isSelected()) {
                        gender = "Male";
                        done.setVisible(true);
                    }
                }
            };
            ActionListener checkBoxListener2 = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (jCheckBox2.isSelected()) {
                        gender = "Female";
                        done.setVisible(true);
                    }
                }
            };

            jCheckBox.addActionListener(checkBoxListener);
            jCheckBox2.addActionListener(checkBoxListener2);

            done.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player = new Player(uname, gender, level, health, new Vector3f(0, 0, 0), vertices, indices);
                    savePlayer(player, "Prologue Screen", null);
                    fadeToScreen("Prologue Screen", 30);
                }
            });

            firstTime.add(jLabel);
            firstTime.add(jTextField);
            firstTime.add(submitButton);
            firstTime.add(done);
            firstTime.add(jCheckBox2);
            firstTime.add(jCheckBox);

            return firstTime;
        }

        private JPanel createPrologueScreen () {
            JPanel prologueScreen = new JPanel();
            SlowTyper stext = new SlowTyper();
            prologueScreen.setLayout(null);
            final String[] fText = new String[1];
            JTextArea firstText = new JTextArea();
            firstText.setBounds(100, 100, 1000, 200);
            firstText.setFont(labelfont);
            firstText.setWrapStyleWord(true);
            firstText.setLineWrap(true);
            firstText.setOpaque(false);
            firstText.setEditable(true);
            firstText.setFocusable(false);
            JButton skip = new JButton("Skip");
            skip.setBounds(300, 500, 300, 190);



            skip.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);

                    //Create and start the game in a Seperate Thread
                    Thread gameThread = new Thread(() -> {
                        GameWindow gameWindow = new GameWindow(new MapRenderer());
                        gameWindow.run();

                        //Show the Launcher when the game exits
                        SwingUtilities.invokeLater(() -> setVisible(true));
                    });
                    gameThread.start();

                }
            });

            prologueScreen.addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {

                }

                @Override
                public void componentMoved(ComponentEvent e) {

                }

                @Override
                public void componentShown(ComponentEvent e) {
                    if(checkFirstRun()){
                        skip.setEnabled(true);
                    }
                    setTitle("Adventure Game-Prologue");
                    firstText.setVisible(true);
                    stext.type(firstText, "In 1945..., there was a war between the Japanese and the Americans. The Americans dropped a bomb on top of Hiroshima, Japan. Devastating the entire city... Or so they thought.... Years later, the Japanese Defense Ministry later on found out that the bomb was dropped by a UFO found above the country at the time the bomb was", 90, Arrays.asList(
                            () -> firstText.setText(" "),
                            () -> stext.type(firstText, "dropped. The Truth was Revealed, But was Kept within the JMC. Ever since, the ALiens have been kept in a facility for many years, but then one day, they all escaped...", 1000)
                    ));

                }

                @Override
                public void componentHidden(ComponentEvent e) {

                }
            });
            prologueScreen.add(firstText);
            prologueScreen.add(skip);
            return prologueScreen;
        }

        private JPanel createFirstScreen(){
            JPanel firstscreen = new JPanel();
            firstscreen.setLayout(null);

            JButton create = new JButton("Create");
            create.setVisible(true);
            create.setBounds(400, 300, 400, 100);

            JButton maingame = new JButton("Main Game");
            maingame.setVisible(true);
            maingame.setBounds(400, 500, 400, 100);

            create.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    try {
                        String[] args = new String[1];
                        Create.main(args);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            maingame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sttart();
                }
            });

            firstscreen.add(maingame);
            firstscreen.add(create);
            return firstscreen;
        }





private void setupOpenGLContext(Renderable... renderables) {
    {
        canvas.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                GL.createCapabilities();
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glClear(com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT | com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT);
                Matrix4f projection = new Matrix4f().setPerspective((float) Math.toRadians(70), (float) canvas.getWidth() / canvas.getHeight(), 0.1f, 100f);
                Matrix4f view = new Matrix4f().lookAt(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
                for (Renderable renderable : renderables) {
                    Matrix4f model = new Matrix4f().translate(new Vector3f(0, 0, 0));
                    Matrix4f mvp = new Matrix4f(projection).mul(view).mul(model);
                    renderable.render(mvp);
                }
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL2 gl = drawable.getGL().getGL2();
                gl.glViewport(0, 0, width, height);
            }
        });
        canvas.display();
    }
}

        public void setupScreens () throws InterruptedException {
            cardPanel.add(createLoadingScreen(), "Loading Screen");
            cardPanel.add(createTitleScreen(), "Title Screen");
            cardPanel.add(createFirstScreen(), "First Screen");
            cardPanel.add(createFirstTime(), "First Time");
            cardPanel.add(createPrologueScreen(), "Prologue Screen");
        }

        private void addScreen (String name, JPanel screen){
            screens.put(name, screen);
            cardPanel.add(screen, name);
        }

        private void startProgressBar ( int delay){
            Timer timer = new Timer(delay, new ActionListener() {
                private int progress = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    progress += 1;
                    progressBar.setValue(progress);
                    jlabel.setText(progress + "%");
                    if (progress >= 100) {
                        ((Timer) e.getSource()).stop();
                        fadeToScreen("Title Screen", 100);
                    }
                }
            });
            timer.start();
        }

        @Override
        public void actionPerformed (ActionEvent e){
            if (e.getSource() == button) {
                try {
                    Start();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        private void fadeToScreen(String screenName, int delay){
            Timer timer = new Timer(delay, new ActionListener() {
                private float alpha = 1f;

                @Override
                public void actionPerformed(ActionEvent e) {
                    alpha -= 0.05f;
                    if (alpha <= 0) {
                        alpha = 0;
                        ((Timer) e.getSource()).stop();
                        cardLayout.show(cardPanel, screenName);
                        fadeIn();
                    }
                    setAlphaForComponents(alpha);
                }
            });
            timer.start();
        }


        private void fadeIn () {
            Timer timer = new Timer(30, new ActionListener() {
                private float alpha = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    alpha += 0.05f;
                    setAlphaForComponents(alpha);
                    if (alpha >= 1) {
                        alpha = 1;
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            timer.start();
        }

        private void setAlphaForComponents ( float alpha){
            for (Component component : cardPanel.getComponents()) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    for (Component c : panel.getComponents()) {
                        c.setForeground(new Color(0, 0, 0, alpha));
                    }
                }
            }
            repaint();
        }
        public void switchToScreen (String screenName){
            CardLayout layout = (CardLayout) cardPanel.getLayout();
            layout.show(cardPanel, screenName);
            JPanel screen = screens.get(screenName);
            if (screen != null) {
                cardPanel.revalidate();
                cardPanel.repaint();
            } else {
                System.err.println("Screen not found: " + screenName);
            }
        }

        public static void main (String[]args) throws IOException {
            SwingUtilities.invokeLater(() -> {
                AdventureGame game = null;
                try {
                    game = new AdventureGame(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            UpdateClient.updateJar();
        }

        public void Start () throws IOException {
            cardLayout.show(cardPanel, "First Screen");
            /*if (checkFirstRun()) {
                System.out.println("First time running, showing First Time screen.");
                cardLayout.show(cardPanel, "First Time");
            } else {
                System.out.println("THis is not the first run");
                loadGame();
            }*/
        }


        public void savePlayer (Player player, String currentScreen, Map < String, Object > screenState){
            PlayerState playerState = new PlayerState(player, currentScreen, screenState);
            Gson gson = new GsonBuilder().registerTypeAdapter(Map.class, new MapSerializer()).create();
            File saveDir = new File("Saves");
            if (!saveDir.exists()) {
                saveDir.mkdirs(); // Create directories if they don't exist
            }
            try (FileWriter writer = new FileWriter("Saves/playerState.json")) {
                gson.toJson(playerState, writer);
                System.out.println("Player state saved.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public PlayerState loadPlayer () throws IOException {
            Gson gson = new GsonBuilder().registerTypeAdapter(Map.class, new MapSerializer()).create();
            try (BufferedReader reader = new BufferedReader(new FileReader("Saves/playerState.json"))) {
                return gson.fromJson(reader, PlayerState.class);
            }
        }
        public void loadGame () {
            try {
                PlayerState playerState = loadPlayer();
                if (playerState == null) {
                    System.err.println("Failed to load player state.");
                    return;
                }

                player = new Player(
                        playerState.getUsername(),
                        playerState.getGender(),
                        playerState.getLevel(),
                        playerState.getHealth(),
                        playerState.getPosition(),
                        vertices, // Assume vertices are defined
                        indices   // Assume indices are defined
                );

                // Restore screen and state
                String currentScreen = playerState.getCurrentScreen();
                Map<String, Object> screenState = playerState.getScreenState();

                fadeToScreen(currentScreen, 90);

                // Handle specific points within the screen state

                System.out.println("Player state loaded and applied.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public static void MainGame () {
            // Implementation for the main game logic
        }

        public static boolean checkFirstRun () {
            Properties properties = new Properties();
            File file = new File("Run/run.properties");

            try {
                if (file.exists()) {
                    properties.load(new FileInputStream(file));
                    String firstRun = properties.getProperty("firstRun");
                    if ("false".equals(firstRun)) {
                        System.out.println("This JAR has been run before.");
                        return false;
                    } else {
                        System.out.println("First time running this JAR.");
                        properties.setProperty("firstRun", "false");
                        properties.store(new FileOutputStream(file), null);
                        return true;
                    }
                } else {
                    // Create directories if they don't exist
                    file.getParentFile().mkdirs();
                    properties.setProperty("firstRun", "true");
                    properties.store(new FileOutputStream(file), null);
                    System.out.println("First time running this JAR.");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false; // Fallback in case of an error
            }
        }

    private void addMovementKeyListener(JPanel screen, Player player) {
        screen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Vector3f delta = new Vector3f();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> delta.y = 0.1f;
                    case KeyEvent.VK_DOWN -> delta.y = -0.1f;
                    case KeyEvent.VK_LEFT -> delta.x = -0.1f;
                    case KeyEvent.VK_RIGHT -> delta.x = 0.1f;
                }
                player.move(delta);
                canvas.display();  // Trigger a repaint
            }
        });
        screen.setFocusable(true);
        screen.requestFocusInWindow();
    }
        public void sttart(){

            if (checkFirstRun()) {
                System.out.println("First time running, showing First Time screen.");
                cardLayout.show(cardPanel, "Prologue Screen");
            } else if(!checkFirstRun()) {
                System.out.println("THis is not the first run");
                loadGame();
            }
        }
}
