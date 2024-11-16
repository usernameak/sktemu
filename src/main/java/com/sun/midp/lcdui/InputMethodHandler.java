package com.sun.midp.lcdui;

public class InputMethodHandler {
    private static final InputMethodHandler instance = new InputMethodHandler();

    private int inputMode = 1;

    public static InputMethodHandler getInputMethodHandler() {
        return instance;
    }

    public void switchInputMode(int mode) {
        inputMode = mode;
    }

    public int getInputMode() {
        return inputMode;
    }
}
