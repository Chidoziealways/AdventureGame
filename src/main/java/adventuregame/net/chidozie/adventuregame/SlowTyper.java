package adventuregame.net.chidozie.adventuregame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

class SlowTyper {
    private JTextArea label;
    private Timer timer;
    private String text;
    private int index;
    private List<Optional<Runnable>> onComplete;
    private JLabel jLabel;



    public void type(JTextArea label, String text, int delay, List<Runnable> onComplete) {


        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    label.setText(label.getText() + text.charAt(index));
                    index++;
                } else {
                    timer.stop();
                    onComplete.forEach(Runnable::run);
                }
            }
        });
    }

    public void type(JTextArea label, String text, int delay) {

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    label.setText(label.getText() + text.charAt(index));
                    index++;
                } else {
                    timer.stop();
                }
            }
        });

        timer.start();
    }

    public void type(JLabel label, String text, int delay, List<Runnable> onComplete) {


        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    label.setText(label.getText() + text.charAt(index));
                    index++;
                } else {
                    timer.stop();
                    onComplete.forEach(Runnable::run);
                }
            }
        });
    }

    public void type(JLabel label, String text, int delay) {

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    label.setText(label.getText() + text.charAt(index));
                    index++;
                } else {
                    timer.stop();
                }
            }
        });

        timer.start();
    }



    public void start() {
        timer.start();
    }

    public SlowTyper(){

    }
}
