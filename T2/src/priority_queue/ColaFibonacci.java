package priority_queue;

import box.Box;
import utils.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class ColaFibonacci extends AbstractQueue {

    private myLinkedList blforest_list;
    private int count_nodes;

    public ColaFibonacci() {
        this.blforest_list = new myLinkedList();
        this.count_nodes = 0;
    }

    @Override
    public void insertar(int x, int p) {
        this.blforest_list.addLast(new BinomialTree(new Node(x, p)));
        this.count_nodes++;
    }

    @Override
    public Node extraer_siguiente(){
        //System.out.println("Before: " + this.blforest_list);
        BinomialTree max_tree = this.blforest_list.pollMax();
        //System.out.println("Max: " + max_tree);
        Node max_node = max_tree.getNode();
        this.blforest_list.addAll(max_tree.getBinTrees());
        make_binomial_forest();
        //System.out.println("After: " + this.blforest_list);
        this.count_nodes--;
        return max_node;
    }

    @Override
    public boolean isEmpty() {
        return blforest_list.isEmpty();
    }


    @Override
    public PriorityQueue heapify(List<Node> nodes) {
        ColaFibonacci cp = new ColaFibonacci();
        for(Node node : nodes)
            cp.insertar(node.getValue(), node.getPriority());
        return cp;
    }

    @Override
    public ColaFibonacci meld(ColaFibonacci c0, ColaFibonacci c1) {
        ColaFibonacci new_cf = new ColaFibonacci();
        new_cf.blforest_list.addAll(c0.blforest_list);
        new_cf.blforest_list.addAll(c1.blforest_list);

        new_cf.count_nodes = new_cf.blforest_list.getNumberOfNodes();
        return new_cf;
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createCFBox();
    }

    private void make_binomial_forest() {
        Vector<BinomialTree> A = new Vector<>();
        for(int i = 0; i < Math.ceil(Math.log(count_nodes)/Math.log(2)); i++) {
            A.add(null);
        }

        BinomialTree current_tree = null;
        while (!this.blforest_list.isEmpty() || current_tree != null) {
            if(current_tree == null)
                current_tree = this.blforest_list.pollFirst();
            int k = current_tree.getOrder();
            //System.out.println(k);
            if(A.get(k) == null) {
                A.set(k, current_tree);
                current_tree = null;
            } else {
                current_tree = new BinomialTree(A.get(k), current_tree);
                A.set(k, null);
            }
        }
        //System.out.println("A: " + A);

        this.blforest_list = new myLinkedList();
        for(BinomialTree bltree: A)
            if(bltree != null)
                this.blforest_list.addLast(bltree);
    }

}
