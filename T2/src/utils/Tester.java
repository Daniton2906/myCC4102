package utils;

import priority_queue.PriorityQueue;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Tester {

    static public void test0(PriorityQueue c, DataManager dm, boolean debug) {
        System.out.println(dm.toString());
        Vector<Integer> vec0 = dm.getSuffleData(),
                vec1= dm.getSuffleData(),
                vec2 = dm.getSuffleData();
        System.out.println(vec0.toString());
        for(Object num: vec0){
            c.insertar((int) num, (int) num);
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
        System.out.println(vec1.toString());
        for(Object num: vec1){
            c.insertar((int) num, (int) num);
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
        System.out.println(vec2.toString());
        for(Object num: vec2){
            c.insertar((int) num, (int) num);
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
    }
}
