package apps;

import priority_queue.PriorityQueue;
import utils.Node;

import java.util.List;

public class Sort {

    private List<Node> nodes;
    private PriorityQueue cp;

    public Sort(List<Node> nodes, PriorityQueue cp) {
        this.nodes = nodes;
        this.cp = cp.heapify(nodes);
    }

    public void sort() {
        int k = 0;
        while (!cp.isEmpty()) {
            nodes.set(k, cp.extraer_siguiente());
            k++;
        }
    }

}
