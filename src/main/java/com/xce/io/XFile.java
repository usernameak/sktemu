package com.xce.io;

import net.sktemu.ams.AppModel;

import java.io.File;
import java.io.IOException;

public class XFile {
    public XFile(String name, int mode) {
        System.out.println(name);
    }

    static File convertFilePath(String path) {
        return new File(AppModel.appModelInstance.getDataDir(), path);
    }

    public static boolean exists(String name) throws IOException {
        return convertFilePath(name).exists();
    }
}
