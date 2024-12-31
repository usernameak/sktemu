package net.sktemu.xceapi;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;
import net.sktemu.ui.EmuCanvas;

import javax.microedition.lcdui.Graphics;

public abstract class XceApiManager {
    private XceApiManager() {}

    public static void initializeLCDUI(SkvmAppInstance appInstance) {
        XDisplay.width = appInstance.getBackbufferImage().getWidth();
        XDisplay.height2 = appInstance.getBackbufferImage().getHeight();

        Toolkit.graphics = new Graphics(appInstance.getBackbufferImage());
    }
}
