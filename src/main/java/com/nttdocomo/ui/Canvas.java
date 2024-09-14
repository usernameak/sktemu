package com.nttdocomo.ui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.doja.DojaAppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;
import net.sktemu.debug.FeatureNotImplementedError;

public abstract class Canvas {
    public static final int IME_COMMITTED = 0;
    public static final int IME_CANCELED = 1;

    public Graphics getGraphics() {
        throw new FeatureNotImplementedError("feature not impl");
    }

    public abstract void paint(Graphics g);

    private void serviceRepaints() {
        DojaAppInstance app = (DojaAppInstance) AppInstance.appInstance;

    }

    public void repaint() {
        AppInstance.appInstance.runOnAppThread(this::serviceRepaints);
    }

    public void repaint(int x, int y, int width, int height) {
        repaint();
    }

    public void processEvent(int type, int param) {

    }

    public int getKeypadState() {
        return 0;
    }

    public int getKeypadState(int group) {
        return 0;
    }

    public void imeOn(String text, int displayMode, int inputMode) {
        imeOn(text, displayMode, inputMode, Integer.MAX_VALUE);
    }

    public void imeOn(String text, int displayMode, int inputMode, int inputSize) {

    }

    public void processIMEEvent(int type, String text) {

    }
}
