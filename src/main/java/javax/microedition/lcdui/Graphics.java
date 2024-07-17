package javax.microedition.lcdui;

import net.sktemu.debug.FeatureNotImplementedError;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphics {
    public static final int DOTTED = 1;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BASELINE = 64;
    public static final int BOTTOM = 32;
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;

    private final BufferedImage bufferedImage;
    private final Graphics2D graphics2D;
    private Rectangle clipRect;

    private int translateX, translateY;

    public Graphics(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.graphics2D = bufferedImage.createGraphics();
        this.clipRect = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        java.awt.Font font = new java.awt.Font("Gulim", java.awt.Font.PLAIN, 12);
        graphics2D.setFont(font);
    }

    public void translate(int x, int y) {
        graphics2D.translate(x, y);
        translateX += x;
        translateY += y;
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }

    public int getColor() {
        return graphics2D.getColor().getRGB();
    }

    public int getRedComponent() {
        throw new FeatureNotImplementedError("Graphics::getRedComponent");
    }

    public int getGreenComponent() {
        throw new FeatureNotImplementedError("Graphics::getGreenComponent");
    }

    public int getBlueComponent() {
        throw new FeatureNotImplementedError("Graphics::getBlueComponent");
    }

    public int getGrayScale() {
        throw new FeatureNotImplementedError("Graphics::getGrayScale");
    }

    public void setColor(int red, int green, int blue) {
        throw new FeatureNotImplementedError("Graphics::setColor");

    }

    public void setColor(int rgb) {
        graphics2D.setColor(new Color(rgb));
    }

    public void setGrayScale(int value) {
        setColor(value, value, value);
    }

    public Font getFont() {
        throw new FeatureNotImplementedError("Graphics::getFont");
    }

    public void setStrokeStyle(int style) {
        throw new FeatureNotImplementedError("Graphics::setStrokeStyle");
    }

    public int getStrokeStyle() {
        throw new FeatureNotImplementedError("Graphics::getStrokeStyle");
    }

    public void setFont(Font font) {
        throw new FeatureNotImplementedError("Graphics::setFont");
    }

    public int getClipX() {
        return clipRect.x;
    }

    public int getClipY() {
        return clipRect.y;
    }

    public int getClipWidth() {
        return clipRect.width;
    }

    public int getClipHeight() {
        return clipRect.height;
    }

    public void clipRect(int x, int y, int width, int height) {
        clipRect = clipRect.intersection(new Rectangle(x, y, width, height));
        graphics2D.setClip(clipRect);
    }

    public void setClip(int x, int y, int width, int height) {
        clipRect = new Rectangle(x, y, width, height);
        graphics2D.setClip(clipRect);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics2D.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        graphics2D.fillRect(x, y, width, height);
    }

    public void drawRect(int x, int y, int width, int height) {
        graphics2D.drawRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new FeatureNotImplementedError("Graphics::drawRoundRect");
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        throw new FeatureNotImplementedError("Graphics::fillRoundRect");
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new FeatureNotImplementedError("Graphics::fillArc");
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new FeatureNotImplementedError("Graphics::drawArc");
    }

    public void drawString(String str, int x, int y, int anchor) {
        drawSubstring(str, 0, str.length(), x, y, anchor);
    }

    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        drawChars(str.toCharArray(), offset, len, x, y, anchor);
    }

    public void drawChar(char character, int x, int y, int anchor) {
        throw new FeatureNotImplementedError("Graphics::drawChar");
    }

    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int width = fontMetrics.charsWidth(data, offset, length);
        if ((anchor & HCENTER) == HCENTER) {
            x -= width / 2;
        } else if ((anchor & RIGHT) == RIGHT) {
            x -= width;
        }
        if ((anchor & TOP) == TOP) {
            y += fontMetrics.getAscent();
        } else if ((anchor & BOTTOM) == BOTTOM) {
            y -= fontMetrics.getDescent();
        }
        graphics2D.drawChars(data, offset, length, x, y);
    }

    public void drawImage(Image img, int x, int y, int anchor) {
        BufferedImage bimg = Image.getBufferedImage(img);
        if ((anchor & HCENTER) == HCENTER) {
            x -= bimg.getWidth() / 2;
        } else if ((anchor & RIGHT) == RIGHT) {
            x -= bimg.getWidth();
        }
        if ((anchor & VCENTER) == VCENTER) {
            y -= bimg.getHeight() / 2;
        } else if ((anchor & BOTTOM) == BOTTOM) {
            y -= bimg.getHeight();
        }
        graphics2D.drawImage(bimg, x, y, null);
    }

    public static Graphics2D getAWTGraphics(Graphics g) {
        return g.graphics2D;
    }

    // XCE API
    public void reset() {
        System.err.println("Graphics::reset() not impl");
    }
}
