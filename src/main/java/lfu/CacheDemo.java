package lfu;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheDemo {

    private static final LFUImpl CACHE = new LFUImpl(1000);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Random RANDOM = new Random();


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
                    Thread.sleep(1000);
                    CACHE.get(key);
                }
            });
        }
        // start to read first 100 elements permanently
        EXECUTOR_SERVICE.submit(() -> {
            while (true) {
                Thread.sleep(10);
                CACHE.get(RANDOM.nextInt(100));
            }
        });

        // wait for random access
        System.out.println("waiting...");
        Thread.sleep(1000);

        // start to write in parallel
        for (int i = 0; i < 2; i++) {
            EXECUTOR_SERVICE.submit(() -> {
                while (true) {
                    int key = getRandomInt();
                    Thread.sleep(RANDOM.nextInt(10) * 100);
                    CACHE.put(key, new CacheObject("Value" + key));
                }
            });
        }

        while (true) {
            Thread.sleep(5000);
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
