package box;

import priority_queue.ColaBinomial;
import priority_queue.PriorityQueue;
import utils.Node;

import java.util.LinkedList;

public class CBBox implements Box {

    private LinkedList<ColaBinomial> cp_list = new LinkedList<>();

    public void new_queue() {
        cp_list.add(new ColaBinomial());
    }

    public PriorityQueue get(int index) {
        return cp_list.get(index);
    }

    public void meld(int i) {
        ColaBinomial cp0 = cp_list.remove(i), cp1 = cp_list.remove(i),
                new_cp = cp0.meld(cp0, cp1);
        cp_list.add(i, new_cp);
    }

    public int size() {
        return cp_list.size();
    }

}
