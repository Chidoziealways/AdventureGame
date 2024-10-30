package net.chidozie.adventuregame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FadeUtility {

    public static void fadeOutComponents(int duration, JComponent... components) {
        Timer timer = new Timer(duration / 20, new ActionListener() {
            private float alpha = 1f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f;
                if (alpha <= 0) {
                    alpha = 0;
                    ((Timer) e.getSource()).stop();
                    for (JComponent component : components) {
                        component.setVisible(false);
                    }
                }
                setAlphaForComponents(alpha, components);
            }
        });
        timer.start();
    }

    public static void fadeInComponents(int duration, JComponent... components) {
        for (JComponent component : components) {
            component.setVisible(true);
        }

        Timer timer = new Timer(duration / 20, new ActionListener() {
            private float alpha = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1) {
                    alpha = 1;
                    ((Timer) e.getSource()).stop();
                }
                setAlphaForComponents(alpha, components);
            }
        });
        timer.start();
    }

    private static void setAlphaForComponents(float alpha, JComponent... components) {
        for (JComponent component : components) {
            component.setOpaque(false);
            component.repaint();
            component.add(new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    super.paintComponent(g);
                }
            });
        }
    }
}
