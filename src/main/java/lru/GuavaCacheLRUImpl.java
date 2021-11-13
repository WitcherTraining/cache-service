package lru;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GuavaCacheLRUImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaCacheLRUImpl.class.getName());

    private final long cacheMaxSize;
    private static final Random random = new Random();

    private final Cache<Long, CacheObject> cache;

    public GuavaCacheLRUImpl(final long cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .concurrencyLevel(10)
                .removalListener(getRemovalListener())
                .build();
    }

    private static RemovalListener<Long, CacheObject> getRemovalListener() {
        //Remove key value listener
        return notification -> {
            LOGGER.info(notification.getKey() + " removed");
            //You can get the key, value, and deletion reason in the listener
            notification.getValue();
            notification.getCause();
        };
    }

    // Cache loading...
    public void initLoadingCache() {
        for (long i = 0; i < this.cacheMaxSize; i++) {
            this.cache.put(i, new CacheObject(i, "New Object " + i));
        }
    }

    // Get object from cache permanently
    public CacheObject getCacheIfPresentContinuously(Long key) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            this.cache.getIfPresent(new CacheObject(key, "New Object " + key));
            LOGGER.info("Trying to get object by ID : {}", key);
        }
    }

    // Writing new objects to cache
    public void putCacheContinuously(Long key) throws InterruptedException {
        while (true) {
            Thread.sleep(random.nextInt(10) * 100);
            cache.put(key, new CacheObject(key, "New Object " + key));
            LOGGER.info("Put key : {}, value: {}", key, "New Object " + key);
        }
    }
}
