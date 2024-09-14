package net.sktemu.ams;

import java.io.File;
import java.io.IOException;

public interface AppModelFactory {
    AppModel createAppModel(File appDir) throws IOException;

    boolean checkIfValidApp(File appDir) throws IOException;
}
