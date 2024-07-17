package net.sktemu.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EmuCanvas extends JComponent {
    private final BufferedImage bufferedImage;
    private final javax.microedition.lcdui.Graphics midpGraphics;

    public EmuCanvas(int width, int height) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));

        this.midpGraphics = new javax.microedition.lcdui.Graphics(bufferedImage);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bufferedImage, 0, 0, this);
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public javax.microedition.lcdui.Graphics getMidpGraphics() {
        return midpGraphics;
    }
}
