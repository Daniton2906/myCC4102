package box;

import priority_queue.LeftistHeap;
import priority_queue.PriorityQueue;

import java.util.LinkedList;

public class LHBox implements Box {

    private LinkedList<LeftistHeap> cp_list = new LinkedList<>();

    public void new_queue() {
        cp_list.add(new LeftistHeap());
    }

    public PriorityQueue get(int index) {
        return cp_list.get(index);
    }

    public void meld(int i) {
        LeftistHeap cp0 = cp_list.remove(i), cp1 = cp_list.remove(i),
                new_cp = cp0.meld(cp0, cp1);
        cp_list.add(i, new_cp);
    }

    public int size() {
        return cp_list.size();
    }

}
