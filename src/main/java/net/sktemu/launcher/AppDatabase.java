package net.sktemu.launcher;

import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppModel;
import net.sktemu.ams.AppModelFactory;
import net.sktemu.ams.AppModelFactoryManager;
import net.sktemu.ams.skvm.SkvmAppModel;
import net.sktemu.utils.SharedConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppDatabase {
    private final File appsDirectory = new File("apps");

    private final List<AppModel> apps = new ArrayList<>();

    public void rescanApps() throws IOException {
        apps.clear();

        //noinspection ResultOfMethodCallIgnored
        appsDirectory.mkdirs();

        File[] appDirs = appsDirectory.listFiles();

        if (appDirs == null) {
            throw new IOException("Applications directory does not exist");
        }

        for (File appDir : appDirs) {
            AppModelFactory factory = AppModelFactoryManager.detectFactory(appDir);
            if (factory == null) continue;

            apps.add(factory.createAppModel(appDir));
        }
    }

    public List<AppModel> getApps() {
        return Collections.unmodifiableList(apps);
    }

    private void zipExtract(ZipFile zipFile, File destDir) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        byte[] buf = new byte[4096];

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            InputStream stream = zipFile.getInputStream(zipEntry);
            File newFile = new File(destDir, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Cannot create dir " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Cannot create dir " + parent);
                }

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = stream.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                }
            }
        }
    }

    public AppModel installSkvmAppFromZip(File file) throws AmsException {
        try (ZipFile zipFile = new ZipFile(file, SharedConstants.CP949)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            String msdFilename = null;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String filename = entry.getName();
                if (filename.endsWith(".msd") && !filename.endsWith(".jad")) {
                    msdFilename = filename;
                }
            }

            if (msdFilename == null) {
                throw new AmsException(".msd/.jad file missing in the app archive");
            }

            String appDirName = msdFilename.substring(0, msdFilename.length() - 4);
            File appDir = new File(appsDirectory, appDirName);
            zipExtract(zipFile, appDir);

            AppModel appModel = new SkvmAppModel(appDir);
            apps.add(appModel);
            return appModel;
        } catch (IOException e) {
            throw new AmsException("Cannot extract app: " + e.getMessage(), e);
        }
    }
}
