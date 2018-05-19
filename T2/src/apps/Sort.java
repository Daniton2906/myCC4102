package apps;

import priority_queue.HeapClasico;
import priority_queue.PriorityQueue;
import utils.Node;

import java.util.List;

public class Sort {

    private List<Node> nodes;
    private PriorityQueue cp;

    public Sort(List<Node> nodes, PriorityQueue cp) {
        this.nodes = nodes;
        this.cp = cp;
        for(Node node : nodes)
            cp.insertar(node.getValue(), node.getPriority());

    }

    public Sort(List<Node> nodes) {
        this.nodes = nodes;
        this.cp = new HeapClasico(nodes);
    }

    public void sort() {
        int k = 0;
        while (!cp.isEmpty()) {
            nodes.set(k, cp.extraer_siguiente());
        }
    }

}
