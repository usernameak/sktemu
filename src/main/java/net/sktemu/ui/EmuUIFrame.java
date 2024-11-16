package net.sktemu.ui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;

public class EmuUIFrame extends JFrame {
    private final EmuCanvas canvas;

    private AppInstance appInstance;

    private HashSet<Integer> pressedKeys = new HashSet<Integer>();

    public EmuUIFrame(AppModel appModel) {
        setTitle("SKTemu \u2013 " + appModel.getAppTitle());

        canvas = new EmuCanvas(
                appModel.getDeviceProfile().getScreenWidth(),
                appModel.getDeviceProfile().getScreenHeight()
        );
        setContentPane(canvas);

        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (appInstance != null && !appInstance.shutdown()) return;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    System.exit(1);
                    return;
                }

                System.exit(0);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());

                if (keyCode != null) {
                    if (!pressedKeys.contains(keyCode)) {
                        pressedKeys.add(keyCode);
                        appInstance.keyPressed(keyCode);
                    } else {
                        appInstance.keyRepeated(keyCode);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());

                if (keyCode != null) {
                    pressedKeys.remove(keyCode);
                    appInstance.keyReleased(keyCode);
                }
            }
        });
    }

    public EmuCanvas getCanvas() {
        return canvas;
    }

    public void setAppInstance(AppInstance appInstance) {
        this.appInstance = appInstance;
    }
}
