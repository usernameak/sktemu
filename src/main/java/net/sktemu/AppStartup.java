package net.sktemu;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import net.sktemu.ams.*;
import net.sktemu.launcher.LauncherStartup;
import net.sktemu.ui.EmuUIFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class AppStartup implements Runnable {
    @Parameter(names = {"--appdir", "-a"}, converter = FileConverter.class)
    private File appDir;

    public static void main(String... args) {
        AppStartup startup = new AppStartup();
        JCommander.newBuilder()
                .addObject(startup)
                .build()
                .parse(args);

        startup.run();
    }

    @Override
    public void run() {
        if (appDir == null) {
            LauncherStartup launcherStartup = new LauncherStartup();
            launcherStartup.run();
        } else {
            SwingUtilities.invokeLater(this::swingRun);
        }
    }

    private void swingRun() {
        try {
            AppModelFactory factory = AppModelFactoryManager.detectFactory(appDir);
            if (factory == null) {
                throw new AmsException("unsupported app type");
            }
            AppModel appModel = factory.createAppModel(appDir);
            AppInstance.launchApp(appModel);
        } catch (AmsException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
