package apps;

import box.Box;
import priority_queue.PriorityQueue;
import priority_queue.BoxFactory;
import utils.Node;

import java.util.LinkedList;
import java.util.List;

public class InsertAndMelding {

    private List<Integer> nodes;
    private Box box;
    private BoxFactory factory;
    private int k;

    public InsertAndMelding(int i, List<Integer> nodes) {
        this.k = (int) Math.pow(2, i);
        this.nodes = nodes;
        this.factory = new BoxFactory();
    }

    public void insercion(PriorityQueue cp) {
        assert box == null;
        this.box = factory.create(cp);
        int n = this.nodes.size();
        for(int offset = 0; offset < this.nodes.size(); offset += n/k) {
            this.box.new_queue();
            for(int j = 0; j < n/k; j += 1) {
                int num = this.nodes.get(offset + j);
                this.box.get(box.size() - 1).insertar(num, num);
            }
        }
    }

    public PriorityQueue melding() {
        while(box.size() > 1) {
            int i = 0, n = box.size() / 2;
            while(i < n) {
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
