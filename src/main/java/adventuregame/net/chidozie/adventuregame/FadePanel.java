package adventuregame.net.chidozie.adventuregame;

import javax.swing.*;
import java.awt.*;

public class FadePanel extends JPanel {
    private float alpha = 1f; // Fully opaque

    public FadePanel() {
        setOpaque(false); // Make sure the panel is transparent
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
