package net.sktemu.launcher;

import javax.swing.*;

public class LauncherStartup implements Runnable {
    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            LauncherFrame launcherFrame = new LauncherFrame();
            launcherFrame.setVisible(true);
        });
    }
}
