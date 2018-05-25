package priority_queue;

import utils.Node;

import java.util.LinkedList;
import java.util.List;

public class myLinkedList {

    LinkedNode first;
    LinkedNode max;
    private int size;
    private int count_nodes;

    private class LinkedNode {
        private BinomialTree value;
        LinkedNode prev;
        LinkedNode next;

        private LinkedNode(LinkedNode prev, BinomialTree value, LinkedNode next) {
            this.value = value;
            this.prev = prev == null? this : prev;
            this.next = next == null? this : next;
        }

        private LinkedNode(BinomialTree value) {
            this(null, value,null);
        }
    }

    protected myLinkedList(){
        this.first = null;
        this.max = null;
        this.size = 0;
        this.count_nodes = 0;
    }

    protected myLinkedList(List<Node> list) {
        this.first = null;
        this.max = null;
        this.size = 0;
        this.count_nodes = 0;
        for (Node node: list) {
            addLast(new BinomialTree(node));
        }
    }

    protected boolean isEmpty() {
        return size == 0;
    }

    protected void addFirst(BinomialTree bltree) {
        if(isEmpty()) {
            this.first = new LinkedNode(bltree);
            this.max = this.first;
        } else {
            LinkedNode new_node = new LinkedNode(this.first.prev, bltree, this.first);
            new_node.prev.next = new_node;
            this.first.prev = new_node;
            this.first = new_node;
            this.max = bltree.compareTo(this.max.value) > 0? new_node : this.max;
        }
        this.count_nodes += bltree.numberOfNodes();
        size++;
    }

    protected void addLast(BinomialTree bltree) {
        if(isEmpty()) {
            this.first = new LinkedNode(bltree);
            this.max = this.first;
        } else {
            LinkedNode new_node = new LinkedNode(this.first.prev, bltree, this.first);
            new_node.prev.next = new_node;
            this.first.prev = new_node;

            this.max = bltree.compareTo(this.max.value) > 0? new_node : this.max;
        }
        this.count_nodes += bltree.numberOfNodes();
        size++;
    }

    protected BinomialTree pollFirst() {
        if(isEmpty()) {
            return null;
        } else {
            BinomialTree bltree = this.first.value;
            if(this.first.equals(this.max))
                this.max = null;
            if (size > 1) {
                this.first.next.prev = this.first.prev;
                this.first.prev.next = this.first.next;
                this.first = this.first.next;
            } else {
                this.first = null;
            }
            this.count_nodes -= bltree.numberOfNodes();
            size--;
            return bltree;
        }
    }

    protected BinomialTree pollLast() {
        if(isEmpty()) {
            return null;
        } else {
            BinomialTree bltree = this.first.prev.value;
            if(this.first.prev.equals(this.max))
                this.max = null;
            if (size > 1) {
                this.first.prev.prev.next = this.first;
                this.first.prev = this.first.prev.prev;

            } else {
                this.first = null;
            }
            this.count_nodes -= bltree.numberOfNodes();
            size--;
            return bltree;
        }
    }

    protected BinomialTree pollMax() {
        if(this.max == null)
            return null;
        BinomialTree bltree = this.max.value;
        if(size > 1) {
            if(this.max.equals(this.first))
                this.first = this.first.next;
            this.max.prev.next = this.max.next;
            this.max.next.prev = this.max.prev;
        } else {
            this.first = null;
        }
        this.max = null;
        this.count_nodes -= bltree.numberOfNodes();
        size--;
        return bltree;
    }

    protected void addAll(myLinkedList other_list) {
        if(this.isEmpty()) {
            this.first = other_list.first;
            this.size = other_list.size;
            this.count_nodes = other_list.count_nodes;
            this.max = other_list.max;
        } else if(!other_list.isEmpty()) {
            assert this.max != null && other_list.max != null;
            LinkedNode prev_last = this.first.prev,
                    prev_first = other_list.first,
                    new_last = prev_first.prev;
            this.first.prev = new_last;
            new_last.next = this.first;
            prev_last.next = prev_first;
            prev_first.prev = prev_last;

            this.size += other_list.size;
            this.count_nodes += other_list.count_nodes;
            this.max = other_list.max.value.compareTo(this.max.value) > 0? other_list.max : this.max;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        LinkedNode aux = this.first;
        while(i < size) {
            sb.append(aux.value.getNode());
            sb.append(" ");
            i++;
            aux = aux.next;
        }
        return sb.toString();
    }

    protected int getNumberOfNodes() {
        return this.count_nodes;
    }

    protected int size() {
        return this.size;
    }
}
