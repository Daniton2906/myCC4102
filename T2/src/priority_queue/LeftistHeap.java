package priority_queue;

import box.Box;
import utils.Node;

public class LeftistHeap extends AbstractQueue {

    boolean empty = true;
    Node root;
    LeftistHeap lt_izq = null, lt_der = null;
    int h;

    public LeftistHeap() {
        this.root = null;
        this.h = 0;

    }

    private LeftistHeap(int x, int p) {
        root = new Node(x, p);
        this.empty = false;
        this.h = 1;

        this.lt_der = new LeftistHeap();
        this.lt_izq = new LeftistHeap();

    }

    private void copy(LeftistHeap LT) {
        this.root = LT.root;
        this.empty = LT.empty;

        this.lt_der = LT.lt_der;
        this.lt_izq = LT.lt_izq;

    }

    public void insertar(int x, int p){
        LeftistHeap new_heap = new LeftistHeap(x, p);
        LeftistHeap act_heap = new LeftistHeap();
        act_heap.copy(this);

        LeftistHeap result_heap = this.meld(act_heap, new_heap);
        this.copy(result_heap);

    }

    public Node extraer_siguiente(){
        Node top = this.root;

        LeftistHeap new_heap = this.meld(this.lt_der, this.lt_izq);
        this.copy(new_heap);

        return top;
    }

    public boolean isEmpty(){
        return this.empty;
    }

    @Override
    public LeftistHeap meld(LeftistHeap c0, LeftistHeap c1) {
        if(c0.isEmpty()) {
            return c1;
        }

        if(c1.isEmpty()) {
            return c0;
        }

        Node r0 = c0.root, r1 = c1.root;
        LeftistHeap result, new_lt;

        if(r0.getPriority() > r1.getPriority()) {
            result = this.meld(c0.lt_der, c1);

            new_lt = new LeftistHeap();
            new_lt.root = r0;
            if(result.h > c0.lt_izq.h) {
                new_lt.lt_der= c0.lt_izq;
                new_lt.lt_izq = result;
            } else {
                new_lt.lt_der = result;
                new_lt.lt_izq = c0.lt_izq;
            }

            new_lt.h = Math.min(result.h, c0.lt_izq.h) + 1;

        } else {
            result = this.meld(c0, c1.lt_der);

            new_lt = new LeftistHeap();
            new_lt.root = r1;
            if(result.h > c1.lt_izq.h) {
                new_lt.lt_der= c1.lt_izq;
                new_lt.lt_izq = result;
            } else {
                new_lt.lt_der = result;
                new_lt.lt_izq = c1.lt_izq;
            }
            new_lt.h = Math.min(result.h, c1.lt_izq.h) + 1;

        }
        new_lt.empty = false;

        return new_lt;
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createLHBox();
    }
}
