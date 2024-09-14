package net.sktemu.ams;

import net.sktemu.ams.doja.DojaAppModelFactory;
import net.sktemu.ams.skvm.SkvmAppModelFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppModelFactoryManager {
    private static final List<AppModelFactory> factories = new ArrayList<>();

    public static AppModelFactory detectFactory(File appDir) throws IOException {
        for (AppModelFactory factory : factories) {
            if (factory.checkIfValidApp(appDir)) {
                return factory;
            }
        }
        return null;
    }

    static {
        factories.add(new DojaAppModelFactory());
        factories.add(new SkvmAppModelFactory());
    }
}
