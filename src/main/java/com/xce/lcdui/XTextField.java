package com.xce.lcdui;

import net.sktemu.input.HangulInputMethod;
import net.sktemu.input.IInputTarget;
import net.sktemu.input.InputMethod;
import net.sktemu.input.LatinInputMethod;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class XTextField implements IInputTarget {
    private int cursorPos = 0;
    private String text;
    private int maxSize;
    private int constraints;
    private boolean hasFocus;

    private InputMethod ime = new LatinInputMethod();

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
        if (!hasFocus) return;

        if (key == '\b') {
            if (cursorPos == 0) return;

            text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
            cursorPos--;
            return;
        }

        text = text.substring(0, cursorPos) + key + text.substring(cursorPos);
        cursorPos++;
    }

    public void keyPressed(int keyCode) {
        ime.keyPress(this, keyCode);
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

        if (cursorPos > text.length()) {
            cursorPos = text.length();
        }
        ime.startNewCharacter(this);
    }

    @Override
    public void generateInputCharacter(char ch) {
        inputChar(ch);
    }

    @Override
    public void discardInputCharacter() {
        inputChar('\b');
    }
}
