package lfu;

import java.util.*;

public class CacheImpl {

    private final int CAPACITY;
    private final Map<Integer, AccessCounter> META_INFO = new HashMap<>();
    private final Map<Integer, CacheObject> CACHE = new HashMap<>();
    private final List<CacheObject> LOG = new ArrayList<>();

    public CacheImpl(int capacity) {
        this.CAPACITY = capacity;
    }

    public void put(int key, CacheObject cacheObject) {
        CacheObject value = CACHE.get(key);
        if (value == null) {
            if (META_INFO.size() >= CAPACITY) {
                Integer k = this.getLessRequestedKey();
                LOG.add(CACHE.remove(k));
                META_INFO.remove(k);
            }
            META_INFO.put(key, new AccessCounter(key, 1, System.nanoTime()));
        } else {
            AccessCounter counter = META_INFO.get(key);
            counter.countOfRequests++;
            counter.lastTimeAccess = System.nanoTime();
        }
        CACHE.put(key, cacheObject);
    }

    public CacheObject get(Integer key) {
        CacheObject value = CACHE.get(key);
        if (value != null) {
            AccessCounter counter = META_INFO.get(key);
            counter.countOfRequests++;
            counter.lastTimeAccess = System.nanoTime();
            return value;
        }
        return null;
    }

    private Integer getLessRequestedKey() {
        AccessCounter lessRequested = Collections.min(META_INFO.values());
        return lessRequested.key;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public List<CacheObject> getLog() {
        return LOG;
    }
}
