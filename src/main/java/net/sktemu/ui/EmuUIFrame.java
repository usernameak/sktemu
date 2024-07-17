package net.sktemu.ui;

import net.sktemu.ams.AppModel;

import javax.microedition.lcdui.Canvas;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EmuUIFrame extends JFrame {
    private final EmuCanvas canvas;

    private AppModel appModel;

    public EmuUIFrame() {
        setTitle("SKTemu");

        canvas = new EmuCanvas(240, 320);
        setContentPane(canvas);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Canvas canvas = (Canvas) appModel.getDisplay().getCurrent();

                Integer keyCode = KeyMappings.keyMappings.get(e.getKeyCode());
                if (keyCode != null) {
                    Canvas.emitKeyPressed(canvas, keyCode);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Canvas canvas = (Canvas) appModel.getDisplay().getCurrent();

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

    public void setAppModel(AppModel appModel) {
        this.appModel = appModel;
    }
}
