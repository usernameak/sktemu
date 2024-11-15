package com.nttdocomo.lang;

public final class XString extends XObject {
    private String str;

    public XString(String str) {
        this.str = str;
    }

    public int length() {
        return str.length();
    }

    public XString concat(XString xStr) {
        return new XString(str + xStr.str);
    }

    public static String getStringContents(XString xStr) {
        return xStr.str;
    }
}