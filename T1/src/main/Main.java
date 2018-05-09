package main;

import dictionary.BTreeDict;
import dictionary.Dictionary;
import dictionary.ExtHashDict;
import dictionary.LinealHashDict;
import utils.DNA;
import utils.DataGenerator;
import utils.FileManager;
import utils.Tester;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {

    static final private long CHAINS = (long) Math.pow(2, 20);
    static final private int B = 512;
    static final private String btree_filename = "/T1/tmp/btree_data.ser";
    static final private String exth_filename = "/T1/tmp/exth_data.ser";
    static final private String linh0_filename = "/T1/tmp/linh0_data.ser";
    static final private String linh1_filename = "/T1/tmp/linh1_data.ser";

    public static void main (String [ ] args) throws IOException {
        System.out.println("Empezamos la ejecución del programa");

        DataGenerator rand_data_gen = new DataGenerator();
        long start = System.currentTimeMillis();
        ArrayList<DNA> dna_array = rand_data_gen.generateRandomChains(CHAINS);
        long end = System.currentTimeMillis();
        System.out.println("Tiempo para crear " + CHAINS + " cadenas: " + (end - start));

        String pathname_btree = System.getProperty("user.dir") + btree_filename;
        String pathname_exth = System.getProperty("user.dir") + exth_filename;
        String pathname_linh0 = System.getProperty("user.dir") + linh0_filename;
        String pathname_linh1 = System.getProperty("user.dir") + linh1_filename;

        //File fd = new File(pathname_btree);
        //System.out.println(fd.getAbsolutePath());
        //FileManager fm = new FileManager(B, fd);
        //Dictionary btree = new BTreeDict(pathname, B, false);
        //Dictionary exth = new ExtHashDict(pathname_exth, B, false);
        //Dictionary linh0 = new LinealHashDict(pathname_linh0, B, 0, true);
        //Dictionary linh1 = new LinealHashDict(pathname_linh1, B, 1, false);

        /*Tester.test0(fm, dna_array, 0, B, 0);
        Tester.test0(fm, dna_array, B, 3*B/2, 1);
        Tester.test0(fm, dna_array, 2*B, 3*B, 2);
        Tester.test0(fm, dna_array, 3*B, 7*B/2, 3);
        Tester.test0(fm, dna_array, 4*B, 5*B, 1);
        ArrayList<Integer> L = new ArrayList<>();
        L.add(dna_array.get(5*B).hashCode());
        fm.append(L);*/

        int n = (int) CHAINS;
        //Tester.test1(btree, dna_array, n);
        int n_test= 1;
        for(int i = 0; i< n_test; i++) {
            Dictionary btree = new BTreeDict(pathname_btree, B, false);
            Tester.test2(btree, dna_array, rand_data_gen, "btree" + i + "-");
        }
        for(int i = 0; i< n_test; i++) {
            Dictionary exth = new ExtHashDict(pathname_exth, B, false);
            Tester.test2(exth, dna_array, rand_data_gen, "exth" + i + "-");
        }
        for(int i = 0; i< n_test; i++) {
            Dictionary linh1 = new LinealHashDict(pathname_linh1, B, 1, false);
            Tester.test2(linh1, dna_array, rand_data_gen, "linh1" + i + "-");
        }
        for(int i = 0; i< n_test; i++) {
            Dictionary linh0 = new LinealHashDict(pathname_linh0, B, 0, true);
            Tester.test2(linh0, dna_array, rand_data_gen, "linh0" + i + "-");
        }

        //Tester.test2(exth, dna_array, rand_data_gen);
        //Tester.test2(linh0, dna_array, rand_data_gen);
        //Tester.test2(linh1, dna_array, rand_data_gen);

    }
}
