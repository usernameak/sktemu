package com.nttdocomo.ui;

import com.nttdocomo.lang.XString;
import net.sktemu.debug.FeatureNotImplementedError;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphics {
    public static final int BLACK = 0;
    public static final int BLUE = 1;
    public static final int LIME = 2;
    public static final int AQUA = 3;
    public static final int RED = 4;
    public static final int FUCHSIA = 5;
    public static final int YELLOW = 6;
    public static final int WHITE = 7;
    public static final int GRAY = 8;
    public static final int NAVY = 9;
    public static final int GREEN = 10;
    public static final int TEAL = 11;
    public static final int MAROON = 12;
    public static final int PURPLE = 13;
    public static final int OLIVE = 14;
    public static final int SILVER = 15;

    public static final int FLIP_NONE = 0;
    public static final int FLIP_HORIZONTAL = 1;
    public static final int FLIP_VERTICAL = 2;
    public static final int FLIP_ROTATE = 3;
    public static final int FLIP_ROTATE_LEFT = 4;
    public static final int FLIP_ROTATE_RIGHT = 5;
    public static final int FLIP_ROTATE_RIGHT_HORIZONTAL = 6;
    public static final int FLIP_ROTATE_RIGHT_VERTICAL = 7;

    private static final int[] NAMED_COLOR_TABLE = new int[]{
            0xFF000000,// BLACK = 0
            0xFF0000FF,// BLUE = 1
            0xFF00FF00,// LIME = 2
            0xFF00FFFF,// AQUA = 3
            0xFFFF0000,// RED = 4
            0xFFFF00FF,// FUCHSIA = 5
            0xFFFFFF00,// YELLOW = 6
            0xFFFFFFFF,// WHITE = 7
            0xFF808080,// GRAY = 8
            0xFF000080,// NAVY = 9
            0xFF008000,// GREEN = 10
            0xFF008080,// TEAL = 11
            0xFF800000,// MAROON = 12
            0xFF800080,// PURPLE = 13
            0xFF808000,// OLIVE = 14
            0xFFC0C0C0,// SILVER = 15
    };

    private final BufferedImage bufferedImage;
    private final Graphics2D graphics2D;

    public Graphics(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.graphics2D = bufferedImage.createGraphics();

        this.graphics2D.setColor(Color.WHITE);

        // graphics2D.setFont(Font.getDefaultFont().getAWTFont());
    }

    public void clearClip() {
    }

    public void clearRect(int x, int y, int width, int height) {
    }

    public void clipRect(int x, int y, int width, int height) {
    }

    public Graphics copy() {
        return null;
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    public void dispose() {
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    public void drawChars(char[] data, int x, int y, int off, int len) {
        graphics2D.drawChars(data, off, len, x, y);
    }

    public void drawImage(Image image, int[] matrix, int sx, int sy, int width, int height) {
    }

    public void drawImage(Image image, int[] matrix) {
    }

    public void drawImage(Image image, int x, int y) {
    }

    public void drawImage(Image var1, int dx, int dy, int sx, int sy, int width, int height) {
    }

    /*
    public void drawImageMap(ImageMap map, int x, int y) {
    }*/

    public void drawLine(int x1, int y1, int x2, int y2) {
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int offset, int count) {
    }

    public void drawRect(int x, int y, int width, int height) {
    }

    public void drawScaledImage(Image image, int dx, int dy, int width, int height, int sx, int sy, int swidth, int sheight) {

    }

    /*
    public void drawSpriteSet(SpriteSet sprites) {
    }

    public void drawSpriteSet(SpriteSet sprites, int offset, int count) {
    }*/

    private void drawString(String str, int x, int y, int off, int len) {
        drawChars(str.toCharArray(), x, y, off, len);
    }

    public void drawString(String str, int x, int y) {
        drawString(str, x, y, 0, str.length());
    }

    public void drawString(XString xStr, int x, int y) {
        drawString(XString.getStringContents(xStr), x, y);
    }

    public void drawString(XString xStr, int x, int y, int off, int len) {
        drawString(XString.getStringContents(xStr), x, y, off, len);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int offset, int count) {
    }

    public void fillRect(int x, int y, int width, int height) {
    }

    public static int getColorOfName(int name) {
        if (name < 0 || name > NAMED_COLOR_TABLE.length) {
            throw new IllegalArgumentException("getColorName out of range");
        }
        return NAMED_COLOR_TABLE[name];
    }

    public static int getColorOfRGB(int r, int g, int b) {
        return getColorOfRGB(r, g, b, 255);
    }

    public static int getColorOfRGB(int r, int g, int b, int a) {
        if (r < 0 || r >= 256 || g < 0 || g >= 256 || b < 0 || b >= 256) {
            throw new IllegalArgumentException("RGB out of range");
        }
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int getPixel(int x, int y) {
        throw new FeatureNotImplementedError("Graphics::getPixel");
    }

    public int[] getPixels(int x, int y, int width, int height, int[] pixels, int off) {
        throw new FeatureNotImplementedError("Graphics::getPixels");
    }

    public int getRGBPixel(int x, int y) {
        throw new FeatureNotImplementedError("Graphics::getRGBPixel");
    }

    public int[] getRGBPixels(int x, int y, int width, int height, int[] pixels, int off) {
        throw new FeatureNotImplementedError("Graphics::getRGBPixels");
    }

    public void lock() {
    }

    public void setClip(int x, int y, int width, int height) {
    }

    public void setColor(int c) {
    }

    public void setFlipMode(int flipmode) {
    }

    public void setFont(Font font) {
        graphics2D.setFont(font.awtFont);
    }

    public void setOrigin(int x, int y) {
    }

    public void setPictoColorEnabled(boolean b) {
    }

    public void setPixel(int x, int y) {
    }

    public void setPixel(int x, int y, int color) {
    }

    public void setPixels(int x, int y, int width, int height, int[] pixels, int off) {
    }

    public void setRGBPixel(int x, int y, int pixel) {
    }

    public void setRGBPixels(int x, int y, int width, int height, int[] pixels, int off) {
    }

    public void unlock(boolean forced) {
    }

    public Graphics2D getAwtGraphics() {
        return graphics2D;
    }
}
