package utils;

import dictionary.Dictionary;

import java.util.ArrayList;

public class Tester {

    static final private int I_EXP = 20;

    static public void test(Dictionary dict, ArrayList<Character> chain_array){
        for (int i = 1; i <= I_EXP; i++) {
            int sindex = (int) Math.pow(2, i - 1),
                    eindex = (int) Math.pow(2, i);
            for (int j = sindex; j < eindex; j++) {

            }

        }
    }
}
