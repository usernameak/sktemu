package net.sktemu.ams.skvm;

import net.sktemu.ams.AppModel;
import net.sktemu.ams.AppModelFactory;

import java.io.File;
import java.io.IOException;

public class SkvmAppModelFactory implements AppModelFactory {
    @Override
    public AppModel createAppModel(File appDir) throws IOException {
        return new SkvmAppModel(appDir);
    }

    @Override
    public boolean checkIfValidApp(File appDir) {
        File[] files = appDir.listFiles();
        if (files == null) return false;

        boolean msdFound = false;
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".msd")) continue;

            msdFound = true;
            break;
        }

        return msdFound;
    }
}
