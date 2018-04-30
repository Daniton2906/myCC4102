package dictionary;

import utils.DNA;

public interface Dictionary {

    void put(DNA key, long value);

    long get(DNA key);

    void delete(DNA key);

    boolean containsKey(DNA key);
}
