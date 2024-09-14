package com.nttdocomo.ui;

import com.nttdocomo.util.ScheduleDate;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.doja.DojaAppInstance;

public abstract class IApplication {
    public static final int LAUNCHED_FROM_MENU = 0;
    public static final int LAUNCHED_AFTER_DOWNLOAD = 1;
    public static final int LAUNCHED_FROM_TIMER = 2;
    public static final int LAUNCHED_AS_CONCIERGE = 3;
    public static final int LAUNCHED_FROM_EXT = 4;
    public static final int LAUNCHED_FROM_BROWSER = 5;
    public static final int LAUNCHED_FROM_MAILER = 6;
    public static final int LAUNCHED_FROM_IAPPLI = 7;
    public static final int LAUNCHED_FROM_LAUNCHER = 8;
    public static final int LAUNCHED_AS_ILET = 9;
    public static final int LAUNCHED_MSG_RECEIVED = 10;
    public static final int LAUNCHED_MSG_SENT = 11;
    public static final int LAUNCHED_MSG_UNSENT = 12;
    public static final int LAUNCHED_FROM_LOCATION_INFO = 13;
    public static final int LAUNCHED_FROM_LOCATION_IMAGE = 14;
    public static final int LAUNCHED_FROM_PHONEBOOK = 15;
    public static final int LAUNCHED_FROM_DTV = 17;
    public static final int LAUNCHED_FROM_TORUCA = 18;
    public static final int LAUNCHED_FROM_FELICA_ADHOC = 19;
    public static final int LAUNCHED_FROM_MENU_FOR_DELETION = 20;
    public static final int LAUNCHED_FROM_BML = 21;
    public static final int LAUNCH_BROWSER = 1;
    public static final int LAUNCH_VERSIONUP = 2;
    public static final int LAUNCH_IAPPLI = 3;
    public static final int LAUNCH_AS_LAUNCHER = 4;
    public static final int LAUNCH_MAILMENU = 5;
    public static final int LAUNCH_SCHEDULER = 6;
    public static final int LAUNCH_MAIL_RECEIVED = 7;
    public static final int LAUNCH_MAIL_SENT = 8;
    public static final int LAUNCH_MAIL_UNSENT = 9;
    public static final int LAUNCH_MAIL_LAST_INCOMING = 10;
    public static final int LAUNCH_DTV = 12;
    public static final int LAUNCH_BROWSER_SUSPEND = 13;
    public static final int SUSPEND_BY_NATIVE = 1;
    public static final int SUSPEND_BY_IAPP = 2;
    public static final int SUSPEND_PACKETIN = 256;
    public static final int SUSPEND_CALL_OUT = 512;
    public static final int SUSPEND_CALL_IN = 1024;
    public static final int SUSPEND_MAIL_SEND = 2048;
    public static final int SUSPEND_MAIL_RECEIVE = 4096;
    public static final int SUSPEND_MESSAGE_RECEIVE = 8192;
    public static final int SUSPEND_SCHEDULE_NOTIFY = 16384;
    public static final int SUSPEND_MULTITASK_APPLICATION = 32768;

    private final DojaAppInstance appInstance;
    private final PushManager pushManager = new PushManager();

    public IApplication() {
        appInstance = ((DojaAppInstance) AppInstance.appInstance);
    }

    public static IApplication getCurrentApp() {
        return ((DojaAppInstance) AppInstance.appInstance).getApplication();
    }

    public final String[] getArgs() {
        return new String[0];
    }

    public final String getParameter(String name) {
        return null;
    }

    public abstract void start();

    public void resume() {
    }

    public final void terminate() {
        appInstance.onShutdown();
    }

    public final String getSourceURL() {
        return appInstance.getDojaAppModel().getPackageURL();
    }

    public final int getLaunchType() {
        return LAUNCHED_FROM_MENU;
    }

    public final void launch(int target, String[] args) {
        throw new SecurityException("launch not supported");
    }

    public int getSuspendInfo() {
        return 0;
    }

    public void setLaunchTime(int index, ScheduleDate date) {
        throw new SecurityException("setLaunchTime/getLaunchTime not supported");
    }

    public ScheduleDate getLaunchTime(int index) {
        throw new SecurityException("setLaunchTime/getLaunchTime not supported");
    }

    public PushManager getPushManager() throws SecurityException {
        return pushManager;
    }

    public final boolean isMoved() {
        return false;
    }

    public final void clearMoved() {
    }

    public final boolean isMovedFromOtherTerminal() {
        return false;
    }
}
