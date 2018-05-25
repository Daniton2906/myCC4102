package priority_queue;

import box.Box;
import utils.Node;

import java.util.LinkedList;
import java.util.List;

public class ColaBinomial extends AbstractQueue {

    private myLinkedList binomial_forest;
    private int max_index;
    private int node_counter;

    public ColaBinomial() {
        this.binomial_forest = new myLinkedList();
        this.max_index = -1;
        this.node_counter = 0;
    }

    private ColaBinomial(Node node) {
        this.binomial_forest = new myLinkedList();
        this.binomial_forest.addFirst(new BinomialTree(node));
        this.max_index = 0;
        this.node_counter = 1;
    }

    @Override
    public void insertar(int x, int p) {
        ColaBinomial cola0 =  new ColaBinomial(new Node(x, p)),
                nueva_cola = suma(this.binomial_forest, cola0.binomial_forest);
        this.binomial_forest = nueva_cola.binomial_forest;
        this.max_index = nueva_cola.max_index;
        this.node_counter++;
    }

    @Override
    public Node extraer_siguiente(){
        BinomialTree max_tree = this.binomial_forest.pollMax();
        Node max_node = max_tree.getNode();
        ColaBinomial nueva_cola = suma(this.binomial_forest, max_tree.getBinTrees());
        this.binomial_forest = nueva_cola.binomial_forest;
        this.max_index = nueva_cola.max_index;
        this.node_counter = nueva_cola.node_counter;
        return max_node;
    }

    public boolean isEmpty(){
        return this.binomial_forest.isEmpty();
    }

    @Override
    public ColaBinomial meld(ColaBinomial c0, ColaBinomial c1) {
        return suma(c0.binomial_forest, c1.binomial_forest);
    }

    @Override
    public Box create(BoxFactory boxFactory) {
        return boxFactory.createCBBox();
    }

    @Override
    public PriorityQueue heapify(List<Node> nodes) {
        ColaBinomial cp = new ColaBinomial();
        for(Node node : nodes)
            cp.insertar(node.getValue(), node.getPriority());
        return cp;
    }

    private ColaBinomial suma(myLinkedList bfX, myLinkedList bfY) {
        ColaBinomial new_cola = new ColaBinomial();
        int k = 0, countX = bfX.getNumberOfNodes(), countY = bfY.getNumberOfNodes();
        myLinkedList T = new myLinkedList(), cS = new myLinkedList();
        BinomialTree bltreeX = bfX.pollFirst(), bltreeY = bfY.pollFirst();

        while(countX > 0 || countY > 0 || T.size() > 0) {
            boolean xHasBk = countX > 0 && bltreeX.getOrder() == k,
                    yHasBk = countY > 0 && bltreeY.getOrder() == k;
            if (xHasBk) {
                T.addLast(bltreeX);
                countX -= Math.pow(2, k);
                bltreeX = bfX.pollFirst();
            } if (yHasBk) {
                T.addLast(bltreeY);
                countY -= Math.pow(2, k);
                bltreeY = bfY.pollFirst();
            } k++;
            if (T.size() == 1) {
                BinomialTree new_bltree = T.pollFirst();
                cS.addLast(new_bltree);
            } else if (T.size() == 2) {
                T.addLast(new BinomialTree(T.pollFirst(), T.pollFirst()));
            } else if (T.size() == 3) {
                BinomialTree new_bltree = T.pollFirst();
                cS.addLast(new_bltree);
                T.addLast(new BinomialTree(T.pollFirst(), T.pollFirst()));
            }
        }
        new_cola.binomial_forest = cS;
        return new_cola;
    }

}
