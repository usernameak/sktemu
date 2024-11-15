package net.sktemu.ams.doja;

import com.nttdocomo.opt.ui.Graphics2;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.IApplication;
import net.sktemu.ams.AmsClassLoader;
import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.doja.io.ScratchpadManager;
import net.sktemu.ui.EmuCanvas;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DojaAppInstance extends AppInstance {
    private AmsClassLoader classLoader;
    private IApplication application;
    private Graphics dojaGraphics;
    private ScratchpadManager scratchpadManager;

    public DojaAppInstance(DojaAppModel appModel, EmuCanvas emuCanvas) {
        super(appModel, emuCanvas);
    }

    @Override
    public void initAppInstance() throws AmsException {
        super.initAppInstance();

        scratchpadManager = new ScratchpadManager();
        scratchpadManager.initializeScratchpad(getDojaAppModel().getAppProperty("SPsize"));

        try {
            classLoader = new AmsClassLoader(appModel);
        } catch (IOException e) {
            throw new AmsException(e);
        }

        dojaGraphics = new Graphics2(getBackbufferImage());

        runOnAppThread(() -> {
            try {
                Class<?> appClass;
                try {
                    appClass = classLoader.loadClass(getDojaAppModel().getAppClassName());
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

    public Graphics getDojaGraphics() {
        return dojaGraphics;
    }

    public ScratchpadManager getScratchpadManager() {
        return scratchpadManager;
    }
}
