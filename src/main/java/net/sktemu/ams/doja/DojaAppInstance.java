package net.sktemu.ams.doja;

import com.nttdocomo.ui.IApplication;
import net.sktemu.ams.AmsClassLoader;
import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppModel;
import net.sktemu.ui.EmuCanvas;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DojaAppInstance extends AppInstance {
    private AmsClassLoader classLoader;
    private IApplication application;

    public DojaAppInstance(DojaAppModel appModel, EmuCanvas emuCanvas) {
        super(appModel, emuCanvas);
    }

    @Override
    public void initAppInstance() throws AmsException {
        super.initAppInstance();

        try {
            classLoader = new AmsClassLoader(appModel);
        } catch (IOException e) {
            throw new AmsException(e);
        }

        runOnAppThread(() -> {
            try {
                Class<?> appClass;
                try {
                    appClass = classLoader.loadClass(((DojaAppModel) appModel).getAppClassName());
                } catch (ClassNotFoundException e) {
                    throw new AmsException("DoJa Application class not found", e);
                }

                try {
                    Object appObj = appClass.getConstructor().newInstance();
                    if (appObj instanceof IApplication) {
                        application = (IApplication) appObj;
                        System.out.println("i-appli load");

                        application.start();
                    }
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    throw new AmsException("DoJa Application creation failed", e);
                } catch (InvocationTargetException e) {
                    throw new AmsException("DoJa Application creation failed", e.getCause());
                }
            } catch (AmsException e) {
                // TODO: this is shit
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() throws AmsException {
        try {

        } finally {
            super.close();
        }
    }

    @Override
    public boolean shutdown() {
        return true;
    }

    @Override
    public void keyPressed(int keyCode) {

    }

    @Override
    public void keyReleased(int keyCode) {

    }

    @Override
    public AmsClassLoader getClassLoader() {
        return classLoader;
    }

    public IApplication getApplication() {
        return application;
    }

    public DojaAppModel getDojaAppModel() {
        return (DojaAppModel) appModel;
    }
}
