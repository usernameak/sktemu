package net.sktemu.xceapi;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import net.sktemu.ams.AppInstance;
import net.sktemu.ui.EmuCanvas;

public abstract class XceApiManager {
    private XceApiManager() {}

    public static void initializeLCDUI(AppInstance appInstance) {
        EmuCanvas emuCanvas = appInstance.getEmuCanvas();

        XDisplay.width = emuCanvas.getBufferedImage().getWidth();
        XDisplay.height2 = emuCanvas.getBufferedImage().getHeight();

        Toolkit.graphics = emuCanvas.getMidpGraphics();
    }
}
