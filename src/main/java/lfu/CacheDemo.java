package lfu;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheDemo {

    public static final int CAPACITY = 1000;
    private static final CacheImpl CACHE = new CacheImpl(CAPACITY);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Random RANDOM = new Random();
    public static final int MILLIS = 10;
    public static final int FIRST_HUNDRED_ELEMENTS = 100;
    public static final int LONG_MILLIS = 5000;


    public static void main(String[] args) throws InterruptedException {

        // populate cache
        for (int i = 0; i < CACHE.getCapacity(); i++) {
            CACHE.put(i, new CacheObject("Cache object " + i));
        }

        // start parallel read
        for (int i = 0; i < 20; i++) {
            EXECUTOR_SERVICE.submit(() -> {
                while (true) {
                    int key = RANDOM.nextInt(CACHE.getCapacity());
                    Thread.sleep(CAPACITY);
                    CACHE.get(key);
                }
            });
        }
        // start to read first 100 elements permanently
        EXECUTOR_SERVICE.submit(() -> {
            while (true) {
                Thread.sleep(MILLIS);
                CACHE.get(RANDOM.nextInt(FIRST_HUNDRED_ELEMENTS));
            }
        });

        // wait for random access
        System.out.println("waiting...");
        Thread.sleep(CAPACITY);

        // start to write in parallel
        for (int i = 0; i < 2; i++) {
            EXECUTOR_SERVICE.submit(() -> {
                while (true) {
                    int key = getRandomInt();
                    Thread.sleep(RANDOM.nextInt(MILLIS) * FIRST_HUNDRED_ELEMENTS);
                    CACHE.put(key, new CacheObject("Value" + key));
                }
            });
        }

        while (true) {
            Thread.sleep(LONG_MILLIS);
            System.out.println("Log size = " + CACHE.getLog().size());
        }
    }

    private static int getRandomInt() {
        int result = RANDOM.nextInt();
        int exceptExisting = result > CACHE.getCapacity() ? result : result + CACHE.getCapacity();
        if (exceptExisting < 0)
            return getRandomInt();

        return exceptExisting;
    }
}
