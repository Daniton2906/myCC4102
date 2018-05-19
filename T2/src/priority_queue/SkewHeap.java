package priority_queue;

import box.Box;
import utils.Node;

public class SkewHeap extends AbstractQueue {

    public void insertar(int x, int p){}

    public Node extraer_siguiente(){return null;}

    public boolean isEmpty(){
        return true;
    }

    @Override
    public SkewHeap meld(SkewHeap c0, SkewHeap c1) {
        return super.meld(c0, c1);
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createSHBox();
    }
}
