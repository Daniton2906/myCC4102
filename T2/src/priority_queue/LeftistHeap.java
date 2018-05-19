package priority_queue;

import box.Box;
import utils.Node;

public class LeftistHeap extends AbstractQueue {

    public void insertar(int x, int p){}

    public Node extraer_siguiente(){return null;}

    public boolean isEmpty(){
        return true;
    }

    @Override
    public LeftistHeap meld(LeftistHeap c0, LeftistHeap c1) {
        return super.meld(c0, c1);
    }

    @Override
    public Box create(BoxFactory factory) {
        return factory.createLHBox();
    }
}
