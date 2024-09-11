package javax.microedition.midlet;

import net.sktemu.ams.AppInstance;
import net.sktemu.debug.FeatureNotImplementedError;

public abstract class MIDlet {
    private final AppInstance appInstance;

    protected MIDlet() {
        appInstance = AppInstance.appInstance;
    }

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final String getAppProperty(String key) {
        return appInstance.getAppModel().getAppProperty(key);
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

    public static AppInstance getAppModel(MIDlet midlet) {
        return midlet.appInstance;
    }

    public static void startMidlet(MIDlet midlet) throws MIDletStateChangeException {
        midlet.startApp();
    }
}
