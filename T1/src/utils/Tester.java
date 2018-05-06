package utils;

import dictionary.Dictionary;

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

    static public void test1(Dictionary dict, ArrayList<DNA> chain_array, int n){
        List<DNA> L = chain_array.subList(0, n); //new ArrayList<>(); //
        /*
        L.add(new DNA(0x1b132df8));
        L.add(new DNA(0x6ef37fcc));
        L.add(new DNA(0x6245e754));
        L.add(new DNA(0xc674b72c));
        L.add(new DNA(0xedc5e960));
        L.add(new DNA(0x7d246850));
        L.add(new DNA(0xf1e570dc));
        L.add(new DNA(0x5a6fdb04));
        L.add(new DNA(0xa01df00));
        L.add(new DNA(0x488c4550));
        L.add(new DNA(0x25bb090));
        L.add(new DNA(0xa878c764));
        L.add(new DNA(0x949aa428));
        L.add(new DNA(0x4372724));
        L.add(new DNA(0xb57515b0));
        L.add(new DNA(0xd44863f8));
        L.add(new DNA(0xcaeba780));
        L.add(new DNA(0xd8f8ceec));
        L.add(new DNA(0xa8bebfcc));
        L.add(new DNA(0x2f7a1300));
        L.add(new DNA(0xccd87bac));
        L.add(new DNA(0xee3b1d00));
        L.add(new DNA(0x1e2d4e24));
        L.add(new DNA(0xce884520));
        L.add(new DNA(0x1d7f6588));
        */
        System.out.println("Insertando " + n + " claves");
        for (DNA dna: L)
            dict.put(dna, 0);
        System.out.println("Buscando mismas claves insertadas");
        int cnt = 0;
        for (DNA dna: L)
            if(dict.containsKey(dna))
                cnt++;
        System.out.println("Encontradas " + cnt + " claves");
        /*try {
        TimeUnit.SECONDS.sleep(5);
        } catch (Exception e){System.out.println("pium");}*/
        System.out.println("Borrando " + n + " claves");
        for (DNA dna: L)
            dict.delete(dna);
        System.out.println("Buscando claves borradas");
        cnt = 0;
        for (DNA dna: L)
            if(dict.containsKey(dna))
                cnt++;
        System.out.println("Encontradas " + cnt + " claves");
        System.out.println("##############################");

    }

    static public void test2(Dictionary dict, ArrayList<DNA> chain_array, DataGenerator dg){
        ArrayList<Integer> checkpoints = new ArrayList<>();
        for(int k = FROM_I_EXP; k <= TO_I_EXP; k++)
            checkpoints.add((int) Math.pow(2, k));

        int size = (int) Math.pow(2, TO_I_EXP);
        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dict.put(chain_array.get(i), 0);
            if(checkpoints.contains(i + 1)) {
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(dict.getUsedSpace());
                tmp.add(dict.getIOs());
                ArrayList<DNA> in_list = dg.getRandomChunk(chain_array, 1000),
                        out_list = dg.getOtherChunk(chain_array, 1000);
                dict.resetIOCounter();
                for(DNA in_dna: in_list)
                    dict.containsKey(in_dna);
                tmp.add(dict.getIOs());
                dict.resetIOCounter();
                for(DNA out_dna: in_list)
                    dict.containsKey(out_dna);
                tmp.add(dict.getIOs());
                results.add(tmp);
            }
        }
        checkpoints.remove(size);
        for (int i = size - 1; i >= 0; i--) {
            dict.delete(chain_array.get(i));
            if(checkpoints.contains(i + 1)) {

            }
        }
        System.out.println("### Report ###");


    }
}
