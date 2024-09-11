package net.sktemu.ams;

import net.sktemu.rms.RmsManager;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.ui.EmuUIFrame;
import net.sktemu.xceapi.XceApiManager;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;
import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppInstance implements AutoCloseable {
    public static AppInstance appInstance = null;

    private final AppModel appModel;
    private AmsClassLoader classLoader;
    private Display display;
    private final EmuCanvas emuCanvas;
    private RmsManager rmsManager;

    private ExecutorService appThreadExecutor;

    static {
        AmsSysPropManager.init();
    }

    public AppInstance(AppModel appModel, EmuCanvas emuCanvas) throws IOException {
        this.appModel = appModel;
        this.emuCanvas = emuCanvas;

        rmsManager = new RmsManager();
    }

    public EmuCanvas getEmuCanvas() {
        return emuCanvas;
    }

    public void runOnAppThread(Runnable runnable) {
        appThreadExecutor.execute(runnable);
    }

    public void runOnUiThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public RmsManager getRmsManager() {
        return rmsManager;
    }

    public void initAppInstance() throws AmsException {
        appInstance = this;

        appThreadExecutor = Executors.newSingleThreadExecutor();

        display = new Display();

        try {
            rmsManager.initialize(appModel.getCacheDir());
        } catch (RecordStoreException e) {
            throw new AmsException(e);
        }

        try {
            classLoader = new AmsClassLoader(appModel.doCacheJar());
        } catch (IOException e) {
            throw new AmsException(e);
        }

        XceApiManager.initializeLCDUI(this);

        runOnAppThread(() -> {
            try {
                Class<?> midletClass;
                try {
                    midletClass = classLoader.loadClass(appModel.getMidletClassName());
                } catch (ClassNotFoundException e) {
                    throw new AmsException("MIDlet class not found", e);
                }

                try {
                    Object midletObj = midletClass.getConstructor().newInstance();
                    if (midletObj instanceof MIDlet) {
                        MIDlet midlet = (MIDlet) midletObj;
                        System.out.println("midlet load");

                        MIDlet.startMidlet(midlet);
                    }
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    throw new AmsException("MIDlet creation failed", e);
                } catch (InvocationTargetException e) {
                    throw new AmsException("MIDlet creation failed", e.getCause());
                } catch (MIDletStateChangeException e) {
                    throw new AmsException("MIDletStateChangeException", e);
                }
            } catch (AmsException e) {
                // TODO: this is shit
                e.printStackTrace();
            }
        });
    }

    public Display getDisplay() {
        return display;
    }

    public AmsClassLoader getClassLoader() {
        return classLoader;
    }

    public AppModel getAppModel() {
        return appModel;
    }

    @Override
    public void close() throws AmsException {
        try {
            if (appThreadExecutor != null) {
                appThreadExecutor.shutdown();
                try {
                    appThreadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    throw new AmsException("termination interrupted", e);
                }
            }
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
            appInstance = null;
        }
    }

    public static void launchApp(AppModel appModel) throws AmsException, IOException {
        EmuUIFrame ui = new EmuUIFrame(appModel);
        ui.setVisible(true);

        AppInstance appInstance = new AppInstance(appModel, ui.getCanvas());
        ui.setAppInstance(appInstance);
        appInstance.initAppInstance();
    }
}
