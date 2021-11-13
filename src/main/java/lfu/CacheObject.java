package lfu;

public class CacheObject {

    private String data;

    public CacheObject(String data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "lfu.CacheObject{" +
                "data='" + data + '\'' +
                '}';
    }
}
