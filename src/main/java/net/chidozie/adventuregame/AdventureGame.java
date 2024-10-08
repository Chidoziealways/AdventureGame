package net.chidozie.adventuregame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AdventureGame extends JFrame implements ActionListener {
    private JButton button;
    private Player player;

    public AdventureGame(Player player){
        // Set the title of the JFrame
        setTitle("Button Click Example");

        // Set the size of the JFrame
        setSize(400, 300);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel to hold the components
        JPanel panel = new JPanel();

        // Create a JButton
        button = new JButton("Click Me!");
        button.addActionListener(this); // Add ActionListener to the button

        // Add the button to the panel
        panel.add(button);

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
    }
    public void Save(AdventureGame adventureGame) throws FileNotFoundException {
       try{
           FileOutputStream fileOutputStream = new FileOutputStream("BinarySaves/Player.svr");
           FileOutputStream fileOutputStream1 = new FileOutputStream("BinarySaves/Game.svr");
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
           ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(fileOutputStream1);
           objectOutputStream.writeObject(adventureGame);
           objectOutputStream1.writeObject(player);
       }catch(IOException e){
           System.out.println("THere was an Exception" + e);
       }
    }
    public void Load(){

    }public void MainGame(){

    }
}
