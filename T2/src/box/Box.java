package box;

import priority_queue.*;

public interface Box {

    void new_queue();

    void meld(int i);

    int size();

    PriorityQueue get(int index);

}
