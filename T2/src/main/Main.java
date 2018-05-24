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

        //Tester.test0(heapClasico, dm, false);
        //Tester.test0(colaBinomial, dm, false);
        Tester.test0(colaFibonacci, dm, false);
        //Tester.test0(letistheap, dm, false);
        //Tester.test0(skewheap, dm, false);
    }
}
