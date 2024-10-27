package net.chidozie.adventuregame;

import com.google.gson.Gson;
import updater.UpdateClient;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.util.*;



public class AdventureGame extends JFrame implements ActionListener, Serializable{
    public static final String PROPERTIES_FILE = "Run/run.properties";
    private static final String SAVE_DIR = "Saves";
    private static final String SAVE_FILE_PATH = SAVE_DIR + "/savefile.json";
    private static double health = 100.0;
    private static double level = 0.0;
    private static Map<String, String> playerData = new HashMap<>();
    private static Player player;
    private JButton button;
    public static JTextField textField;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JProgressBar progressBar;
    private JLabel jlabel;
    private static String uname;
    private static String gender;

    public AdventureGame(Player player) throws IOException {
       checkFirstRun();
        System.out.println("Setting up JFrame...");
        setTitle("Adventure Game Prologue");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setVisible(true);

        // Initialize CardLayout and CardPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels for different screens
        JPanel loadingScreen = createLoadingScreen();
        JPanel titleScreen = createTitleScreen();
        JPanel firstTime = createFirstTime();
        JPanel mainScreen = createMainScreen();

        // Add panels to CardPanel
        cardPanel.add(loadingScreen, "Loading Screen");
        cardPanel.add(titleScreen, "Title Screen");
        cardPanel.add(firstTime, "First Time");
        cardPanel.add(mainScreen, "Main Screen");


        // Add CardPanel to JFrame
        setContentPane(cardPanel);

        // Make JFrame visible after everything is set up
        setVisible(true);
        this.player = player;

        // Start Progress Bar
        startProgressBar(1);
        System.out.println("JFrame setup complete.");
    }





    private JPanel createLoadingScreen() {
        JPanel loadingScreen = new JPanel();
        loadingScreen.setLayout(null);

        // Components
        Font pixel = new Font("Pixel", Font.BOLD, 100);
        JLabel loadingTitle = new JLabel("ADVENTURE GAME");
        loadingTitle.setFont(pixel);
        loadingTitle.setBounds(200, 200, 1000, 100);

        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(130, 500, 1000, 30);
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected Color getSelectionBackground() { return Color.WHITE; }
            protected Color getSelectionForeground() { return Color.RED; }
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

    private JPanel createTitleScreen() {
        JPanel titleScreen = new JPanel();
        titleScreen.setLayout(null);
        titleScreen.setBackground(Color.LIGHT_GRAY);

        JLabel label = new JLabel("Title Screen");
        label.setBounds(10, 10, 80, 20);

        button = new JButton("Start");
        button.setBounds(200, 150, 100, 30);
        button.addActionListener(this);

        textField = new JTextField(20);
        textField.setBounds(180, 500, 80, 30);

        JButton closeButton = new JButton("Quit Game");
        closeButton.setBounds(200, 600, 100, 30);
        closeButton.addActionListener(e -> dispose());

        titleScreen.add(label);
        titleScreen.add(button);
        titleScreen.add(textField);
        titleScreen.add(closeButton);

        return titleScreen;
    }

    private JPanel createFirstTime() {
        JPanel firstTime = new JPanel();
        firstTime.setLayout(null);

        JLabel jLabel = new JLabel();
        jLabel.setBounds(200, 100, 1000, 200);
        Font labelfont = new Font("Pixel", Font.BOLD, 30);
        jLabel.setFont(labelfont);

        JTextField jTextField = new JTextField();
        jTextField.setBounds(200, 400, 1000, 50);
        jTextField.setVisible(false);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(200, 300, 200, 30);
        submitButton.setVisible(false);

        JButton submitButton2 = new JButton("Submit");
        submitButton2.setBounds(200, 300, 200, 30);
        submitButton2.setVisible(false);

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
                SlowTyper stext = new SlowTyper(jLabel, text, 200, Arrays.asList(
                        Optional.of(() -> jTextField.setVisible(true)),
                        Optional.of(() -> submitButton.setVisible(true))
                        ));
                stext.start();
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
                String text = "Hello" + uname + "What is your gender?";
                SlowTyper gender = new SlowTyper(jLabel, text, 100, Arrays.asList(
                        Optional.of(() -> submitButton2.setVisible(true)),
                        Optional.of(() -> jTextField.setText(" ")),
                        Optional.of(() -> jTextField.setVisible(true))
                ));
                gender.start();
            }
        });
        JButton done = new JButton("DONE");
        done.setBounds(100, 500, 100, 50);
        done.setVisible(false);
        submitButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    gender = jTextField.getText();
                    done.setVisible(true);
            }
        });
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player = new Player(uname,gender, level, health);
                savePlayer(player);
            }
        });

        firstTime.add(jLabel);
        firstTime.add(jTextField);
        firstTime.add(submitButton);
        firstTime.add(submitButton2);
        firstTime.add(done);

        return firstTime;
    }

    private JPanel createMainScreen(){
        JPanel mainScreen = new JPanel();
        mainScreen.setLayout(null);



        return mainScreen;
    }

    private void startProgressBar(int delay) {
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            Start();
        }
    }

    private void fadeToScreen(String screenName, int delay) {
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

    private void fadeIn() {
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

    private void setAlphaForComponents(float alpha) {
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

    public static void main(String[] args) throws IOException {


        new AdventureGame(player);
        UpdateClient.updateJar();
    }

    public void Start() {
        if (checkFirstRun()) {
            System.out.println("First time running, showing First Time screen.");
            cardLayout.show(cardPanel, "First Time");
        } else {
            System.out.println("This is not the first run.");
            // Other actions for subsequent runs
        }
    }


    public void savePlayer(Player player) {
        Gson gson = new Gson();
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            if (saveDir.mkdirs()) {
                System.out.println("Save directory created: " + SAVE_DIR);
            } else {
                System.out.println("Failed to create save directory: " + SAVE_DIR);
                return; // Exit if directory creation fails
            }
        }

        try (FileWriter writer = new FileWriter(SAVE_FILE_PATH)) {
            gson.toJson(player, writer);
            System.out.println("Player data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void Load() {
        // Implementation for loading the game
    }

    public void MainGame() {
        // Implementation for the main game logic
    }

    public static boolean checkFirstRun() {
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


}
