package utils;

import dictionary.Dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tester {

    static final private int FROM_I_EXP = 15;
    static final private int TO_I_EXP = 20;

    static public void test0(FileManager fm, ArrayList<DNA> dna_array, int from, int to, int b_offset) {

        //ArrayList<DNA> L = new ArrayList<>(dna_array.subList(from, to));
        ArrayList<Integer> LI = new ArrayList<>();
        for(int i= from; i < to; i++){
            LI.add(dna_array.get(i).hashCode());
            System.out.println(dna_array.get(i).hashCode());
        }
        fm.write(LI, b_offset);
        ArrayList<Integer> L2 = fm.read(b_offset);
        for(int dna: L2)
            System.out.println(dna);
        System.out.println("############################");
    }

    static public void test1(Dictionary dict, ArrayList<DNA> chain_array, int n) {
        List<DNA> L = chain_array.subList(0, n); //new ArrayList<>(); //
        System.out.println("Insertando " + n + " claves");
        for (DNA dna: L)
            dict.put(dna, 0);
        System.out.println("Cantidad de I/Os promedio: " + dict.getIOs()/n);
        System.out.println("Buscando mismas claves insertadas");
        dict.resetIOCounter();
        int cnt = 0;
        for (DNA dna: L)
            if(dict.containsKey(dna))
                cnt++;
        System.out.println("Encontradas " + cnt + " claves");
        System.out.println("Cantidad de I/Os promedio: " + dict.getIOs()/n);
        System.out.println("Bytes usado por bloque: " + dict.getUsedSpace());
        try {
        TimeUnit.SECONDS.sleep(10);
        } catch (Exception e){System.out.println("pium");}
        dict.resetIOCounter();
        System.out.println("Borrando " + n + " claves");
        for (DNA dna: L)
            dict.delete(dna);
        System.out.println("Cantidad de I/Os promedio: " + dict.getIOs()/n);
        System.out.println("Buscando claves borradas");
        dict.resetIOCounter();
        cnt = 0;
        for (DNA dna: L)
            if(dict.containsKey(dna))
                cnt++;
        System.out.println("Encontradas " + cnt + " claves");
        System.out.println("Cantidad de I/Os promedio: " + dict.getIOs()/n);
        System.out.println("Bytes usado por bloque: " + dict.getUsedSpace());
        System.out.println("##############################");
    }

    static public void test2(Dictionary dict, ArrayList<DNA> chain_array, DataGenerator dg, String name)  throws IOException {
        ArrayList<Integer> checkpoints = new ArrayList<>();
        for(int i = FROM_I_EXP; i <= TO_I_EXP; i++)
            checkpoints.add((int) Math.pow(2, i));

        int size = (int) Math.pow(2, TO_I_EXP);
        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        System.out.println("Comienza test, registro datos desde " + checkpoints.get(0) + " claves.");
        for (int k = 0; k < size; k++) {
            dict.put(chain_array.get(k), 0);
            if(checkpoints.contains(k + 1)) {
                System.out.println("Checkpoint insertar " + (k + 1) + " claves");
                ArrayList<Integer> tmp = new ArrayList<>();
                int us = dict.getUsedSpace(),
                    ios = dict.getIOs();
                System.out.println("Espacio por bloque put en checkpoint " + (k + 1) + ": " + us);
                System.out.println("IOs put en checkpoint " + (k + 1) + ": " + ios);
                tmp.add(us);
                tmp.add(ios);
                ArrayList<DNA> in_list = dg.getRandomChunk(chain_array, 1000),
                        out_list = dg.getOtherChunk(chain_array, 1000);
                dict.resetIOCounter();
                System.out.println("Buscando " + 1000 + " claves insertadas en el dict");
                for(DNA in_dna: in_list)
                    dict.containsKey(in_dna);
                int ios_s = dict.getIOs();
                System.out.println("IOs busquedas exitosas en checkpoint " + (k + 1) + ": " + ios_s);
                tmp.add(ios_s);
                dict.resetIOCounter();
                System.out.println("Buscando " + 1000 + " claves fuera del dict");
                for(DNA out_dna: in_list)
                    dict.containsKey(out_dna);
                int ios_s2 = dict.getIOs();
                System.out.println("IOs busquedas infructuosas en checkpoint " + (k + 1) + ": " + ios_s2);
                tmp.add(ios_s2);
                results.add(tmp);
            }
        }
        checkpoints.remove(checkpoints.size() - 1);
        int w = results.size() - 2;
        int totalIOs = 0;
        for (int k = size - 1; k >= 0; k--) {
            dict.delete(chain_array.get(k));
            if(checkpoints.contains(k + 1)) {
                int us_d = dict.getUsedSpace(),
                        ios_d = dict.getIOs();
                System.out.println("Checkpoint borrar hasta " + (k + 1) + " claves");
                System.out.println("Espacio por bloque put en checkpoint " + (k + 1) + ": " + us_d);
                System.out.println("IOs put en checkpoint " + (k + 1) + ": " + ios_d);
                results.get(w).add(us_d);
                results.get(w).add(ios_d);
                totalIOs += dict.getIOs();
                dict.resetIOCounter();
                w--;
            }
        }
        totalIOs += dict.getIOs();
        System.out.println("IOs totales para vaciar dict: " + totalIOs);
        System.out.println("IOs totales para vaciar dict desde" + checkpoints.get(0) + ": " + dict.getIOs());
        results.get(results.size() - 1).add(dict.getIOs());
        results.get(results.size() - 1).add(totalIOs);
        dict.resetIOCounter();
        checkpoints.add(size);

        String btree_filename = "/T1/results/" + name + System.currentTimeMillis() + ".tsv";
        String fileName = System.getProperty("user.dir") + btree_filename;

        //try {

            File fd = new File(fileName);
            fd.getParentFile().mkdir();
            fd.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

            System.out.println("############################## REPORT ###########################################################");
            String header = "i \t 2^i \t space_put \t ios_put \t ios_search_in \t ios_search_out \t space_delete \t ios_delete" + System.lineSeparator();
            System.out.print(header);
            writer.write(header);
            for(int i = 0; i < results.size(); i++){
                ArrayList<Integer> tmp = results.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(FROM_I_EXP + i); sb.append("\t"); sb.append(checkpoints.get(i));
                for(int num: tmp) {
                    sb.append("\t"); sb.append(num);
                }
                sb.append(System.lineSeparator());

                System.out.print(sb.toString());
                writer.write(sb.toString());
            }
            writer.close();
        /*} catch( IOException e) {
            System.out.println("error");
        }*/
    }
}
