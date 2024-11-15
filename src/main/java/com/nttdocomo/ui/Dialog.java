package com.nttdocomo.ui;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppLoopManager;

public final class Dialog extends Canvas {
    public static final int DIALOG_INFO = 0;
    public static final int DIALOG_WARNING = 1;
    public static final int DIALOG_ERROR = 2;
    public static final int DIALOG_YESNO = 3;
    public static final int DIALOG_YESNOCANCEL = 4;

    public static final int BUTTON_OK = 0;
    public static final int BUTTON_CANCEL = 1 << 1;
    public static final int BUTTON_YES = 1 << 2;
    public static final int BUTTON_NO = 1 << 3;

    private final Font headerFont = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_PLAIN | Font.SIZE_MEDIUM);
    private Font font = Font.getDefaultFont();

    private final int type;
    private final String title;
    private String text = "";

    private AppLoopManager.ModalLoopToken modalLoopToken;
    private int showResult = BUTTON_CANCEL;

    public Dialog(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(headerFont);
        g.drawString(title, 0, headerFont.getAscent());

        g.setFont(font);
        g.drawString(text, 0, font.getAscent() + headerFont.getHeight());
    }

    public int show() {
        Display.setCurrentDialog(this);

        AppLoopManager appLoopManager = AppInstance.appInstance.getAppLoopManager();

        modalLoopToken = appLoopManager.newModalLoopToken();
        appLoopManager.startModalLoop(modalLoopToken);

        return showResult;
    }
}
