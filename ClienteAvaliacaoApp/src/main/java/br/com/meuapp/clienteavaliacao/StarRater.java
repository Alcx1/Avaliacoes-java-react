package br.com.meuapp.clienteavaliacao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StarRater extends JPanel {
    private int starCount;
    private int rating;
    private StarListener listener;

    public StarRater(int starCount, int rating) {
        this.starCount = starCount;
        this.rating = rating;
        this.listener = new StarListener();
        this.setPreferredSize(new Dimension(starCount * 32, 32));
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
    }

    public int getRating() {
        return rating;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < starCount; i++) {
            if (i < rating) {
                g.drawImage(StarImages.FILLED_STAR, i * 32, 0, null);
            } else {
                g.drawImage(StarImages.EMPTY_STAR, i * 32, 0, null);
            }
        }
    }

    private class StarListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            rating = (x / 32) + 1;
            repaint();
        }
    }
}