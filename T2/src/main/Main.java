package main;

import org.junit.Test;
import priority_queue.*;
import utils.DataManager;
import utils.Tester;

import java.io.IOException;

public class Main {

    private static final int N = 10;

    public static void main (String [ ] args) throws IOException {
        System.out.println("Hello World");

        DataManager dm = new DataManager(N, 0);
        PriorityQueue heapClasico = new HeapClasico(),
                colaBinomial = new ColaBinomial(),
                colaFibonacci = new ColaFibonacci(),
                leftistHeap = new LeftistHeap(),
                skewHeap = new SkewHeap();
        //Tester.test0(heapClasico, dm, false);
        //Tester.test0(colaBinomial, dm, false);
        //Tester.test0(colaFibonacci, dm, false);

       // Tester.sort_test(heapClasico, false, "heap-clasico");
        Tester.sort_test(colaBinomial, false, "cola-binomial");
        Tester.sort_test(colaFibonacci, false, "cola-fibonacci");
        //Tester.sort_test(leftistHeap, false, "leftist-heap");
        //Tester.sort_test(skewHeap, false, "skew-heap");

        //Tester.insert_and_melding_test(heapClasico, false, "heap-clasico");
        Tester.insert_and_melding_test(colaBinomial, false, "cola-binomial");
        Tester.insert_and_melding_test(colaFibonacci, false, "cola-fibonacci");
        //Tester.insert_and_melding_test(leftistHeap, false, "leftist-heap");
        //Tester.insert_and_melding_test(skewHeap, false, "skew-heap");
    }
}
