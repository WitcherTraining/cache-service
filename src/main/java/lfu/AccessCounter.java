package lfu;

class AccessCounter implements Comparable<AccessCounter> {
    Integer key;
    Integer countOfRequests;
    Long lastTimeAccess;

    public AccessCounter(Integer key, Integer countOfRequests, Long lastTimeAccess) {
        this.key = key;
        this.countOfRequests = countOfRequests;
        this.lastTimeAccess = lastTimeAccess;
    }

    @Override
    public int compareTo(AccessCounter counter) {
        int result = countOfRequests.compareTo(counter.countOfRequests);
        return result != 0 ? result : lastTimeAccess.compareTo(counter.lastTimeAccess);
    }
}