package javax.microedition.lcdui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;
import net.sktemu.debug.FeatureNotImplementedError;
import net.sktemu.ui.EmuCanvas;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;

    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;

    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;

    // SKT-specific keys
    public static final int KEY_CLR = 8;
    public static final int KEY_COML = 129;
    public static final int KEY_COMC = 130;
    public static final int KEY_COMR = 131;
    public static final int KEY_UP = 141;
    public static final int KEY_LEFT = 142;
    public static final int KEY_RIGHT = 145;
    public static final int KEY_DOWN = 146;
    public static final int KEY_FIRE = 148;
    public static final int KEY_CALL = 190;
    public static final int KEY_END = 191;
    public static final int KEY_FLIP_OPEN = 192;
    public static final int KEY_FLIP_CLOSE = 193;
    public static final int KEY_VOL_UP = 194;
    public static final int KEY_VOL_DOWN = 195;

    private boolean isShown = false;

    protected Canvas() {

    }

    public int getWidth() {
        return AppInstance.appInstance.getBackbufferImage().getWidth();
    }

    public int getHeight() {
        return AppInstance.appInstance.getBackbufferImage().getHeight();
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    public boolean hasPointerEvents() {
        return false;
    }

    public boolean hasPointerMotionEvents() {
        return false;
    }

    public boolean hasRepeatEvents() {
        return true;
    }

    public int getKeyCode(int gameAction) {
        throw new FeatureNotImplementedError("Canvas::getKeyCode");
    }

    public String getKeyName(int keyCode) {
        throw new FeatureNotImplementedError("Canvas::getKeyName");
    }

    public int getGameAction(int keyCode) {
        switch (keyCode) {
            case KEY_UP: return UP;
            case KEY_DOWN: return DOWN;
            case KEY_LEFT: return LEFT;
            case KEY_RIGHT: return RIGHT;

            case KEY_FIRE: return FIRE;
            case KEY_COML: return GAME_A;
            case KEY_COMR: return GAME_B;
        }
        return 0;
    }

    protected void keyPressed(int keyCode) {
    }

    protected void keyRepeated(int keyCode) {
    }

    protected void keyReleased(int keyCode) {
    }

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }

    protected void pointerDragged(int x, int y) {
    }

    public final void repaint(int x, int y, int width, int height) {
        repaint();
    }

    public final void repaint() {
        AppInstance.appInstance.runOnAppThread(this::serviceRepaints);
    }

    public final void serviceRepaints() {
        SkvmAppInstance app = (SkvmAppInstance) AppInstance.appInstance;

        Display display = app.getDisplay();
        if (display.getCurrent() != Canvas.this) {
            return;
        }

        paint(new Graphics(app.getBackbufferImage()));
        app.blitGraphics();
    }

    protected void showNotify() {
    }

    protected void hideNotify() {
    }

    protected abstract void paint(Graphics g);

    public static void emitKeyPressed(Canvas canvas, int keyCode) {
        canvas.keyPressed(keyCode);
    }

    public static void emitKeyRepeated(Canvas canvas, int keyCode) {
        canvas.keyRepeated(keyCode);
    }

    public static void emitKeyReleased(Canvas canvas, int keyCode) {
        canvas.keyReleased(keyCode);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    public static void setCanvasShown(Canvas canvas, boolean isShown) {
        if (canvas.isShown == isShown) return;

        canvas.isShown = isShown;
        if (isShown) {
            canvas.showNotify();
        } else {
            canvas.hideNotify();
        }
    }
}
