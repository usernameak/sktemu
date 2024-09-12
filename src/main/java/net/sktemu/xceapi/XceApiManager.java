package net.sktemu.xceapi;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import net.sktemu.ams.AppInstance;
import net.sktemu.ui.EmuCanvas;

public abstract class XceApiManager {
    private XceApiManager() {}

    public static void initializeLCDUI(AppInstance appInstance) {
        XDisplay.width = appInstance.getBackbufferImage().getWidth();
        XDisplay.height2 = appInstance.getBackbufferImage().getHeight();

        Toolkit.graphics = appInstance.getMidpGraphics();
    }
}
