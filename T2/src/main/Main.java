package main;

import priority_queue.HeapClasico;
import priority_queue.PriorityQueue;
import utils.DataManager;
import utils.Tester;

import java.io.IOException;

public class Main {

    private static final int N = 10;

    public static void main (String [ ] args) throws IOException {
        System.out.println("Hello World");

        DataManager dm = new DataManager(N, 0);
        PriorityQueue heapClasico = new HeapClasico();
        Tester.test0(heapClasico, dm, false);
    }
}
