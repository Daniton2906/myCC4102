package main;

import utils.DNA;
import utils.DataGenerator;

import java.util.ArrayList;

public class Main {

    static final private long CHAINS = (long) Math.pow(2, 20);

    public static void main (String [ ] args) {
        System.out.println("Empezamos la ejecución del programa");
        DataGenerator rand_data_gen = new DataGenerator();
        long start = System.currentTimeMillis();
        ArrayList<DNA> dna_array = rand_data_gen.generateRandomChains(CHAINS);
        long end = System.currentTimeMillis();
        System.out.print("Tiempo para crear " + CHAINS + " cadenas: " + (end - start));
    }
}
