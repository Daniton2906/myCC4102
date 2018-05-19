package box;

import priority_queue.HeapClasico;
import priority_queue.PriorityQueue;

import java.util.LinkedList;

public class HCBox implements Box {

    private LinkedList<HeapClasico> cp_list = new LinkedList<>();

    public void new_queue() {
        cp_list.add(new HeapClasico());
    }

    public PriorityQueue get(int index) {
        return cp_list.get(index);
    }

    public void meld(int i) {
        HeapClasico cp0 = cp_list.remove(i), cp1 = cp_list.remove(i),
                new_cp = cp0.meld(cp0, cp1);
        cp_list.add(i, new_cp);
    }

    public int size() {
        return cp_list.size();
    }

}
