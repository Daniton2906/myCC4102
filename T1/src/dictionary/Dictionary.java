package dictionary;

import utils.DNA;

public interface Dictionary {

    void put(DNA key, long value);

    void delete(DNA key);

    boolean containsKey(DNA key);

    void resetIOCounter();

    int getIOs();

    int getUsedSpace();
}
