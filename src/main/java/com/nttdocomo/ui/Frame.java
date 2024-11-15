package com.nttdocomo.ui;

import net.sktemu.ams.AppInstance;

public abstract class Frame {
    public final int getWidth() {
        return AppInstance.appInstance.getBackbufferImage().getWidth();
    }

    public final int getHeight() {
        return AppInstance.appInstance.getBackbufferImage().getHeight();
    }

    public void setBackground(int c) {}

    public void setSoftLabel(int key, String label) {

    }

    public void setSoftLabelVisible(boolean b) {

    }
}
