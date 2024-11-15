package com.nttdocomo.ui;

import com.nttdocomo.lang.XString;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.doja.DojaAppInstance;
import net.sktemu.debug.FeatureNotImplementedError;

public class Font {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_HEADING = 1;

    public static final int FACE_SYSTEM = 0x71000000;
    public static final int FACE_MONOSPACE = 0x72000000;
    public static final int FACE_PROPORTIONAL = 0x73000000;

    public static final int STYLE_PLAIN = 0x70100000;
    public static final int STYLE_BOLD = 0x70110000;
    public static final int STYLE_ITALIC = 0x70120000;
    public static final int STYLE_BOLDITALIC = 0x70130000;

    public static final int SIZE_SMALL = 0x70000100;
    public static final int SIZE_MEDIUM = 0x70000200;
    public static final int SIZE_LARGE = 0x70000300;
    public static final int SIZE_TINY = 0x70000400;

    private static Font defaultFont = null;

    private static final Font[] SIZED_FONTS = new Font[] {
            new Font(new java.awt.Font("MS Gothic", java.awt.Font.PLAIN, 12)),
            new Font(new java.awt.Font("MS Gothic", java.awt.Font.PLAIN, 18)),
            new Font(new java.awt.Font("MS Gothic", java.awt.Font.PLAIN, 22)),
            new Font(new java.awt.Font("MS Gothic", java.awt.Font.PLAIN, 9)),
    };

    java.awt.Font awtFont;
    private java.awt.Graphics awtGraphics;

    protected Font(java.awt.Font awtFont) {
        this.awtFont = awtFont;
        this.awtGraphics = ((DojaAppInstance) AppInstance.appInstance).getDojaGraphics().getAwtGraphics();
    }

    public static Font getFont(int type) {
        int size = (type & 0xF00) >> 8;
        if (size == 0 || size > 4) {
            size = 4;
        }
        return SIZED_FONTS[size - 1];
    }

    public static Font getFont(int type, int fontSize) {
        return getFont(type);
    }

    private static final int[] supportedFontSizes = {12};

    public static int[] getSupportedFontSizes() {
        return supportedFontSizes;
    }

    public static Font getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = getFont(FACE_SYSTEM | SIZE_TINY | STYLE_PLAIN);
        }
        return defaultFont;
    }

    public static void setDefaultFont(Font f) {
        defaultFont = f;
    }

    public int getAscent() {
        return awtGraphics.getFontMetrics(awtFont).getAscent();
    }

    public int getDescent() {
        return awtGraphics.getFontMetrics(awtFont).getDescent();
    }

    public int getHeight() {
        return awtGraphics.getFontMetrics(awtFont).getHeight();
    }

    private int stringWidth(String str, int off, int len) {
        return awtGraphics.getFontMetrics(awtFont).charsWidth(str.toCharArray(), off, len);
    }

    public int stringWidth(String str) {
        return stringWidth(str, 0, str.length());
    }

    private int getBBoxWidth(String str, int off, int len) {
        throw new FeatureNotImplementedError("Font::getBBoxWidth");
    }

    public int getBBoxWidth(String str) {
        return getBBoxWidth(str, 0, str.length());
    }

    public int getBBoxHeight(String str) {
        throw new FeatureNotImplementedError("Font::getBBoxHeight");
    }

    public int getLineBreak(String str, int off, int len, int width) {
        throw new FeatureNotImplementedError("Font::getLineBreak");
    }

    public int getBBoxWidth(XString xStr, int off, int len) {
        return getBBoxWidth(XString.getStringContents(xStr), off, len);
    }

    public int getBBoxWidth(XString xStr) {
        return getBBoxWidth(xStr, 0, xStr.length());
    }

    public int getBBoxHeight(XString xStr) {
        return getBBoxHeight(XString.getStringContents(xStr));
    }

    public int stringWidth(XString xStr) {
        return stringWidth(XString.getStringContents(xStr));
    }

    public int stringWidth(XString xStr, int off, int len) {
        return stringWidth(XString.getStringContents(xStr), off, len);
    }

    public int getLineBreak(XString xStr, int off, int len, int width) {
        return getLineBreak(XString.getStringContents(xStr), off, len, width);
    }
}
