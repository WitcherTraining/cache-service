package lru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LRUCacheDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(LRUCacheDemo.class.getName());
    private static final long CACHE_MAX_SIZE = 10000L;


    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {

        GuavaCacheLRUImpl cacheDemo = new GuavaCacheLRUImpl(CACHE_MAX_SIZE);
        cacheDemo.initLoadingCache();

        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                try {
                    cacheDemo.getCacheIfPresentContinuously((long) random.nextInt((int) CACHE_MAX_SIZE));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // start to read first 100 elements permanently
        executorService.submit(() -> {
            try {
                cacheDemo.getCacheIfPresentContinuously((long) random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // wait for random access
        LOGGER.info("Waiting...");
        Thread.sleep(10000);


        // start to write in parallel
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    cacheDemo.putCacheContinuously(getRandomLongWithoutExisting());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Long getRandomLongWithoutExisting() {
        long result = random.nextLong();
        long exceptExisting = result > CACHE_MAX_SIZE ? result : result + CACHE_MAX_SIZE;
        if (exceptExisting < 0)
            return getRandomLongWithoutExisting();

        return exceptExisting;
    }
}
