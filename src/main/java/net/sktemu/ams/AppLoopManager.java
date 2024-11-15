package net.sktemu.ams;

import java.util.concurrent.LinkedBlockingQueue;

public class AppLoopManager {
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private volatile boolean shouldStop = false;
    private final Object terminationSignal = new Object();

    public class ModalLoopToken {
        private volatile boolean modalShouldStop = false;

        public void stopModalLoop() {
            queue.add(() -> modalShouldStop = true);
        }
    }

    private void threadMain() {
        try {
            while (!shouldStop) {
                Runnable runnable = queue.take();
                runnable.run();
            }
        } catch (InterruptedException ignored) {
        }

        synchronized (terminationSignal) {
            terminationSignal.notify();
        }
    }

    public void addTask(Runnable runnable) {
        this.queue.add(runnable);
    }

    public ModalLoopToken newModalLoopToken() {
        return new ModalLoopToken();
    }

    public void startModalLoop(ModalLoopToken token) {
        try {
            while (!token.modalShouldStop && !shouldStop) {
                Runnable runnable = queue.take();
                runnable.run();
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public void terminateAndWait() throws InterruptedException {
        addTask(() -> shouldStop = true);

        synchronized (terminationSignal) {
            terminationSignal.wait();
        }
    }

    public void startLoopThread() {
        Thread thread = new Thread(this::threadMain);
        thread.setName("Emulator App Loop");
        thread.start();
    }
}
