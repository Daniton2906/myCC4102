package priority_queue;

import box.Box;
import utils.Node;

import java.util.List;

public class SkewHeap extends AbstractQueue {

    boolean empty = true;
    Node root;
    SkewHeap lt_izq = null, lt_der = null;
    int h;

    public SkewHeap() {
        this.root = null;
        this.h = 0;

    }

    private SkewHeap(int x, int p) {
        root = new Node(x, p);
        this.empty = false;
        this.h = 1;

        this.lt_der = new SkewHeap();
        this.lt_izq = new SkewHeap();

    }

    private void copy(SkewHeap LT) {
        this.root = LT.root;
        this.empty = LT.empty;

        this.lt_der = LT.lt_der;
        this.lt_izq = LT.lt_izq;

    }

    public void insertar(int x, int p){
        SkewHeap new_heap = new SkewHeap(x, p);
        SkewHeap act_heap = new SkewHeap();
        act_heap.copy(this);

        SkewHeap result_heap = this.meld(act_heap, new_heap);
        this.copy(result_heap);

    }

    public Node extraer_siguiente(){
        Node top = this.root;

        SkewHeap new_heap = this.meld(this.lt_der, this.lt_izq);
        this.copy(new_heap);

        return top;
    }

    public boolean isEmpty(){
        return this.empty;
    }

    @Override
    public SkewHeap meld(SkewHeap c0, SkewHeap c1) {
        if(c0.isEmpty()) {
            return c1;
        }

        if(c1.isEmpty()) {
            return c0;
        }

        Node r0 = c0.root, r1 = c1.root;
        SkewHeap result, new_lt;

        if(r0.getPriority() > r1.getPriority()) {
            result = this.meld(c0.lt_der, c1);

            new_lt = new SkewHeap();
            new_lt.root = r0;
            new_lt.lt_der= c0.lt_izq;
            new_lt.lt_izq = result;

            new_lt.h = Math.min(result.h, c0.lt_izq.h) + 1;

        } else {
            result = this.meld(c0, c1.lt_der);

            new_lt = new SkewHeap();
            new_lt.root = r1;
            new_lt.lt_der= c1.lt_izq;
            new_lt.lt_izq = result;

            new_lt.h = Math.min(result.h, c1.lt_izq.h) + 1;

        }
        new_lt.empty = false;

        return new_lt;
    }

    @Override
    public PriorityQueue heapify(List<Node> nodes) {
        SkewHeap cp = new SkewHeap();
        for(Node node : nodes)
            cp.insertar(node.getValue(), node.getPriority());
        return cp;
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createLHBox();
    }
}
