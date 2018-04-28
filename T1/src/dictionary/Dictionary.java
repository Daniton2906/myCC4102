package dictionary;

public interface Dictionary {

    void put(String key, long value);

    long get(String key);

    void delete(String key);

    boolean containsKey(String key);
}
