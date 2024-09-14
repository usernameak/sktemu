package net.sktemu.ui;

import net.sktemu.ams.AppDeviceProfile;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;

import javax.microedition.lcdui.Canvas;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EmuUIFrame extends JFrame {
    private final EmuCanvas canvas;

    private AppInstance appInstance;

    public EmuUIFrame(AppModel appModel) {
        setTitle("SKTemu \u2013 " + appModel.getMidletTitle());

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
                    appInstance.runOnAppThread(() -> {
                        Canvas canvas = (Canvas) appInstance.getDisplay().getCurrent();
                        Canvas.emitKeyPressed(canvas, keyCode);
                    });
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());

                if (keyCode != null) {
                    appInstance.runOnAppThread(() -> {
                        Canvas canvas = (Canvas) appInstance.getDisplay().getCurrent();
                        Canvas.emitKeyReleased(canvas, keyCode);
                    });
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
