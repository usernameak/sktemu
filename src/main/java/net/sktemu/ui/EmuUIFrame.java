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

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_LEFT);
                        break;
                    case KeyEvent.VK_UP:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_UP);
                        break;
                    case KeyEvent.VK_RIGHT:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_RIGHT);
                        break;
                    case KeyEvent.VK_DOWN:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_DOWN);
                        break;
                    case KeyEvent.VK_ENTER:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_FIRE);
                        break;
                    case KeyEvent.VK_MINUS:
                        Canvas.emitKeyPressed(canvas, Canvas.KEY_POUND);
                        break;
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

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
