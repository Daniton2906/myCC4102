package box;

import priority_queue.ColaFibonacci;
import priority_queue.PriorityQueue;

import java.util.LinkedList;

public class CFBox implements Box {

    private LinkedList<ColaFibonacci> cp_list = new LinkedList<>();

    public void new_queue() {
        cp_list.add(new ColaFibonacci());
    }

    public PriorityQueue get(int index) {
        return cp_list.get(index);
    }

    public void meld(int i) {
        ColaFibonacci cp0 = cp_list.remove(i), cp1 = cp_list.remove(i),
                new_cp = cp0.meld(cp0, cp1);
        cp_list.add(i, new_cp);
    }

    public int size() {
        return cp_list.size();
    }

}
