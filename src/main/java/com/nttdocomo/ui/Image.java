package com.nttdocomo.ui;

import net.sktemu.doja.ui.ImageImpl;

import java.awt.image.BufferedImage;

public abstract class Image {
    private int transparentColor;
    private int alpha;

    protected Image() {

    }

    public static Image createImage(int width, int height) {
        return new ImageImpl(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
    }

    public static Image createImage(int width, int height, int[] data, int off) {
        Image image = createImage(width, height);
        image.getGraphics().setRGBPixels(0, 0, width, height, data, off);
        return image;
    }

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public abstract void dispose();

    public Graphics getGraphics() {
        throw new UnsupportedOperationException();
    }

    public void setTransparentColor(int color) {
        transparentColor = color;
    }

    public int getTransparentColor() {
        return transparentColor;
    }

    public void setTransparentEnabled(boolean enabled) {

    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return alpha;
    }
}
