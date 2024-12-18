package net.chidozie.adventuregame;

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

    public SlowTyper(JTextArea label, String text, int delay) {
        this(label, text, delay, List.of(Optional.empty()));

        timer = new Timer(delay, new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                      if (index < text.length()) {
                          label.setText(label.getText() + text.charAt(index));
                          index++;
                      } else {
                          timer.stop();
                          onComplete.forEach(runnable -> runnable.ifPresent(Runnable::run));
                      }
                  }
              });
    }

    public SlowTyper(JTextArea label, String text, int delay, List<Optional<Runnable>> onComplete) {
        this.label = label;
        this.text = text;
        this.index = 0;
        this.onComplete = onComplete;

        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    label.setText(label.getText() + text.charAt(index));
                    index++;
                } else {
                    timer.stop();
                    onComplete.forEach(runnable -> runnable.ifPresent(Runnable::run));
                }
            }
        });
    }

    public SlowTyper(JLabel label, String text, int delay) {
           this.jLabel = label;
           this.text = text;
           this.index = delay;

           timer = new Timer(delay, new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent e) {
                         if (index < text.length()) {
                             label.setText(label.getText() + text.charAt(index));
                             index++;
                         } else {
                             timer.stop();
                             onComplete.forEach(runnable -> runnable.ifPresent(Runnable::run));
                         }
                     }
                 });
       }

       public SlowTyper(JLabel label, String text, int delay, List<Optional<Runnable>> onComplete) {
           this.jLabel = label;
           this.text = text;
           this.index = 0;
           this.onComplete = onComplete;

           timer = new Timer(delay, new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   if (index < text.length()) {
                       label.setText(label.getText() + text.charAt(index));
                       index++;
                   } else {
                       timer.stop();
                       onComplete.forEach(runnable -> runnable.ifPresent(Runnable::run));
                   }
               }
           });
       }

    public void start() {
        timer.start();
    }
}
