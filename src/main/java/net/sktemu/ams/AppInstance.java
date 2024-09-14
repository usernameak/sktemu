package net.sktemu.ams;

import net.sktemu.rms.RmsManager;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.ui.EmuUIFrame;
import net.sktemu.xceapi.XceApiManager;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;
import javax.swing.*;
import java.awt.image.BufferedImage;
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
    private BufferedImage backbufferImage;
    private Graphics midpGraphics;
    private final EmuCanvas emuCanvas;
    private RmsManager rmsManager;

    private ExecutorService appThreadExecutor;

    private final Object frameLimiterLock = new Object();

    private long lastPresentTime = 0;

    private MIDlet midlet;

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
            classLoader = new AmsClassLoader(appModel);
        } catch (IOException e) {
            throw new AmsException(e);
        }

        backbufferImage = new BufferedImage(
                emuCanvas.getBufferedImage().getWidth(),
                emuCanvas.getBufferedImage().getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        midpGraphics = new Graphics(backbufferImage);
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
                        midlet = (MIDlet) midletObj;
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

    public BufferedImage getBackbufferImage() {
        return backbufferImage;
    }

    public Graphics getMidpGraphics() {
        return midpGraphics;
    }

    public void blitGraphics() {
        int maxFps = getAppModel().getDeviceProfile().getMaxFps();

        if (maxFps > 0) {
            synchronized (frameLimiterLock) {
                long presentTime = System.nanoTime();
                long deltaTime = 1_000_000_000L / maxFps - (presentTime - lastPresentTime);

                if (deltaTime > 0) {
                    try {
                        Thread.sleep(deltaTime / 1_000_000L, (int) (deltaTime % 1_000_000));
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (deltaTime < -20_000_000L) {
                    lastPresentTime = presentTime - 20; // overrun
                } else {
                    lastPresentTime = presentTime + deltaTime;
                }
            }
        }

        synchronized (emuCanvas.getBufferedImage()) {
            backbufferImage.copyData(emuCanvas.getBufferedImage().getRaster());
        }

        runOnUiThread(emuCanvas::repaint);
    }

    public boolean shutdown() {
        try {
            MIDlet.destroyMidlet(midlet);
        } catch (MIDletStateChangeException e) {
            return false;
        }
        return true;
    }

    public void onShutdown() {
        System.exit(0);
    }
}
