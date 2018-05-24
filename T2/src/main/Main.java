package main;

import org.junit.Test;
import priority_queue.ColaBinomial;
import priority_queue.ColaFibonacci;
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
        PriorityQueue heapClasico = new HeapClasico(),
                colaBinomial = new ColaBinomial(),
                colaFibonacci = new ColaFibonacci();
        //Tester.test0(heapClasico, dm, false);
        //Tester.test0(colaBinomial, dm, false);
        //Tester.test0(colaFibonacci, dm, false);

        //Tester.sort_test(heapClasico, false, "heap-clasico");
        //Tester.sort_test(colaBinomial, false, "cola-binomial");
        //Tester.sort_test(colaFibonacci, false, "cola-fibonacci");

        Tester.insert_and_melding_test(heapClasico, false, "heap-clasico");
        Tester.insert_and_melding_test(colaBinomial, false, "cola-binomial");
        Tester.insert_and_melding_test(colaFibonacci, false, "cola-fibonacci");
    }
}
