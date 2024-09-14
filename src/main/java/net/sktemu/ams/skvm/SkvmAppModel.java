package net.sktemu.ams.skvm;

import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.utils.SharedConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class SkvmAppModel extends AppModel {
    private String appID;
    private String midletClassName;

    private final Properties propertyTable = new Properties();

    public SkvmAppModel(File dataDir) throws IOException {
        super(dataDir);

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
        String midletTitle = midlet1Split[0];
        midletClassName = midlet1Split[2];

        midletTitle = propertyTable.getProperty("MIDlet-Name");
        if (midletTitle == null) {
            midletTitle = midlet1Split[0];
            if (midletTitle.isEmpty()) {
                midletTitle = midletClassName;
            }
        }

        setAppTitle(midletTitle);
    }

    @Override
    public File doCacheJar() throws AmsException {
        File jarPath = new File(getDataDir(), appID + ".jar");
        File cachedJarPath = new File(getCacheDir(), "app.jar");

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

    public String getAppProperty(String name) {
        return propertyTable.getProperty(name);
    }

    public String getMidletClassName() {
        return midletClassName;
    }

    @Override
    public AppInstance createAppInstance(EmuCanvas canvas) throws AmsException {
        return new SkvmAppInstance(this, canvas);
    }
}
