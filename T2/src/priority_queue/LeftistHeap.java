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

        this.lt_der = LT.lt_der;
        this.lt_izq = LT.lt_izq;

    }

    public void insertar(int x, int p){
        LeftistHeap new_heap = new LeftistHeap(x, p);

        LeftistHeap result_heap = this.meld(this, new_heap);
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
        if(c0.isEmpty())
            return c1;
        if(c1.isEmpty())
            return c0;

        Node r0 = c0.root, r1 = c1.root;
        LeftistHeap result, new_lt;

        if(r0.getPriority() > r1.getPriority()) {
            result = this.meld(c0.lt_der, c1);
            c0.lt_der = result;

            new_lt = c0;
            new_lt.h = Math.min(c0.lt_der.h, c0.lt_izq.h) + 1;
        } else {
            result = this.meld(c1.lt_der, c0);
            c1.lt_der = result;

            new_lt = c1;
            new_lt.h = Math.min(c1.lt_der.h, c1.lt_izq.h) + 1;
        }

        if(new_lt.lt_der.h > new_lt.lt_izq.h) {
            LeftistHeap aux = new_lt.lt_der;
            new_lt.lt_der = new_lt.lt_izq;
            new_lt.lt_izq = aux;

        }

        return new_lt;
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createLHBox();
    }
}
