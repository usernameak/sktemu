package net.sktemu.ams;

import net.sktemu.ui.EmuCanvas;
import net.sktemu.utils.SharedConstants;
import net.sktemu.xceapi.XceApiManager;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppModel {
    public static AppModel appModelInstance = null;

    private final Properties propertyTable = new Properties();
    private final File dataDir;
    private String appID;
    private String midletClassName;
    private AmsClassLoader classLoader;
    private Display display;
    private final EmuCanvas emuCanvas;

    private ExecutorService appThreadExecutor;

    static {
        System.setProperty("com.xce.wipi.version", "1.0.0");
        System.setProperty("m.SK_VM", "12");
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

        File cachedJarDir = new File(dataDir, "_SKTemu_cache");
        if (!cachedJarDir.exists() && !cachedJarDir.mkdir()) {
            throw new AmsException("Failed to create cached jar directory");
        }

        try (FileInputStream fis = new FileInputStream(jarPath)) {
            if (fis.skip(32) != 32) {
                throw new AmsException("Failed to skip data in prefixed jar input stream");
            }

            File cachedJarPath = new File(cachedJarDir, "app.jar");
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

    public void initAppModel() throws AmsException {
        appModelInstance = this;

        appThreadExecutor = Executors.newSingleThreadExecutor();

        display = new Display();

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
}
