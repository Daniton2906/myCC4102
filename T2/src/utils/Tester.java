package utils;

import priority_queue.PriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Tester {

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

    //static
}
