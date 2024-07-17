package com.xce.lcdui;

import net.sktemu.ams.AppModel;

public class XDisplay {
    public static int width;
    public static int height2;

    public static void refresh(int x, int y, int width, int height) {
        AppModel.appModelInstance.runOnUiThread(() -> {
            AppModel.appModelInstance.getEmuCanvas().repaint();
        });
    }
}
