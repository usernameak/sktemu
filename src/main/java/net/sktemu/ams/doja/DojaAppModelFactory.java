package net.sktemu.ams.doja;

import net.sktemu.ams.AppModel;
import net.sktemu.ams.AppModelFactory;

import java.io.File;
import java.io.IOException;

public class DojaAppModelFactory implements AppModelFactory {
    @Override
    public AppModel createAppModel(File appDir) throws IOException {
        return new DojaAppModel(appDir);
    }

    @Override
    public boolean checkIfValidApp(File appDir) throws IOException {
        File[] files = appDir.listFiles();
        if (files == null) return false;

        boolean found = false;
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".jam")) continue;

            found = true;
            break;
        }

        return found;
    }
}
