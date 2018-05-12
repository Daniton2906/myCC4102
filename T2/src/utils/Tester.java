package utils;

import priority_queue.PriorityQueue;

import java.util.Vector;

public class Tester {

    static public void test0(PriorityQueue c, int n) {
        DataManager dm = new DataManager(n, 0);
        System.out.println(dm.toString());
        Vector<Integer> vec0 = dm.getSuffleData(),
                vec1= dm.getSuffleData(),
                vec2 = dm.getSuffleData();
        System.out.println(vec0.toString());
        for(int num: vec0){
            c.insertar(num, num);
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
        System.out.println(vec1.toString());
        System.out.println(vec2.toString());

    }
}
