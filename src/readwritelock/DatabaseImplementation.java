package readwritelock;

import java.awt.desktop.SystemEventListener;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class DatabaseImplementation {
    public static int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDB inventoryDB = new InventoryDB();
        Random random = new Random();

        IntStream.range(0, HIGHEST_PRICE).forEach(item -> inventoryDB.addItem(random.nextInt(HIGHEST_PRICE)));

        Thread writer = new Thread(() -> {
           while (true) {
               inventoryDB.addItem(random.nextInt(HIGHEST_PRICE));
               inventoryDB.removeItem(random.nextInt(HIGHEST_PRICE));

               try {
                   Thread.sleep(10);
               } catch (InterruptedException e) {
               }
           }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfThreads = 7;
        List<Thread> readers = new ArrayList<>();

        IntStream.range(0, numberOfThreads)
                .forEach(count -> {
                    Thread reader = new Thread(() -> {
                        IntStream.range(0, 100000)
                                .forEach(num -> {
                                    int upperB = random.nextInt(HIGHEST_PRICE);
                                    int lowerB = upperB > 0 ? random.nextInt(upperB) : 0;

                                    inventoryDB.getItemsInPriceRange(lowerB, upperB);
                                });
                    });

                    reader.setDaemon(true);
                    readers.add(reader);
                });

        long start = System.currentTimeMillis();

        readers.forEach(Thread::start);

        for (Thread reader: readers) reader.join();

        long end = System.currentTimeMillis();

        System.out.printf("Reading took %d ms%n", end - start);
    }

    public static class InventoryDB {
        private TreeMap<Integer, Integer> priceToItemsMap = new TreeMap<>();
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private Lock readLock = lock.readLock();
        private Lock writeLock = lock.writeLock();

        public void getItemsInPriceRange(int lowerB, int upperB) {
            readLock.lock();
            try {
                Integer fromKey = priceToItemsMap.ceilingKey(lowerB);
                Integer toKey = priceToItemsMap.floorKey(upperB);

                if (fromKey == null || toKey == null) return;

                if (fromKey < toKey) {
                    NavigableMap<Integer, Integer> range = priceToItemsMap.subMap(fromKey, true, toKey, true);
                    int sum = range.values().stream().mapToInt(Integer::intValue).sum();
                }
            }
            finally {
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();

            try {
                priceToItemsMap.put(price, priceToItemsMap.getOrDefault(price, 0) + 1);
            }
            finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                if (priceToItemsMap.containsKey(price)) priceToItemsMap.put(price, priceToItemsMap.get(price) - 1);
                else priceToItemsMap.remove(price);
            } finally {
                writeLock.unlock();
            }

        }
    }
}
