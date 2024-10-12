package net.chidozie.adventuregame;

import net.chidozie.adventuregame.jpanel.ImagePanel;
import updater.UpdateClient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AdventureGame extends JFrame implements ActionListener, Serializable {
    private Player player;
    private JButton button;
    public static JTextField textField;
    public AdventureGame(Player player){
        // Set the title of the JFrame
        setTitle("Adventure Game Prologue");

        // Set the size of the JFrame
        setSize(400, 300);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set the Background
        ImageIcon icon = new ImageIcon("");


        // Create a JPanel to hold the components
        ImagePanel panel = new ImagePanel(icon.getImage());

        // Create a JButton
        button = new JButton("Start");
        JLabel label = new JLabel("HEllo");
        label.setSize(10, 10);
        button.setBounds(180, 80, 1000, 100);
        button.addActionListener(this); // Add ActionListener to the button
        button.setBackground(Color.BLACK);
        textField = new JTextField(20); //
        textField.setBounds(180, 500, 80, 90);
        JButton button1 = new JButton("SUBMIT");
        button1.setBounds(180, 80, 100, 100);
        button.setVisible(false);


        // Add the button to the panel
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

        // Add the panel to the JFrame
        add(panel);




        // Make the JFrame visible
        setVisible(true);
        this.player = player;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button click event
        if (e.getSource() == button) {
                Start();

              }
    }
    public static void main(String[] args) throws IOException {
        AdventureGame adventureGame = new AdventureGame(new Player());
        UpdateClient.updateJar();
    }
    public void Start(){
        System.out.println("hello");
        button.setVisible(false);
        button.setText("Hello");
        button.setVisible(true);
    }
    public void Save(AdventureGame adventureGame) throws FileNotFoundException {
       try{
           FileOutputStream fileOutputStream = new FileOutputStream("BinarySaves/Game.svr");
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
           objectOutputStream.writeObject(adventureGame);
       }catch(IOException e){
           System.out.println("THere was an Exception" + e);
       }
    }
    public void Load(){

    }public void MainGame(){

    }
}
