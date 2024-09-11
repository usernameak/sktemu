package net.sktemu.ui;

import net.sktemu.ams.AppDeviceProfile;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;

import javax.microedition.lcdui.Canvas;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Canvas canvas = (Canvas) appInstance.getDisplay().getCurrent();

                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());
                if (keyCode != null) {
                    Canvas.emitKeyPressed(canvas, keyCode);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Canvas canvas = (Canvas) appInstance.getDisplay().getCurrent();

                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());
                if (keyCode != null) {
                    Canvas.emitKeyReleased(canvas, keyCode);
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
