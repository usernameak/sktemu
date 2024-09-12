package com.xce.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class XTextField {
    private String text;
    private int maxSize;
    private int constraints;
    private boolean hasFocus;

    public XTextField(String text, int maxSize, int constraints, Canvas canvas) {
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

    }

    public void repaint() {

    }

    public void setBounds(int x, int y, int width, int height) {

    }

    public void setText(String s) {
        this.text = s;
    }
}
