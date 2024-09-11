package javax.microedition.lcdui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ui.EmuCanvas;

import javax.microedition.midlet.MIDlet;

public class Display {
    private final EmuCanvas emuCanvas = AppInstance.appInstance.getEmuCanvas();

    private Displayable current;

    public static Display getDisplay(MIDlet midlet) {
        AppInstance appInstance = MIDlet.getAppModel(midlet);
        return appInstance.getDisplay();
    }

    public Displayable getCurrent() {
        return current;
    }

    public void setCurrent(Displayable current) {
        if (this.current != current) {
            if (this.current instanceof Canvas) {
                Canvas.setCanvasShown((Canvas) this.current, false);
            }
            if (current instanceof Canvas) {
                Canvas.setCanvasShown((Canvas) current, true);
            }
        }

        this.current = current;

        _ui_doRepaint();
    }

    public void _ui_doRepaint() {
        if (current instanceof Canvas) {
            ((Canvas) current).repaint();
        }
    }
}
