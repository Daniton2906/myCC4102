package main;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import utils.DataManager;
import utils.Graph;
import utils.Tester;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final int N = 10;

    public static void main (String [ ] args) throws IOException {
        System.out.println("Hello World");

        String results_folder = "/T3/results";

        File dir = new File(new File("").getAbsolutePath() + results_folder);
        System.out.println("Resultados en " + dir);

        if (!dir.exists() && !dir.mkdir())
            System.exit(0);
        System.out.println("Iniciando testing...");

        // Tester.test0();

        int N_TESTS = 1;
        for (int i = 0; i < N_TESTS; i++) {
            System.out.println("Holo");
            Tester.testMinCut();
            /*
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
            */
        }



    }
}
