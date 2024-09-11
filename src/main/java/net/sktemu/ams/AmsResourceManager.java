package net.sktemu.ams;

import java.io.IOException;
import java.io.InputStream;

public class AmsResourceManager {
    public static InputStream getResourceAsStream(Class<?> clazz, String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        System.out.println("AmsResourceManager::getResourceAsStream " + name);
        try {
            return AppInstance.appInstance.getClassLoader().getAmsResourceAsStream(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}