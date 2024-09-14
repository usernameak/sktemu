package net.sktemu.ams;

import net.sktemu.ui.EmuCanvas;

import java.io.*;

public abstract class AppModel {
    private final File dataDir;
    private final File cacheDir;
    private final File deviceProfileFile;
    private String appTitle;

    private final AppDeviceProfile deviceProfile = new AppDeviceProfile();

    public AppModel(File dataDir) throws IOException {
        this.dataDir = dataDir;

        if (!dataDir.isDirectory()) {
            throw new IOException("Application data directory does not exist");
        }

        deviceProfileFile = new File(dataDir, "_SKTemu_devprof.ini");
        try {
            deviceProfile.loadDeviceProfile(deviceProfileFile);
        } catch (FileNotFoundException ignored) {
            deviceProfile.saveDeviceProfile(deviceProfileFile);
        }

        cacheDir = new File(dataDir, "_SKTemu_cache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IOException("Failed to create cache directory");
        }
    }

    public String getAppTitle() {
        return appTitle;
    }

    protected void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getDataDir() {
        return dataDir;
    }

    public AppDeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public File getDeviceProfileFile() {
        return deviceProfileFile;
    }

    public abstract File doCacheJar() throws AmsException;

    public abstract AppInstance createAppInstance(EmuCanvas canvas) throws AmsException;
}
