package net.sktemu.xceapi;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import net.sktemu.ams.AppModel;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.ui.EmuUIFrame;

public abstract class XceApiManager {
    private XceApiManager() {}

    public static void initializeLCDUI(AppModel appModel) {
        EmuCanvas emuCanvas = appModel.getEmuCanvas();

        XDisplay.width = emuCanvas.getBufferedImage().getWidth();
        XDisplay.height2 = emuCanvas.getBufferedImage().getHeight();

        Toolkit.graphics = emuCanvas.getMidpGraphics();
    }
}
