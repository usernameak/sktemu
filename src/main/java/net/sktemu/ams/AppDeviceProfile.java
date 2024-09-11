package net.sktemu.ams;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppDeviceProfile {
    private int screenWidth = 240;
    private int screenHeight = 320;

    public void loadDeviceProfile(File file) throws IOException {
        Properties propertyTable = new Properties();

        try (InputStream stream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            propertyTable.load(reader);
        }

        try {
            screenWidth = Integer.parseInt(propertyTable.getProperty("screenWidth", "240"));
        } catch (NumberFormatException ignored) {}
        try {
            screenHeight = Integer.parseInt(propertyTable.getProperty("screenHeight", "320"));
        } catch (NumberFormatException ignored) {}
    }

    public void saveDeviceProfile(File file) throws IOException {
        Properties propertyTable = new Properties();

        propertyTable.setProperty("screenWidth", Integer.toString(screenWidth));
        propertyTable.setProperty("screenHeight", Integer.toString(screenHeight));

        try (OutputStream stream = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            propertyTable.store(writer, "SKTemu device profile");
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
