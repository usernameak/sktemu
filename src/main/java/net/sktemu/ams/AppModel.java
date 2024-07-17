package net.sktemu.ams;

import net.sktemu.rms.RmsManager;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.utils.SharedConstants;
import net.sktemu.xceapi.XceApiManager;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;
import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppModel implements AutoCloseable {
    public static AppModel appModelInstance = null;

    private final Properties propertyTable = new Properties();
    private final File dataDir;
    private final File cacheDir;
    private String appID;
    private String midletClassName;
    private AmsClassLoader classLoader;
    private Display display;
    private final EmuCanvas emuCanvas;
    private RmsManager rmsManager;

    private ExecutorService appThreadExecutor;

    static {
        AmsSysPropManager.init();
    }

    public AppModel(File dataDir, EmuCanvas emuCanvas) throws IOException {
        this.dataDir = dataDir;
        this.emuCanvas = emuCanvas;

        File[] files = dataDir.listFiles();
        if (files == null) {
            throw new IOException("Application data directory does not exist");
        }
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".msd")) continue;

            appID = name.substring(0, name.length() - 4);

            loadDescriptor(file);
            break;
        }

        cacheDir = new File(dataDir, "_SKTemu");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IOException("Failed to create cache directory");
        }

        rmsManager = new RmsManager();
    }

    public EmuCanvas getEmuCanvas() {
        return emuCanvas;
    }

    public void loadDescriptor(File file) throws IOException {
        try (InputStream stream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(stream, SharedConstants.CP949)) {
            propertyTable.load(reader);
        }

        String midlet1 = propertyTable.getProperty("MIDlet-1");
        String[] midlet1Split = midlet1.split(",");
        if (midlet1Split.length != 3) {
            throw new IllegalArgumentException("MIDlet-1 must be exactly 3 comma separated items long");
        }
        midletClassName = midlet1Split[2];
    }

    public String getAppProperty(String name) {
        return propertyTable.getProperty(name);
    }

    public void runOnAppThread(Runnable runnable) {
        appThreadExecutor.execute(runnable);
    }

    public void runOnUiThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    private File doCacheJar() throws AmsException {
        File jarPath = new File(dataDir, appID + ".jar");

        try (FileInputStream fis = new FileInputStream(jarPath)) {
            if (fis.skip(32) != 32) {
                throw new AmsException("Failed to skip data in prefixed jar input stream");
            }

            File cachedJarPath = new File(cacheDir, "app.jar");
            try {
                Files.copy(fis, cachedJarPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return cachedJarPath;
            } catch (IOException e) {
                throw new AmsException("Failed to create cached jar file", e);
            }
        } catch (IOException e) {
            throw new AmsException("Failed to open the prefixed jar", e);
        }
    }

    public RmsManager getRmsManager() {
        return rmsManager;
    }

    public void initAppModel() throws AmsException {
        appModelInstance = this;

        appThreadExecutor = Executors.newSingleThreadExecutor();

        display = new Display();

        try {
            rmsManager.initialize(cacheDir);
        } catch (RecordStoreException e) {
            throw new AmsException(e);
        }

        try {
            classLoader = new AmsClassLoader(doCacheJar());
        } catch (IOException e) {
            throw new AmsException(e);
        }

        XceApiManager.initializeLCDUI(this);

        runOnAppThread(() -> {
            try {
                Class<?> midletClass;
                try {
                    midletClass = classLoader.loadClass(midletClassName);
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

    public File getDataDir() {
        return dataDir;
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
            appModelInstance = null;
        }
    }
}
