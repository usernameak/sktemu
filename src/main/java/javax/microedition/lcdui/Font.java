package javax.microedition.lcdui;

import com.xce.lcdui.Toolkit;
import net.sktemu.debug.FeatureNotImplementedError;

public class Font {
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_UNDERLINED = 4;

    public static final int SIZE_SMALL = 8;
    public static final int SIZE_MEDIUM = 0;
    public static final int SIZE_LARGE = 16;

    public static final int FACE_SYSTEM = 0;
    public static final int FACE_MONOSPACE = 32;
    public static final int FACE_PROPORTIONAL = 64;

    public static final int FONT_STATIC_TEXT = 0;
    public static final int FONT_INPUT_TEXT = 1;

    private static final Font DEFAULT = new Font();

    private final java.awt.Font font = new java.awt.Font("Gulim", java.awt.Font.PLAIN, 12);

    public java.awt.Font getAWTFont() {
        return font;
    }

    public static Font getDefaultFont() {
        return DEFAULT;
    }

    public static Font getFont(int face, int style, int size) {
        return DEFAULT;
    }

    public int getStyle() {
        return STYLE_PLAIN;
    }

    public int getSize() {
        return SIZE_MEDIUM;
    }

    public int getFace() {
        return FACE_SYSTEM;
    }

    public boolean isPlain() {
        return getStyle() == STYLE_PLAIN;
    }

    public boolean isBold() {
        return (getStyle() & STYLE_BOLD) == STYLE_BOLD;
    }

    public boolean isItalic() {
        return (getStyle() & STYLE_ITALIC) == STYLE_ITALIC;
    }

    public boolean isUnderlined() {
        return (getStyle() & STYLE_UNDERLINED) == STYLE_UNDERLINED;
    }

    public int getHeight() {
        // TODO: this will not work with multiple fonts

        return Graphics.getAWTGraphics(Toolkit.graphics).getFontMetrics().getHeight();
    }

    public int getBaselinePosition() {
        throw new FeatureNotImplementedError("Font::getBaselinePosition");
    }

    public int charWidth(char ch) {
        return Graphics.getAWTGraphics(Toolkit.graphics).getFontMetrics().charWidth(ch);
    }

    public int charsWidth(char[] ch, int offset, int length) {
        return Graphics.getAWTGraphics(Toolkit.graphics).getFontMetrics().charsWidth(ch, offset, length);
    }

    public int stringWidth(String str) {
        return substringWidth(str, 0, str.length());
    }

    public int substringWidth(String str, int offset, int len) {
        return charsWidth(str.toCharArray(), offset, len);
    }
}
