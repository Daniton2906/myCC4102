package priority_queue;

import utils.Node;

import java.util.LinkedList;

public class ColaFibonacci implements PriorityQueue {

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

        @Override
        public String toString() {
            return "B[" + this.order + "] root " + this.node + ":" + this.binomial_trees;
        }
    }

    private LinkedList<Binomial_Tree> blforest_list;
    private int count_nodes;
    private int max_index;

    public ColaFibonacci() {
        this.blforest_list = new LinkedList<>();
        this.max_index = -1;
        this.count_nodes = 0;
    }

    public ColaFibonacci(ColaFibonacci c0, ColaFibonacci c1) {
        this.blforest_list = new LinkedList<>();
        this.blforest_list.addAll(c0.blforest_list); this.blforest_list.addAll(c1.blforest_list);

        Node node0 = c0.blforest_list.get(c0.max_index).node,
                node1 = c1.blforest_list.get(c1.max_index).node;
        if(node0.getPriority() >= node1.getPriority())
            this.max_index = c0.max_index;
        else
            this.max_index = c0.blforest_list.size() + c1.max_index - 1;

        this.count_nodes = c0.count_nodes + c1.count_nodes;
    }

    public void insertar(int x, int p) {
        this.blforest_list.add(new Binomial_Tree(new Node(x, p)));
        if(max_index == -1 || p >= this.blforest_list.get(max_index).node.getPriority()) {
            this.max_index = this.blforest_list.size() - 1;
        }
        count_nodes++;
    }

    public Node extraer_siguiente(){
        Binomial_Tree max_tree = this.blforest_list.remove(this.max_index);
        Node max_node = max_tree.node;
        this.max_index = -1;
        this.blforest_list.addAll(max_tree.binomial_trees);
        make_binomial_forest();
        this.count_nodes--;
        return max_node;
    }

    public boolean isEmpty() {
        return blforest_list.isEmpty();
    }

    private void make_binomial_forest() {
        LinkedList<Binomial_Tree> A = new LinkedList<>();
        for(int i = 0; i < Math.ceil(Math.log(count_nodes)/Math.log(2)); i++) {
            A.add(null);
        }

        Binomial_Tree current_tree = null;
        int j = 0;
        while (j < this.blforest_list.size()) {
            if(current_tree == null)
                current_tree = this.blforest_list.get(j);
            int k = current_tree.order;
            if(A.get(k) == null) {
                A.set(k, current_tree);
                current_tree = null;
                j++;
            } else {
                current_tree = new Binomial_Tree(A.get(k), current_tree);
                A.set(k, null);
            }
        }

        this.blforest_list = new LinkedList<>();
        for(Binomial_Tree bltree: A) {
            if(bltree != null) {
                this.blforest_list.add(bltree);
                if (max_index == -1 ||
                        bltree.node.getPriority() >= this.blforest_list.get(max_index).node.getPriority()) {
                    max_index = this.blforest_list.size() - 1;
                }
            }
        }
    }

}
