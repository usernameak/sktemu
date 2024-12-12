package net.sktemu.ams.skvm.applet;

import net.sktemu.ams.AmsException;
import org.kwis.msp.lcdui.Jlet;
import org.kwis.msp.lcdui.JletStateChangeException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class AppletUtil {
    public static void startApplet(IApplet applet) throws AmsException {
        if (applet instanceof MIDlet) {
            try {
                MIDlet.startMidlet((MIDlet) applet);
            } catch (MIDletStateChangeException e) {
                throw new AmsException("MIDletStateChangeException", e);
            }
        } else if (applet instanceof Jlet) {
            try {
                Jlet.startJlet((Jlet) applet);
            } catch (JletStateChangeException e) {
                throw new AmsException("JletStateChangeException", e);
            }
        }
    }

    public static void destroyApplet(IApplet applet) throws AmsException {
        if (applet instanceof MIDlet) {
            try {
                MIDlet.destroyMidlet((MIDlet) applet);
            } catch (MIDletStateChangeException e) {
                throw new AmsException("MIDletStateChangeException", e);
            }
        } else if (applet instanceof Jlet) {
            try {
                Jlet.destroyJlet((Jlet) applet);
            } catch (JletStateChangeException e) {
                throw new AmsException("JletStateChangeException", e);
            }
        }
    }
}
