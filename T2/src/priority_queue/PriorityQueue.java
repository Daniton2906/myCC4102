package priority_queue;

import box.Box;
import utils.Node;

import java.util.List;

public interface PriorityQueue {

    void insertar(int x, int p);

    Node extraer_siguiente();

    boolean isEmpty();

    PriorityQueue heapify(List<Node> nodes);

    HeapClasico meld(HeapClasico c0, HeapClasico c1);

    ColaBinomial meld(ColaBinomial c0, ColaBinomial c1);

    ColaFibonacci meld(ColaFibonacci c0, ColaFibonacci c1);

    LeftistHeap meld(LeftistHeap c0, LeftistHeap c1);

    SkewHeap meld(SkewHeap c0, SkewHeap c1);

    Box create(BoxFactory boxFactory);
}
