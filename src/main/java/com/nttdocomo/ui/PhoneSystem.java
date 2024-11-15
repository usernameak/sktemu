package com.nttdocomo.ui;

import com.nttdocomo.system.StoreException;

public class PhoneSystem {
    public static final int ATTR_BACKLIGHT_OFF = 0;
    public static final int ATTR_BACKLIGHT_ON = 1;
    public static final int ATTR_FOLDING_CLOSE = 0;
    public static final int ATTR_FOLDING_OPEN = 1;
    public static final int ATTR_MAIL_AT_CENTER = 2;
    public static final int ATTR_MAIL_NONE = 0;
    public static final int ATTR_MAIL_RECEIVED = 1;
    public static final int ATTR_MESSAGE_AT_CENTER = 2;
    public static final int ATTR_MESSAGE_NONE = 0;
    public static final int ATTR_MESSAGE_RECEIVED = 1;
    public static final int ATTR_VIBRATOR_OFF = 0;
    public static final int ATTR_VIBRATOR_ON = 1;
    public static final int ATTR_BATTERY_PARTIAL = 0;
    public static final int ATTR_BATTERY_FULL = 1;
    public static final int ATTR_BATTERY_CHARGING = 2;
    public static final int ATTR_SERVICEAREA_OUTSIDE = 0;
    public static final int ATTR_SERVICEAREA_INSIDE = 1;
    public static final int ATTR_MANNER_OFF = 0;
    public static final int ATTR_MANNER_ON = 1;
    public static final int ATTR_SCREEN_INVISIBLE = 0;
    public static final int ATTR_SCREEN_VISIBLE = 1;
    public static final int ATTR_SURROUND_OFF = 0;
    public static final int ATTR_SURROUND_ON = 1;

    public static final int DEV_BACKLIGHT = 0;
    public static final int DEV_VIBRATOR = 1;
    public static final int DEV_FOLDING = 2;
    public static final int DEV_MAILBOX = 3;
    public static final int DEV_MESSAGEBOX = 4;
    public static final int DEV_BATTERY = 5;
    public static final int DEV_SERVICEAREA = 6;
    public static final int DEV_MANNER = 7;
    public static final int DEV_KEYPAD = 8;
    public static final int DEV_SCREEN_VISIBLE = 9;
    public static final int DEV_AUDIO_SURROUND = 10;

    public static final int MIN_VENDOR_ATTR = 64;
    public static final int MAX_VENDOR_ATTR = 127;

    public static final int MAX_OPTION_ATTR = 255;
    public static final int MIN_OPTION_ATTR = 128;

    public static final int SOUND_INFO = 0;
    public static final int SOUND_WARNING = 1;
    public static final int SOUND_ERROR = 2;
    public static final int SOUND_ALARM = 3;
    public static final int SOUND_CONFIRM = 4;

    public static final int THEME_STANDBY = 0;
    public static final int THEME_CALL_OUT = 1;
    public static final int THEME_CALL_IN = 2;
    public static final int THEME_MESSAGE_SEND = 3;
    public static final int THEME_MESSAGE_RECEIVE = 4;
    public static final int THEME_AV_CALL_IN = 5;
    public static final int THEME_CHAT_RECEIVED = 6;
    public static final int THEME_AV_CALLING = 7;

    protected PhoneSystem() {
    }

    public static void setAttribute(int attr, int value) {
    }

    public static int getAttribute(int attr) {
        return 0;
    }

    public static boolean isAvailable(int attr) {
        return false;
    }

    public static void playSound(int type) {
    }

    public static void setImageTheme(int target, int id) throws StoreException {
    }

    public static void setSoundTheme(int target, int id) throws StoreException {
    }

    public static void setMovieTheme(int target, int id) throws StoreException {
    }

    public static void setMenuIcons(int[] path, int[] ids) throws StoreException {
    }
}
