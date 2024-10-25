package net.chidozie.adventuregame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FadePanel extends JPanel {
    private float alpha = 1f; // Fully opaque

    public FadePanel() {
        setBackground(Color.BLACK); // Set a background color for visibility
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
