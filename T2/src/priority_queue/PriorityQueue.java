package priority_queue;

import utils.Node;

public interface PriorityQueue {

    void insertar(int x, int p);

    Node extraer_siguiente();

    boolean isEmpty();

    PriorityQueue meld(PriorityQueue c0, PriorityQueue c1);
}
