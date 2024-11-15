package net.sktemu.ams.doja;

import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;
import net.sktemu.ui.EmuCanvas;
import net.sktemu.utils.SharedConstants;

import java.io.*;
import java.util.Properties;

public class DojaAppModel extends AppModel {
    private String appID;
    private String appClassName;
    private String packageURL;

    private final Properties propertyTable = new Properties();

    public DojaAppModel(File dataDir) throws IOException {
        super(dataDir);

        File[] files = dataDir.listFiles();
        if (files == null) {
            throw new IOException("Application data directory does not exist");
        }

        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".jam")) continue;

            appID = name.substring(0, name.length() - 4);

            loadDescriptor(file);
            break;
        }
    }

    public void loadDescriptor(File file) throws IOException {
        try (InputStream stream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(stream, SharedConstants.SHIFT_JIS)) {
            propertyTable.load(reader);
        }

        setAppTitle(propertyTable.getProperty("AppName"));
        appClassName = propertyTable.getProperty("AppClass");
        packageURL = propertyTable.getProperty("PackageURL");
    }

    public String getAppClassName() {
        return appClassName;
    }

    public String getPackageURL() {
        return packageURL;
    }

    @Override
    public File doCacheJar() throws AmsException {
        return new File(getDataDir(), appID + ".jar");
    }

    @Override
    public AppInstance createAppInstance(EmuCanvas canvas) {
        return new DojaAppInstance(this, canvas);
    }

    public String getAppProperty(String name) {
        return propertyTable.getProperty(name);
    }
}
