package priority_queue;

import box.Box;
import utils.Node;

public class LeftistHeap extends AbstractQueue {

    boolean empty = true;
    Node root;
    LeftistHeap lt_izq = null, lt_der = null;
    int h_der, h_izq;

    public LeftistHeap() {
        this.root = null;
        this.h_der = 0;
        this.h_izq = 0;

    }

    private LeftistHeap(int x, int p) {
        root = new Node(x, p);
        this.empty = false;
        this.h_der = 0;
        this.h_izq = 0;

    }

    private void copy(LeftistHeap LT) {
        this.root = LT.root;
        this.h_der = LT.h_der;
        this.h_izq = LT.h_izq;

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

        return super.meld(c0, c1);
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createLHBox();
    }
}
