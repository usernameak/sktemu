package net.sktemu.ams;

import net.sktemu.ui.EmuUIFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class AppStartup {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmuUIFrame ui = new EmuUIFrame();

            ui.setVisible(true);

            try {
                AppModel appModel = new AppModel(new File(args[0]), ui.getCanvas());
                ui.setAppModel(appModel);
                appModel.initAppModel();
            } catch (AmsException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
