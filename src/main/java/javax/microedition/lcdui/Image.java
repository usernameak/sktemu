package javax.microedition.lcdui;

import net.sktemu.ams.AmsResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Image {
    private final BufferedImage image;
    private boolean mutable;

    private Image(boolean mutable, BufferedImage bufferedImage) {
        this.image = bufferedImage;
        this.mutable = mutable;
    }

    public static Image createImage(int width, int height) {
        return new Image(true, new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
    }

    public static Image createImage(Image source) {
        return new Image(false, copyImage(source.image));
    }

    public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
        if (transform != 0) {
            throw new UnsupportedOperationException();
        }
        Image result = createImage(width, height);
        java.awt.Graphics2D g = result.image.createGraphics();
        g.drawImage(
                image.image,
                0, 0,
                width, height,
                x, y,
                x + width, x + height,
                null
        );
        result.mutable = false;
        return image;
    }

    public static Image createImage(String name) throws IOException {
        try {
            try (InputStream stream = AmsResourceManager.getResourceAsStream(Image.class, name)) {
                if (stream == null) {
                    return null;
                }

                BufferedImage image = ImageIO.read(stream);
                if (image == null) {
                    System.err.println("ImageIO.read returned null on " + name);
                    return null;
                }

                return new Image(false, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData, imageOffset, imageLength);
        BufferedImage image = null;
        try {
            image = ImageIO.read(bais);
            if (image == null) {
                System.err.println("ImageIO.read returned null on byte array");
                return null;
            }

            return new Image(false, image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage copyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public Graphics getGraphics() {
        if (!mutable) {
            throw new IllegalStateException("Image::getGraphics() - image is immutable");
        }
        return new Graphics(image);
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public boolean isMutable() {
        return mutable;
    }

    public static BufferedImage getBufferedImage(Image image) {
        return image.image;
    }
}
