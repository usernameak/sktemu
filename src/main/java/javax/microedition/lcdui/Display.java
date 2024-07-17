package javax.microedition.lcdui;

import net.sktemu.ams.AppModel;
import net.sktemu.ui.EmuCanvas;

import javax.microedition.midlet.MIDlet;

public class Display {
    private final EmuCanvas emuCanvas = AppModel.appModelInstance.getEmuCanvas();

    private Displayable current;

    public static Display getDisplay(MIDlet midlet) {
        AppModel appModel = MIDlet.getAppModel(midlet);
        return appModel.getDisplay();
    }

    public Displayable getCurrent() {
        return current;
    }

    public void setCurrent(Displayable current) {
        this.current = current;

        _ui_doRepaint();
    }

    public void _ui_doRepaint() {
        if (current instanceof Canvas) {
            ((Canvas) current).repaint();
        }
    }
}
