package priority_queue;

import box.Box;
import utils.Node;

import java.util.List;
import java.util.Vector;

public class HeapClasico extends AbstractQueue {

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
            swap_nodes(current_parent_index, current_index);
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
                current_child_index = get_child_index(current_index);
        while(current_child_index < this.heap.size()
                && this.heap.get(current_index).getPriority() <= this.heap.get(current_child_index).getPriority()) {
            swap_nodes(current_index, current_child_index);
            current_index = current_child_index;
            current_child_index = get_child_index(current_index);
        }
        return next_node;
    }

    public boolean isEmpty() {
        return this.heap.size() < 2;
    }

    public PriorityQueue heapify(List<Node> nodes) {
        HeapClasico new_queue = new HeapClasico();
        new_queue.heap.addAll(nodes);
        new_queue.heapify(1);
        return new_queue;
    }

    @Override
    public HeapClasico meld(HeapClasico c0, HeapClasico c1) {
        HeapClasico new_hp = new HeapClasico();
        new_hp.heap.addAll(c0.heap);
        new_hp.heap.addAll(c1.heap.subList(1, c1.heap.size()));
        heapify(1);
        return new_hp;
    }

    @Override
    public Box create(BoxFactory boxFactory) {
        return boxFactory.createHCBox();
    }

    private int get_child_index(int current_index){
        int left_index = 2*current_index,
                right_index = 2*current_index + 1;
        if(right_index < this.heap.size()
                && this.heap.get(right_index).getPriority() >= this.heap.get(left_index).getPriority())
            return right_index;
        else
            return left_index;
    }

    private void swap_nodes(int i, int j){
        Node node = this.heap.get(i);
        Node other_node = this.heap.get(j);
        this.heap.set(j, node);
        this.heap.set(i, other_node);
    }

    private void heapify(int index) {
        if(index >= heap.size())
            return;
        heapify(2*index);
        heapify(2*index + 1);
        int child_idx = get_child_index(index);
        if(child_idx < this.heap.size()) {
            int p1 = this.heap.get(index).getPriority(),
                p2 =  this.heap.get(child_idx).getPriority();
            if (p1 <= p2) {
                swap_nodes(index, child_idx);
                heapify(child_idx);
            }
        }
    }

}
