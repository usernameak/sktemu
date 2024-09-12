package net.sktemu.ams;

import net.sktemu.utils.SharedConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class AppModel {
    private final File dataDir;
    private final File cacheDir;
    private final File deviceProfileFile;
    private String appID;
    private String midletClassName;
    private String midletTitle;

    private final AppDeviceProfile deviceProfile = new AppDeviceProfile();

    private final Properties propertyTable = new Properties();

    public AppModel(File dataDir) throws IOException {
        this.dataDir = dataDir;

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

        cacheDir = new File(dataDir, "_SKTemu_cache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IOException("Failed to create cache directory");
        }

        deviceProfileFile = new File(dataDir, "_SKTemu_devprof.ini");
        try {
            deviceProfile.loadDeviceProfile(deviceProfileFile);
        } catch (FileNotFoundException ignored) {
            deviceProfile.saveDeviceProfile(deviceProfileFile);
        }
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
        midletTitle = midlet1Split[0];
        midletClassName = midlet1Split[2];

        midletTitle = propertyTable.getProperty("MIDlet-Name");
        if (midletTitle == null) {
            midletTitle = midlet1Split[0];
            if (midletTitle.isEmpty()) {
                midletTitle = midletClassName;
            }
        }
    }

    public String getMidletTitle() {
        return midletTitle;
    }

    public String getAppProperty(String name) {
        return propertyTable.getProperty(name);
    }

    public File doCacheJar() throws AmsException {
        File jarPath = new File(dataDir, appID + ".jar");
        File cachedJarPath = new File(cacheDir, "app.jar");

        if ("true".equals(System.getProperty("sktemu.assumePrecachedJars"))) {
            return cachedJarPath;
        }

        try (FileInputStream fis = new FileInputStream(jarPath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            bis.mark(2);
            byte[] signature = new byte[2];
            if (bis.read(signature) != 2) {
                throw new AmsException("Not enough data in jar input stream");
            }
            bis.reset();

            if (signature[0] != 'P' || signature[1] != 'K') {
                if (bis.skip(32) != 32) {
                    throw new AmsException("Failed to skip data in prefixed jar input stream");
                }
            }

            try {
                Files.copy(bis, cachedJarPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new AmsException("Failed to create cached jar file", e);
            }
            return cachedJarPath;
        } catch (IOException e) {
            throw new AmsException("Failed to open the prefixed jar", e);
        }
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getDataDir() {
        return dataDir;
    }

    public String getMidletClassName() {
        return midletClassName;
    }

    public AppDeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public File getDeviceProfileFile() {
        return deviceProfileFile;
    }
}
