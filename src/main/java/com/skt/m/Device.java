package com.skt.m;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Device {
    public static void setBacklightEnabled(boolean flag) {
    }

    public static void setKeyToneEnabled(boolean flag) {
    }

    public static void enableRestoreLCD(boolean flag) {
    }

    public static void setColorMode(int mode) {
        System.err.println("Device::setColorMode");
    }

    public static void invokeWapBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
