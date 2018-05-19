package apps;

import box.Box;
import priority_queue.PriorityQueue;
import priority_queue.BoxFactory;
import utils.Node;

import java.util.LinkedList;
import java.util.List;

public class InsertAndMelding {

    private List<List<Node>> nodes_list;
    private Box box;
    private BoxFactory factory;

    public InsertAndMelding(int i, List<Node> nodes) {
        int k = (int) Math.pow(2, i);
        nodes_list = new LinkedList<>();
        for(int j = 0; j < nodes.size(); j += k) {
            nodes_list.add(new LinkedList<>(nodes.subList(j, j + k)));
        }
        factory = new BoxFactory();
    }

    public void insercion(PriorityQueue cp) {
        assert box == null;
        this.box = factory.create(cp);
        for (List<Node> nodes: nodes_list) {
            this.box.new_queue();
            for (Node node: nodes) {
                this.box.get(box.size() - 1).insertar(node.getValue(), node.getPriority());
            }
        }
    }

    public PriorityQueue melding() {
        while(box.size() > 1) {
            int i = 0;
            while(i < box.size() / 2) {
                box.meld(i);
                i++;
            }
        }
        assert box.size() == 1;
        PriorityQueue result = box.get(0);
        box = null;
        return result;
    }

}
