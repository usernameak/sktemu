package net.sktemu.ui;

import javax.microedition.lcdui.Canvas;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyMappings {
    public static final HashMap<Integer, Integer> keyMappings = new HashMap<>();
    static {
        keyMappings.put(KeyEvent.VK_LEFT, Canvas.KEY_LEFT);
        keyMappings.put(KeyEvent.VK_UP, Canvas.KEY_UP);
        keyMappings.put(KeyEvent.VK_RIGHT, Canvas.KEY_RIGHT);
        keyMappings.put(KeyEvent.VK_DOWN, Canvas.KEY_DOWN);
        keyMappings.put(KeyEvent.VK_ENTER, Canvas.KEY_FIRE);

        keyMappings.put(KeyEvent.VK_Z, Canvas.KEY_COML);
        keyMappings.put(KeyEvent.VK_X, Canvas.KEY_COMC);
        keyMappings.put(KeyEvent.VK_C, Canvas.KEY_COMR);

        keyMappings.put(KeyEvent.VK_BACK_SPACE, Canvas.KEY_CLR);

        keyMappings.put(KeyEvent.VK_NUMPAD0, Canvas.KEY_NUM0);
        keyMappings.put(KeyEvent.VK_NUMPAD1, Canvas.KEY_NUM1);
        keyMappings.put(KeyEvent.VK_NUMPAD2, Canvas.KEY_NUM2);
        keyMappings.put(KeyEvent.VK_NUMPAD3, Canvas.KEY_NUM3);
        keyMappings.put(KeyEvent.VK_NUMPAD4, Canvas.KEY_NUM4);
        keyMappings.put(KeyEvent.VK_NUMPAD5, Canvas.KEY_NUM5);
        keyMappings.put(KeyEvent.VK_NUMPAD6, Canvas.KEY_NUM6);
        keyMappings.put(KeyEvent.VK_NUMPAD7, Canvas.KEY_NUM7);
        keyMappings.put(KeyEvent.VK_NUMPAD8, Canvas.KEY_NUM8);
        keyMappings.put(KeyEvent.VK_NUMPAD9, Canvas.KEY_NUM9);
        keyMappings.put(KeyEvent.VK_DIVIDE, Canvas.KEY_POUND);
        keyMappings.put(KeyEvent.VK_MULTIPLY, Canvas.KEY_STAR);

        keyMappings.put(KeyEvent.VK_0, Canvas.KEY_NUM0);
        keyMappings.put(KeyEvent.VK_1, Canvas.KEY_NUM1);
        keyMappings.put(KeyEvent.VK_2, Canvas.KEY_NUM2);
        keyMappings.put(KeyEvent.VK_3, Canvas.KEY_NUM3);
        keyMappings.put(KeyEvent.VK_4, Canvas.KEY_NUM4);
        keyMappings.put(KeyEvent.VK_5, Canvas.KEY_NUM5);
        keyMappings.put(KeyEvent.VK_6, Canvas.KEY_NUM6);
        keyMappings.put(KeyEvent.VK_7, Canvas.KEY_NUM7);
        keyMappings.put(KeyEvent.VK_8, Canvas.KEY_NUM8);
        keyMappings.put(KeyEvent.VK_9, Canvas.KEY_NUM9);
        keyMappings.put(KeyEvent.VK_MINUS, Canvas.KEY_POUND);
        keyMappings.put(KeyEvent.VK_EQUALS, Canvas.KEY_STAR);
    }
}
