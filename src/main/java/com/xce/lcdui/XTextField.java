package com.xce.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class XTextField {
    private String text;
    private int maxSize;
    private int constraints;
    private boolean hasFocus;

    private int x, y, width, height;

    public XTextField(String text, int maxSize, int constraints, Canvas canvas) {
        if (text == null) {
            text = "";
        }
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getText() {
        return text;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public void setFocus(boolean focus) {
        hasFocus = focus;
    }

    public void inputChar(char key) {

    }

    public void keyPressed(int keyCode) {

    }

    public void keyReleased(int keyCode) {

    }

    public void keyRepeated(int keyCode) {

    }

    public void paint(Graphics g) {
        g.setColor(0xFFFFFFFF);
        g.fillRect(x, y, width, height);
        g.setColor(0xFF000000);
        g.drawRect(x, y, width, height);

        g.drawString(text, x + 1, y + 1, Graphics.LEFT | Graphics.TOP);
    }

    public void repaint() {
        // TODO:
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
    }
}
