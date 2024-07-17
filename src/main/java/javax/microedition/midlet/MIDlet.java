package javax.microedition.midlet;

import net.sktemu.ams.AmsClassLoader;
import net.sktemu.ams.AppModel;
import net.sktemu.debug.FeatureNotImplementedError;

public abstract class MIDlet {
    private final AppModel appModel;

    protected MIDlet() {
        appModel = AppModel.appModelInstance;
    }

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final String getAppProperty(String key) {
        return appModel.getAppProperty(key);
    }

    public final void notifyDestroyed() {
        throw new FeatureNotImplementedError("MIDlet::notifyDestroyed");
    }

    public final void notifyPaused() {
        throw new FeatureNotImplementedError("MIDlet::notifyPaused");
    }

    public final void resumeRequest() {
        throw new FeatureNotImplementedError("MIDlet::resumeRequest");
    }

    public static AppModel getAppModel(MIDlet midlet) {
        return midlet.appModel;
    }

    public static void startMidlet(MIDlet midlet) throws MIDletStateChangeException {
        midlet.startApp();
    }
}
