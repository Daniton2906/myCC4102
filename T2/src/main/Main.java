package main;

import priority_queue.*;
import utils.DataManager;
import utils.Tester;

import java.io.IOException;

public class Main {

    private static final int N = 10;

    public static void main (String [ ] args) throws IOException {
        System.out.println("Ejecucion Main");

        DataManager dm = new DataManager(N, 0);
        PriorityQueue heapClasico = new HeapClasico(),
                colaBinomial = new ColaBinomial(),
                colaFibonacci = new ColaFibonacci(),
                leftistheap = new LeftistHeap(),
                skewheap = new SkewHeap();

        System.out.println("  Test heap clasico");
        Tester.test0(heapClasico, dm, false);

        System.out.println("  Test cola binomial");
        Tester.test0(colaBinomial, dm, false);

        System.out.println("  Test cola fibonacci");
        Tester.test0(colaFibonacci, dm, false);

        System.out.println("  Test leftist heap");
        Tester.test0(leftistheap, dm, false);

        System.out.println("  Test skew heap");
        Tester.test0(skewheap, dm, false);
    }
}
