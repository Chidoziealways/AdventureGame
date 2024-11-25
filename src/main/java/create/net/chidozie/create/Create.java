package create.net.chidozie.create;

import javax.swing.*;
import java.io.IOException;

public class Create extends JFrame{
    public Create() throws IOException, InterruptedException {
        System.out.println("Setting up JFrame...");
        setTitle("Create");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("JFrame setup complete.");

        setVisible(true);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Create();
    }
}
