package org.kwis.msp.lcdui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;
import net.sktemu.ams.skvm.SkvmAppModel;
import net.sktemu.ams.skvm.applet.IApplet;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public abstract class Jlet implements IApplet {
    private final SkvmAppInstance appInstance;
    private final EventQueue eventQueue;

    private static Jlet activeJlet;

    protected Jlet() {
        appInstance = (SkvmAppInstance) AppInstance.appInstance;
        eventQueue = new EventQueue();
    }

    public static void setActiveJlet(Jlet ql) {
        activeJlet = ql;
    }

    public static Jlet getActiveJlet() {
        return activeJlet;
    }

    public static Jlet getJletFromPID(int id) {
        if (id == 0) {
            return activeJlet;
        }
        return null;
    }

    public static Jlet getCurrentJlet() {
        return activeJlet;
    }

    public int getCurrentProgramID() {
        return 0;
    }

    protected abstract void startApp(String[] args);

    protected void pauseApp() {

    }

    protected void resumeApp() {
    }

    protected abstract void destroyApp(boolean unconditional) throws JletStateChangeException;

    public final void notifyDestroyed() {
        appInstance.onShutdown();
    }

    public final String getAppProperty(String key) {
        return ((SkvmAppModel) appInstance.getAppModel()).getAppProperty(key);
    }

    public final EventQueue getEventQueue() {
        return eventQueue;
    }

    public static void startJlet(Jlet jlet) throws JletStateChangeException {
        jlet.startApp(new String[0]);
    }

    public static void destroyJlet(Jlet jlet) throws JletStateChangeException {
        jlet.destroyApp(false);
    }
}
