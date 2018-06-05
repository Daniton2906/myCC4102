package main;

import org.junit.Test;
import priority_queue.*;
import utils.DataManager;
import utils.Tester;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final int N = 10;

    public static void main (String [ ] args) throws IOException {
        System.out.println("Hello World");

        String results_folder = "/T2/results";

        File dir = new File(new File("").getAbsolutePath() + results_folder);
        System.out.println("Resultados en " + dir);

        if (!dir.exists() && !dir.mkdir())
            System.exit(0);
        System.out.println("Iniciando testing...");

        DataManager dm = new DataManager(N, 0);
        PriorityQueue heapClasico = new HeapClasico(),
                colaBinomial = new ColaBinomial(),
                colaFibonacci = new ColaFibonacci(),
                leftistHeap = new LeftistHeap(),
                skewHeap = new SkewHeap();
        //Tester.test0(heapClasico, dm, false);
        //Tester.test0(colaBinomial, dm, false);
        //Tester.test0(colaFibonacci, dm, false);
        //Tester.test0(leftistHeap, dm, false);
        //Tester.test0(skewHeap, dm, false)

        int N_TESTS = 1;
        for (int i = 0; i < N_TESTS; i++) {
            Tester.sort_test(heapClasico, false, "heap-clasico");
            Tester.sort_test(colaBinomial, false, "cola-binomial");
            Tester.sort_test(colaFibonacci, false, "cola-fibonacci");
            Tester.sort_test(leftistHeap, false, "leftist-heap");
            Tester.sort_test(skewHeap, false, "skew-heap");

            Tester.insert_and_melding_test(heapClasico, false, "heap-clasico");
            Tester.insert_and_melding_test(colaBinomial, false, "cola-binomial");
            Tester.insert_and_melding_test(colaFibonacci, false, "cola-fibonacci");
            Tester.insert_and_melding_test(leftistHeap, false, "leftist-heap");
            Tester.insert_and_melding_test(skewHeap, false, "skew-heap");
        }
    }
}
