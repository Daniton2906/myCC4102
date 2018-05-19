package utils;

import priority_queue.PriorityQueue;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Tester {

    static public void test0(PriorityQueue c, DataManager dm, boolean debug) {
        System.out.println(dm.toString());
        Vector<Node> vec0 = dm.getSuffleData(),
                vec1= dm.getSuffleData(),
                vec2 = dm.getSuffleData();
        System.out.println(vec0.toString());
        for(Node node: vec0){
            c.insertar(node.getValue(), node.getPriority());
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
        System.out.println(vec1.toString());
        for(Node node: vec1){
            c.insertar(node.getValue(), node.getPriority());
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
        System.out.println(vec2.toString());
        for(Node node: vec2){
            c.insertar(node.getValue(), node.getPriority());
        }
        while(!c.isEmpty()){
            System.out.println(c.extraer_siguiente().getValue());
        }
    }

    //static
}
