package net.sktemu.ams.skvm;

import net.sktemu.ams.AmsClassLoader;
import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.applet.AppletUtil;
import net.sktemu.ams.skvm.applet.IApplet;
import net.sktemu.rms.RmsManager;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.xceapi.XceApiManager;
import org.kwis.msp.lcdui.Jlet;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SkvmAppInstance extends AppInstance {
    private Display display;
    private final RmsManager rmsManager = new RmsManager();
    private Graphics midpGraphics;
    private AmsClassLoader classLoader;

    private IApplet applet;

    public SkvmAppInstance(SkvmAppModel appModel, EmuCanvas emuCanvas) {
        super(appModel, emuCanvas);
    }

    public Display getDisplay() {
        return display;
    }

    @Override
    public AmsClassLoader getClassLoader() {
        return classLoader;
    }

    public Graphics getMidpGraphics() {
        return midpGraphics;
    }

    @Override
    public void initAppInstance() throws AmsException {
        super.initAppInstance();

        SkvmSysProps.init();

        display = new Display();

        try {
            rmsManager.initialize(appModel.getCacheDir());
        } catch (RecordStoreException e) {
            throw new AmsException(e);
        }

        try {
            classLoader = new AmsClassLoader(appModel);
        } catch (IOException e) {
            throw new AmsException(e);
        }

        midpGraphics = new Graphics(getBackbufferImage());
        XceApiManager.initializeLCDUI(this);

        runOnAppThread(() -> {
            try {
                Class<?> midletClass;
                try {
                    midletClass = classLoader.loadClass(((SkvmAppModel) appModel).getMidletClassName());
                } catch (ClassNotFoundException e) {
                    throw new AmsException("MIDlet class not found", e);
                }

                try {
                    Object midletObj = midletClass.getConstructor().newInstance();
                    if (midletObj instanceof IApplet) {
                        applet = (IApplet) midletObj;

                        System.out.println("IApplet load");
                        AppletUtil.startApplet(applet);
                    }
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    throw new AmsException("MIDlet creation failed", e);
                } catch (InvocationTargetException e) {
                    throw new AmsException("MIDlet creation failed", e.getCause());
                }
            } catch (AmsException e) {
                // TODO: this is shit
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() throws AmsException {
        try {
            AmsException exception = null;
            if (rmsManager != null) {
                try {
                    rmsManager.close();
                } catch (RecordStoreException e) {
                    exception = new AmsException("failed to close rms manager", e);
                }
            }
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException e) {
                    exception = new AmsException("failed to close classloader", e);
                }
            }
            if (exception != null) {
                throw exception;
            }
        } finally {
            super.close();
        }
    }


    public RmsManager getRmsManager() {
        return rmsManager;
    }

    @Override
    public boolean shutdown() {
        try {
            AppletUtil.destroyApplet(applet);
        } catch (AmsException e) {
            return false;
        }
        return true;
    }

    @Override
    public void keyPressed(int keyCode) {
        runOnAppThread(() -> {
            Canvas canvas = (Canvas) display.getCurrent();
            Canvas.emitKeyPressed(canvas, keyCode);
        });
    }

    @Override
    public void keyRepeated(int keyCode) {
        runOnAppThread(() -> {
            Canvas canvas = (Canvas) display.getCurrent();
            Canvas.emitKeyRepeated(canvas, keyCode);
        });
    }

    @Override
    public void keyReleased(int keyCode) {
        runOnAppThread(() -> {
            Canvas canvas = (Canvas) display.getCurrent();
            Canvas.emitKeyReleased(canvas, keyCode);
        });
    }
}
