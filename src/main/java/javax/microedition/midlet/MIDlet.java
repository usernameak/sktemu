package javax.microedition.midlet;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;
import net.sktemu.ams.skvm.SkvmAppModel;
import net.sktemu.ams.skvm.applet.IApplet;

public abstract class MIDlet implements IApplet {
    private final SkvmAppInstance appInstance;

    protected MIDlet() {
        appInstance = (SkvmAppInstance) AppInstance.appInstance;
    }

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final String getAppProperty(String key) {
        return ((SkvmAppModel) appInstance.getAppModel()).getAppProperty(key);
    }

    public final void notifyDestroyed() {
        appInstance.onShutdown();
    }

    public final void notifyPaused() {
        pauseApp();
    }

    public final void resumeRequest() {
        try {
            startApp();
        } catch (MIDletStateChangeException e) {
            // i guess...?
            e.printStackTrace();
        }
    }

    public static SkvmAppInstance getAppModel(MIDlet midlet) {
        return midlet.appInstance;
    }

    public static void startMidlet(MIDlet midlet) throws MIDletStateChangeException {
        midlet.startApp();
    }

    public static void destroyMidlet(MIDlet midlet) throws MIDletStateChangeException {
        midlet.destroyApp(false);
    }
}
