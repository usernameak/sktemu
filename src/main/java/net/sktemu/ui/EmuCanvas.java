package net.sktemu.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EmuCanvas extends JComponent {
    private final BufferedImage bufferedImage;

    public EmuCanvas(int width, int height) {
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width * 2, height * 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        synchronized (bufferedImage) {
            Dimension size = getSize();
            g.drawImage(bufferedImage,
                    0, 0, size.width, size.height,
                    0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(),
                    this);
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
