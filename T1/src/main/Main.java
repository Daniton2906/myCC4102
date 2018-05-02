package main;

import utils.DNA;
import utils.DataGenerator;
import utils.FileManager;

import java.io.*;
import java.util.ArrayList;

public class Main {

    static final private long CHAINS = (long) Math.pow(2, 20);
    static final private int B = 10;
    static final private String btree_filename = "/tmp/btree_data.ser";

    public static void main (String [ ] args) {
        System.out.println("Empezamos la ejecución del programa");

        DataGenerator rand_data_gen = new DataGenerator();
        long start = System.currentTimeMillis();
        ArrayList<DNA> dna_array = rand_data_gen.generateRandomChains(CHAINS);
        long end = System.currentTimeMillis();
        System.out.println("Tiempo para crear " + CHAINS + " cadenas: " + (end - start));

        File fd = new File(System.getProperty("user.dir") + btree_filename);
        FileManager fm = new FileManager(B + 1, fd);
        System.out.println(System.getProperty("user.dir") + btree_filename);
        fm.test(dna_array, 10); //(int) Math.pow(2, 20));
    }
}
