package priority_queue;

import box.Box;
import utils.Node;

import java.util.LinkedList;

public class ColaBinomial extends AbstractQueue {

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

    private LinkedList<Binomial_Tree> binomial_forest;
    private int max_index;

    public ColaBinomial() {
        this.binomial_forest = new LinkedList<>();
        this.max_index = -1;
    }

    private ColaBinomial(Node node) {
        this.binomial_forest = new LinkedList<>();
        this.binomial_forest.add(new Binomial_Tree(node));
        this.max_index = 0;
    }

    private ColaBinomial(ColaBinomial c0, ColaBinomial c1) {
        ColaBinomial nueva_cola = suma(c0.binomial_forest, c1.binomial_forest);
        this.binomial_forest = nueva_cola.binomial_forest;
        this.max_index = nueva_cola.max_index;
    }

    public void insertar(int x, int p) {
        ColaBinomial cola0 =  new ColaBinomial(new Node(x, p)),
                nueva_cola = suma(this.binomial_forest, cola0.binomial_forest);
        this.binomial_forest = nueva_cola.binomial_forest;
        this.max_index = nueva_cola.max_index;
    }

    public Node extraer_siguiente(){
        Binomial_Tree max_tree = this.binomial_forest.remove(this.max_index);
        Node max_node = max_tree.node;
        ColaBinomial nueva_cola = suma(this.binomial_forest, max_tree.binomial_trees);
        this.binomial_forest = nueva_cola.binomial_forest;
        this.max_index = nueva_cola.max_index;
        return max_node;
    }

    public boolean isEmpty(){
        return this.binomial_forest.isEmpty();
    }

    private ColaBinomial suma(LinkedList<Binomial_Tree> bfX, LinkedList<Binomial_Tree> bfY) {
        ColaBinomial new_cola = new ColaBinomial();
        int countX = count_nodes(bfX), countY = count_nodes(bfY),
                iX = 0, iY = 0, k = 0,
                max_value = -1, max_index = -1;
        LinkedList<Binomial_Tree> T = new LinkedList<>(), cS = new LinkedList<>();
        while(countX > 0 || countY > 0 || T.size() > 0) {
            boolean xHasBk = countX > 0 && bfX.get(iX).order == k,
                    yHasBk = countY > 0 && bfY.get(iY).order == k;
            if (xHasBk) {
                T.add(bfX.get(iX));
                countX -= Math.pow(2, k);
                iX++;
            } if (yHasBk) {
                T.add(bfY.get(iY));
                countY -= Math.pow(2, k);
                iY++;
            } k++;
            if (T.size() == 1) {
                Binomial_Tree new_bltree = T.remove();
                cS.add(new_bltree);
                int value = new_bltree.node.getPriority();
                if(max_index == -1 || value >= max_value) {
                    max_value = value;
                    max_index = cS.size() - 1;
                }
            } else if (T.size() == 2) {
                T.add(new Binomial_Tree(T.remove(), T.remove()));
            } else if (T.size() == 3) {
                Binomial_Tree new_bltree = T.remove();
                cS.add(new_bltree);
                int value = new_bltree.node.getPriority();
                if(max_index == -1 || value >= max_value) {
                    max_value = value;
                    max_index = cS.size() - 1;
                }
                T.add(new Binomial_Tree(T.remove(), T.remove()));
            }
        }
        new_cola.max_index = max_index;
        new_cola.binomial_forest = cS;
        return new_cola;
    }

    private int count_nodes(LinkedList<Binomial_Tree> bl_forest){
        int res = 0;
        for (Binomial_Tree bt: bl_forest) {
            res += (int) Math.pow(2, bt.order);
        }
        return res;
    }

    @Override
    public ColaBinomial meld(ColaBinomial c0, ColaBinomial c1) {
        return new ColaBinomial(c0, c1);
    }

    @Override
    public Box create(BoxFactory boxFactory) {
        return boxFactory.createCBBox();
    }

}
