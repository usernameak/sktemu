package net.sktemu.ams;

import net.sktemu.ui.EmuCanvas;
import net.sktemu.ui.EmuUIFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AppInstance implements AutoCloseable {
    public static AppInstance appInstance = null;

    public final AppModel appModel;
    private BufferedImage backbufferImage;
    private final EmuCanvas emuCanvas;

    private ExecutorService appThreadExecutor;

    private final Object frameLimiterLock = new Object();

    private long lastPresentTime = 0;

    public AppInstance(AppModel appModel, EmuCanvas emuCanvas) {
        this.appModel = appModel;
        this.emuCanvas = emuCanvas;
    }

    public EmuCanvas getEmuCanvas() {
        return emuCanvas;
    }

    public AppModel getAppModel() {
        return appModel;
    }

    public BufferedImage getBackbufferImage() {
        return backbufferImage;
    }

    public void runOnAppThread(Runnable runnable) {
        appThreadExecutor.execute(runnable);
    }

    public void runOnUiThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public void initAppInstance() throws AmsException {
        appInstance = this;

        appThreadExecutor = Executors.newSingleThreadExecutor();

        backbufferImage = new BufferedImage(
                emuCanvas.getBufferedImage().getWidth(),
                emuCanvas.getBufferedImage().getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
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
        } finally {
            appInstance = null;
        }
    }

    public static void launchApp(AppModel appModel) throws AmsException, IOException {
        EmuUIFrame ui = new EmuUIFrame(appModel);
        ui.setVisible(true);

        AppInstance appInstance = appModel.createAppInstance(ui.getCanvas());
        ui.setAppInstance(appInstance);
        appInstance.initAppInstance();
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

    public void onShutdown() {
        System.exit(0);
    }

    public abstract boolean shutdown();

    public abstract void keyPressed(int keyCode);
    public abstract void keyRepeated(int keyCode);
    public abstract void keyReleased(int keyCode);

    public abstract AmsClassLoader getClassLoader();
}
