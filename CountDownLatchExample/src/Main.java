import services.CacheService;
import services.DatabaseService;
import services.MessagingService;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch serviceLatch = new CountDownLatch(3);
        CountDownLatch mainAppLatch = new CountDownLatch(1);

        new Thread(new DatabaseService(3000, serviceLatch, mainAppLatch)).start();
        new Thread(new MessagingService(1500, serviceLatch, mainAppLatch)).start();
        new Thread(new CacheService(5000, serviceLatch, mainAppLatch)).start();

        System.out.println("Main application is waiting for all the services to be up and running...");

        mainAppLatch.countDown();
        serviceLatch.await();

        System.out.println("All services are up.");
    }
}