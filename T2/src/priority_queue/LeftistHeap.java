package priority_queue;

import box.Box;
import utils.Node;

import java.util.List;

public class LeftistHeap extends AbstractQueue {

    @Override
    public void insertar(int x, int p){}

    @Override
    public Node extraer_siguiente(){return null;}

    @Override
    public boolean isEmpty(){
        return true;
    }

    @Override
    public PriorityQueue heapify(List<Node> nodes) {
        return null;
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
