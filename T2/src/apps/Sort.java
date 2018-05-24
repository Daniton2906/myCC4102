package apps;

import priority_queue.PriorityQueue;
import utils.DataManager;
import utils.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Sort {

    private List<Node> nodes;
    private PriorityQueue cp;

    public Sort(List<Integer> array, PriorityQueue cp) {
        this.cp = cp;
        this.nodes = DataManager.toNodeArray(array);
    }

    public void heapify() {
        this.cp = cp.heapify(this.nodes);
    }

    public List<Integer> sort() {
        Vector<Integer> sorted = new Vector<>();
        while (!cp.isEmpty()) {
            sorted.add(cp.extraer_siguiente().getValue());
        }
        return sorted;
    }

}
