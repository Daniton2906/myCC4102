package priority_queue;

import utils.Node;

import java.util.LinkedList;

public class BinomialTree implements Comparable<BinomialTree> {

    private myLinkedList binomial_trees;
    private int order;
    private Node node;
    private int count_nodes;


    protected BinomialTree(Node node) {
        this.binomial_trees = new myLinkedList();
        this.order = 0;
        this.node = node;
        this.count_nodes = 1;
    }

    protected BinomialTree(BinomialTree t0, BinomialTree t1) {
        assert t0.order == t1.order;
        this.binomial_trees = new myLinkedList();
        if (t0.node.getPriority() >= t1.node.getPriority()) {
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
        this.count_nodes = t0.count_nodes + t1.count_nodes;
    }

    protected myLinkedList getBinTrees() {
        return binomial_trees;
    }

    protected Node getNode() {
        return node;
    }

    protected int getOrder() {
        return order;
    }

    protected int numberOfNodes() {
        return count_nodes;
    }

    @Override
    public String toString() {
        return "B[" + this.order + "] root " + this.node + ": (" + this.binomial_trees + ")";
    }

    @Override
    public int compareTo(BinomialTree o) {
        return Integer.compare(this.node.getPriority(), o.node.getPriority());
    }
}
