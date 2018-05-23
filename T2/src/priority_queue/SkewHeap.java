package priority_queue;

import box.Box;
import utils.Node;

public class SkewHeap extends AbstractQueue {

    boolean empty = true;
    Node root;
    SkewHeap lt_izq = null, lt_der = null;
    int h_der, h_izq;

    public SkewHeap() {
        this.root = null;
        this.h_der = 0;
        this.h_izq = 0;

    }

    private SkewHeap(int x, int p) {
        root = new Node(x, p);
        this.empty = false;
        this.h_der = 0;
        this.h_izq = 0;

    }

    private void copy(SkewHeap LT) {
        this.root = LT.root;
        this.h_der = LT.h_der;
        this.h_izq = LT.h_izq;

        this.lt_der = LT.lt_der;
        this.lt_izq = LT.lt_izq;
    }

    public void insertar(int x, int p){
        SkewHeap new_heap = new SkewHeap(x, p);

        SkewHeap result_heap = this.meld(this, new_heap);
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
