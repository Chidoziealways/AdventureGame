package net.chidozie.adventuregame;

import net.chidozie.adventuregame.jpanel.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AdventureGame extends JFrame implements ActionListener, Serializable {
    private Player player;
    private JButton button;
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

        // Add the button to the panel
        panel.add(button);
        panel.add(label);

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
    public static void main(String[] args) throws FileNotFoundException{

       AdventureGame adventureGame = new AdventureGame(new Player());
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
