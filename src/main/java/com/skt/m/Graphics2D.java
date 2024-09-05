package com.skt.m;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import net.sktemu.debug.FeatureNotImplementedError;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.awt.image.BufferedImage;

public class Graphics2D {
    private final Graphics g;

    public Graphics2D(Graphics g) {
        this.g = g;
    }

    public static Graphics2D getGraphics2D(Graphics g) {
        return new Graphics2D(g);
    }

    public void drawImage(int tx, int ty, Image src, int sx, int sy, int sw, int sh, int mode) {
        BufferedImage bufferedImage = Image.getBufferedImage(src);
        Graphics.getAWTGraphics(g).drawImage(
                bufferedImage,
                tx, ty, tx + sw, ty + sh,
                sx, sy, sx + sw, sy + sh,
                null);
    }

    public static Image captureLCD(int x, int y, int w, int h) {
        Image image = Image.createImage(w, h);
        BufferedImage bufferedImage = Graphics.getImage(Toolkit.graphics);
        Graphics.getAWTGraphics(image.getGraphics()).drawImage(
                bufferedImage,
                0, 0, w, h,
                x, y, x + w, x + h,
                null);
        return image;
    }

    public static Image createMaskableImage(int width, int height) {
        return Image.createImage(width, height);
    }
}
