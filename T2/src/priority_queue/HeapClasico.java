package priority_queue;

import utils.Node;

import java.util.Vector;

public class HeapClasico implements PriorityQueue {

    private Vector<Node> heap;

    public HeapClasico(){
        heap = new Vector<>();
        heap.add(null);
    }

    public void insertar(int x, int p){
        Node new_node = new Node(x, p);
        this.heap.add(new_node);
        int current_index = this.heap.size() - 1,
                current_parent_index = (int) Math.floor(current_index / 2.0);
        // emerger
        while(current_parent_index != 0
                && new_node.getPriority() > this.heap.get(current_parent_index).getPriority()) {
            Node parent = this.heap.get(current_parent_index);
            this.heap.set(current_parent_index, new_node);
            this.heap.set(current_index, parent);
            current_index = current_parent_index;
            current_parent_index = (int) Math.floor(current_index / 2.0);
        }
    }

    public Node extraer_siguiente(){
        if(this.heap.size() < 3) {
            return this.heap.size() > 1? this.heap.remove(1) : null;
        }
        Node next_node = this.heap.get(1);
        this.heap.set(1, this.heap.remove(this.heap.size() - 1));
        int current_index = 1,
                left_index = 2*current_index,
                right_index = 2*current_index + 1;
        int current_child_index = right_index < this.heap.size()
                && this.heap.get(right_index).getPriority()
                    >= this.heap.get(left_index).getPriority()? right_index: left_index;
        while(current_child_index < this.heap.size()
                && this.heap.get(current_index).getPriority() <= this.heap.get(current_child_index).getPriority()) {
            Node currentNode = this.heap.get(current_index);
            Node currentChildNode = this.heap.get(current_child_index);
            this.heap.set(current_child_index, currentNode);
            this.heap.set(current_index, currentChildNode);
        }
        return next_node;
    }

    public boolean isEmpty(){
        return this.heap.size() < 2;
    }

    public PriorityQueue meld(PriorityQueue c0, PriorityQueue c1) {
        PriorityQueue new_pqueue = new HeapClasico();
        Node node;
        while((node = c0.extraer_siguiente()) != null) {
            new_pqueue.insertar(node.getValue(), node.getPriority());
        }
        while((node = c1.extraer_siguiente()) != null) {
            new_pqueue.insertar(node.getValue(), node.getPriority());
        }
        return new_pqueue;
    }

}
