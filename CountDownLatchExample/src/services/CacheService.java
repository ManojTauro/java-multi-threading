package services;

import java.util.concurrent.CountDownLatch;

public class CacheService implements Service, Runnable {
    private final long initTime;
    private final CountDownLatch latch;
    private final String NAME = "CACHE";
    private final CountDownLatch mainAppLatch;

    public CacheService(long initTime, CountDownLatch latch, CountDownLatch mainAppLatch) {
        this.initTime = initTime;
        this.latch = latch;
        this.mainAppLatch = mainAppLatch;
    }

    @Override
    public void onRunning() {
        System.out.println(NAME+" service is up and running...");
    }

    @Override
    public void run() {
        try {
            mainAppLatch.await();
            System.out.println(NAME+" is Starting Up");
            Thread.sleep(initTime);
            onRunning();
        } catch (Exception ignored) {

        } finally {
            latch.countDown();
        }
    }
}
