package net.sktemu.doja.ui;

import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.Image;

import java.awt.image.BufferedImage;

public class ImageImpl extends Image {
    private final BufferedImage image;
    private final Graphics graphics;

    public ImageImpl(BufferedImage bufferedImage) {
        this.image = bufferedImage;
        this.graphics = new Graphics(bufferedImage);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }
}
