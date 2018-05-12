package main;

import priority_queue.HeapClasico;
import priority_queue.PriorityQueue;
import utils.Node;
import utils.Tester;

import java.io.IOException;

public class Main {

    public static void main (String [ ] args) throws IOException {
        System.out.println("Hello World");
        Node n1 = new Node(100, 0),
                n2 = new Node(50, 1),
                n3 = new Node(75, 2);
        System.out.println(n1.compareTo(n2));
        System.out.println(n2.compareTo(n3));
        System.out.println(n3.compareTo(n1));
        PriorityQueue heapClasico = new HeapClasico();
        Tester.test0(heapClasico, 10);
    }
}
