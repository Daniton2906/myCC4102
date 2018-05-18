package priority_queue;

import utils.Node;

import java.util.LinkedList;

public class ColaBinomial implements PriorityQueue {

    private class Binomial_Tree {

        private LinkedList<Binomial_Tree> binomial_trees;
        private int order;
        private Node node;

        private Binomial_Tree(Node node) {
            this.binomial_trees = new LinkedList<>();
            this.order = 0;
            this.node = node;
        }

        private Binomial_Tree(Binomial_Tree t0, Binomial_Tree t1) {
            assert t0.order == t1.order;
            this.binomial_trees = new LinkedList<>();
            if(t0.node.getPriority() >= t1.node.getPriority()) {
                this.binomial_trees.addAll(t0.binomial_trees);
                this.binomial_trees.addLast(t1);
                this.node = t0.node;
                this.order = t0.order + 1;
            } else {
                this.binomial_trees.addAll(t1.binomial_trees);
                this.binomial_trees.addLast(t0);
                this.node = t1.node;
                this.order = t1.order + 1;
            }
        }
    }

    private LinkedList<Binomial_Tree> binomial_forest;

    public ColaBinomial() {
        this.binomial_forest = new LinkedList<>();
    }

    // suma
    public ColaBinomial(ColaBinomial c0, ColaBinomial c1) {
        int total_nodes = (int) (Math.pow(2, c0.count_nodes()) + Math.pow(2, c1.count_nodes()));

    }

    public void insertar(int x, int p) {}

    public Node extraer_siguiente(){return null;}

    public boolean isEmpty(){
        return true;
    }

    private int count_nodes(){
        int res = 0;
        for (Binomial_Tree bt: binomial_forest) {
            res += (int) Math.pow(2, bt.order);
        }
        return res;
    }

}
