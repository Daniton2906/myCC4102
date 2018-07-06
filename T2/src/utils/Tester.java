package utils;

import apps.InsertAndMelding;
import apps.Sort;
import priority_queue.PriorityQueue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class Tester {

    private static final int MIN_EXP_SORT = 15; //15;
    private static final int MAX_EXP_SORT = 21; //21;
    private static final int MIN_EXP_INS_MEL = 0; //0
    private static final int MAX_EXP_INS_MEL = 15; //15
    private static final int MAX_EXP = 20; //20

    static private File createFile(String abs_path, String filename) {
        File dir = new File(abs_path);
        if(!dir.exists() && !dir.mkdir()) {
            System.out.println("Error al crear " + abs_path);
            System.exit(0);
        }

        File fd = new File(dir, filename);
        // the rest of your code
        try {
            if (fd.createNewFile()) {
                System.out.println("created new fle");
            } else {
                System.out.println("could not create a new file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fd;
    }

    static public void test0(PriorityQueue c, DataManager dm, boolean debug) {
        System.out.println("Original array: " + dm.toString());
        Vector<Integer> vec;
        for(int i = 0; i < 3; i++) {
            vec = dm.getSuffleData();
            System.out.println("Shuffled array: " + vec.toString());
            for(int num: vec){
                c.insertar(num, num);
            }
            System.out.print("Final array: [ ");
            while(!c.isEmpty()){
                System.out.print(c.extraer_siguiente().getValue() + " ");
            }
            System.out.println("]");
        }
    }

    static public void sort_test(PriorityQueue cp, boolean debug, String name) throws IOException {
        String cp_filename = name + "-sort-" + System.currentTimeMillis() + ".tsv",
            cp_absolute_path = new File("").getAbsolutePath() + "/T2/results/sort/";

        File fd = createFile(cp_absolute_path, cp_filename);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        String header = "i\t2^i\theapify_time\textract_time" + System.lineSeparator();

        StringBuilder sb = new StringBuilder(header);
        for(int j = MIN_EXP_SORT; j <= MAX_EXP_SORT; j++) {
            sb.append(j); sb.append("\t");
            int n = (int) Math.pow(2, j);
            sb.append(n); sb.append("\t");
            System.out.println("Creando arreglo tamaño " + n + "...");
            DataManager dm = new DataManager(n, 0);

            System.out.println("Revolviendo arreglo...");
            List<Integer> list = dm.getSuffleData(), sorted_list;
            long start, end;
            Sort sort = new Sort(list, cp);

            System.out.println("Heapifiando arreglo...");
            start = System.currentTimeMillis();
            sort.heapify();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append("\t");

            System.out.println("Extrayendo elementos en orden...");
            start = System.currentTimeMillis();
            sorted_list = sort.sort();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append(System.lineSeparator());
        }

        writer.write(sb.toString());
        System.out.println("Escribiendo resultados en " +  cp_absolute_path + cp_filename + "...");
        System.out.println();
        System.out.println("############################## REPORT (" + name + "-sort) ###########################################################");
        System.out.println(sb.toString());
        writer.close();
    }

    static public void insert_and_melding_test(PriorityQueue cp, boolean debug, String name) throws IOException {
        String cp_absolute_path = new File("").getAbsolutePath() + "/T2/results/ins_meld/",
                cp_filename = name + "-ins_meld-" + System.currentTimeMillis() + ".tsv";

        File fd = createFile(cp_absolute_path, cp_filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        String header = "i\tn=2^" + MAX_EXP + "\tk=2^i\tinsert_time\tmelding_time" + System.lineSeparator();
        StringBuilder sb = new StringBuilder(header);

        int n = (int) Math.pow(2, MAX_EXP);
        System.out.println("Creando arreglo de tamaño " + n + "...");
        DataManager dm = new DataManager(n, 0);

        System.out.println("Revolviendo arreglo...");
        List<Integer> list = dm.getSuffleData(), sorted_list;

        for(int i = MAX_EXP_INS_MEL; i >= MIN_EXP_INS_MEL; i--) {
            sb.append(i); sb.append("\t");
            sb.append(n); sb.append("\t");
            int k = (int) Math.pow(2, i);
            sb.append(k); sb.append("\t");

            long start, end;
            InsertAndMelding insMel = new InsertAndMelding(i, list);

            System.out.println("Creando " + k + " subarreglos de tamaño " + n/k + " en subcolas...");
            start = System.currentTimeMillis();
            insMel.insercion(cp);
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append("\t");

            System.out.println("Aplicando melding en colas...");
            start = System.currentTimeMillis();
            PriorityQueue cola_final = insMel.melding();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append(System.lineSeparator());
        }

        writer.write(sb.toString());
        System.out.println("Escribiendo resultados en " +  cp_absolute_path + "...");
        System.out.println("");
        System.out.println("############################## REPORT (" + name + "-ins_meld) ###########################################################");
        System.out.println(sb.toString());
        writer.close();
    }
}
