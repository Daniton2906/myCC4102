package main;

import utils.DNA;
import utils.DataGenerator;

import java.util.ArrayList;

public class Main {

    static final private long CHAINS = 0;

    public static void main (String [ ] args) {
        System.out.println("Empezamos la ejecución del programa");

        ArrayList<DNA> dna_array = DataGenerator.generateRandomChains(CHAINS);
    }
}
