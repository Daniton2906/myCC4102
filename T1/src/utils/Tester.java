package utils;

import dictionary.Dictionary;

import java.util.ArrayList;

public class Tester {

    static final private int I_EXP = 20;

    static public void test0(FileManager fm, ArrayList<DNA> dna_array, int from, int to, int b_offset) {

        //ArrayList<DNA> L = new ArrayList<>(dna_array.subList(from, to));
        ArrayList<Integer> LI = new ArrayList<>();
        for(int i= from; i < to; i++){
            LI.add(dna_array.get(i).hashCode());
            //System.out.println(dna_array.get(i).hashCode());
        }
        fm.write(LI, b_offset);

        ArrayList<Integer> L2 = fm.read(b_offset);
        for(int dna: L2);
            //System.out.println(dna);
    }

    static public void test1(Dictionary dict, ArrayList<DNA> chain_array){
        for(DNA dna: chain_array){
            System.out.println(dna.hashCode());
            dict.put(dna, 0);
        }

    }

    static public void test2(Dictionary dict, ArrayList<DNA> chain_array){
        for (int i = 1; i <= I_EXP; i++) {
            int sindex = (int) Math.pow(2, i - 1),
                    eindex = (int) Math.pow(2, i);
            for (int j = sindex; j < eindex; j++) {

            }

        }
    }
}
