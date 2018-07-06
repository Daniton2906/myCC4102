package priority_queue;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.DataManager;
import utils.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LinkedListTesting {

    private Vector<Node> vec = DataManager.toNodeArray(new DataManager(1000000, 0).getSuffleData());

    private myLinkedList L0 = new myLinkedList(vec.subList(0, 100)),
            L1 = new myLinkedList(vec.subList(0, 100)),
            L2 = new myLinkedList(vec.subList(0, 1000)),
            L3 = new myLinkedList(vec.subList(0, 1000)),
            L4 = new myLinkedList(vec.subList(0, 10000)),
            L5 = new myLinkedList(vec.subList(0, 10000)),
            L6 = new myLinkedList(vec.subList(0, 100000)),
            L7 = new myLinkedList(vec.subList(0, 100000)),
            L8 = new myLinkedList(vec),
            L9 = new myLinkedList(vec);
    /*
        L10 = new myLinkedList(vec),
        L11 = new myLinkedList(vec);
*/
    @Test
    public void addOneTest() {
        List<Node> L = vec.subList(0, 10);
        myLinkedList myL = new myLinkedList();
        myL.addFirst(new BinomialTree(L.get(0)));
        assertEquals(L.get(0).getValue(), myL.pollFirst().getNode().getValue());

        myL.addLast(new BinomialTree(L.get(0)));
        assertEquals(L.get(0).getValue(), myL.pollLast().getNode().getValue());

        myL.addFirst(new BinomialTree(L.get(0)));
        assertEquals(L.get(0).getValue(), myL.pollLast().getNode().getValue());

        myL.addLast(new BinomialTree(L.get(0)));
        assertEquals(L.get(0).getValue(), myL.pollFirst().getNode().getValue());
    }

    @Test
    public void addTwoTest() {
        List<Node> L = vec.subList(0, 10);
        //System.out.println(L);
        myLinkedList myL = new myLinkedList();
        myL.addFirst(new BinomialTree(L.get(0)));
        myL.addFirst(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(1).getValue(), myL.pollFirst().getNode().getValue());
        assertEquals(L.get(0).getValue(), myL.pollFirst().getNode().getValue());

        myL.addLast(new BinomialTree(L.get(0)));
        myL.addLast(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(1).getValue(), myL.pollLast().getNode().getValue());
        assertEquals(L.get(0).getValue(), myL.pollLast().getNode().getValue());

        myL.addFirst(new BinomialTree(L.get(0)));
        myL.addLast(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(0).getValue(), myL.pollFirst().getNode().getValue());
        assertEquals(L.get(1).getValue(), myL.pollLast().getNode().getValue());

        myL.addLast(new BinomialTree(L.get(0)));
        myL.addFirst(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(1).getValue(), myL.pollFirst().getNode().getValue());
        assertEquals(L.get(0).getValue(), myL.pollLast().getNode().getValue());

        myL.addFirst(new BinomialTree(L.get(0)));
        myL.addLast(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(1).getValue(), myL.pollLast().getNode().getValue());
        assertEquals(L.get(0).getValue(), myL.pollFirst().getNode().getValue());

        myL.addLast(new BinomialTree(L.get(0)));
        myL.addFirst(new BinomialTree(L.get(1)));
        //System.out.println(myL.toString());
        assertEquals(L.get(0).getValue(), myL.pollLast().getNode().getValue());
        assertEquals(L.get(1).getValue(), myL.pollFirst().getNode().getValue());
    }

    @Test
    public void addNTest() {
        List<Node> L = vec.subList(0, 10);
        myLinkedList myL = new myLinkedList();
        //System.out.println(L);

        for (Node node: L) {
            myL.addFirst(new BinomialTree(node));
        }
        //System.out.println(myL.toString());
        int i = 0, n = L.size();
        while (!myL.isEmpty()) {
            assertEquals(L.get(n - i - 1).getValue(), myL.pollFirst().getNode().getValue());
            i++;
        }

        for (Node node: L) {
            myL.addLast(new BinomialTree(node));
        }
        //System.out.println(myL.toString());
        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(L.get(n - i - 1).getValue(), myL.pollLast().getNode().getValue());
            i++;
        }

        for (Node node: L) {
            myL.addFirst(new BinomialTree(node));
        }
        //System.out.println(myL.toString());
        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(L.get(i).getValue(), myL.pollLast().getNode().getValue());
            i++;
        }

        for (Node node: L) {
            myL.addLast(new BinomialTree(node));
        }
        //System.out.println(myL.toString());
        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(L.get(i).getValue(), myL.pollFirst().getNode().getValue());
            i++;
        }
    }

    @Test
    public void randomTest() {
        List<Node> L = vec.subList(0, 10), Ls = new Vector<>();
        myLinkedList myL = new myLinkedList();
        for (Node node: L) {
            if(node.getPriority() % 2 == 0)
                myL.addLast(new BinomialTree(node));
            else
                myL.addFirst(new BinomialTree(node));
        }
        int i = 0;
        while (!myL.isEmpty()) {
            if(i % 2 == 0)
                Ls.add(myL.pollFirst().getNode());
            else
                Ls.add(myL.pollLast().getNode());
            i++;
        }
        assertEquals(L.size(), Ls.size());
    }

    @Test
    public void maxTest() {
        List<Node> L = vec.subList(0, 10), Ls = new Vector<>();
        System.out.println(L);
        myLinkedList myL = new myLinkedList();
        Node max = L.get(0);
        int i = 0, idx = 0;
        for (Node node: L) {
            if(node.compareTo(max) > 0) {
                max = node;
                idx = i;
            }
            i++;
            Ls.add(node);
        }
        System.out.println(Ls.remove(idx));
        System.out.println(Ls);


        for (Node node: L) {
            myL.addFirst(new BinomialTree(node));
        }
        assertTrue(max.compareTo(myL.pollMax().getNode()) == 0);
        assertTrue(myL.pollMax() == null);

        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(Ls.get(i).getValue(), myL.pollLast().getNode().getValue());
            i++;
        }

        for (Node node: L) {
            myL.addLast(new BinomialTree(node));
        }
        assertTrue(max.compareTo(myL.pollMax().getNode()) == 0);
        assertTrue(myL.pollMax() == null);

        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(Ls.get(i).getValue(), myL.pollFirst().getNode().getValue());
            i++;
        }

        for (Node node: L) {
            myL.addFirst(new BinomialTree(node));
        }
        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(L.get(i).getValue(), myL.pollLast().getNode().getValue());
            i++;
        }
        assertTrue(myL.pollMax() == null);

        for (Node node: L) {
            myL.addLast(new BinomialTree(node));
        }

        i = 0;
        while (!myL.isEmpty()) {
            assertEquals(L.get(i).getValue(), myL.pollFirst().getNode().getValue());
            i++;
        }
        assertTrue(myL.pollMax() == null);
    }

    @Test
    public void unir_10_2() {
        L0.addAll(L1);
        assertEquals(200, L0.size());
    }

    @Test
    public void unir_10_3() {
        L2.addAll(L3);
        assertEquals(2000, L2.size());
    }

    @Test
    public void unir_10_4() {
        L4.addAll(L5);
        assertEquals(20000, L4.size());
    }

    @Test
    public void unir_10_5() {
        L6.addAll(L7);
        assertEquals(200000, L6.size());
    }

    @Test
    public void unir_10_6() {
        L8.addAll(L9);
        assertEquals(2000000, L8.size());
    }
/*
    @Ignore
    @Test
    public void unir_10_7() {
        L10.addAll(L11);
        assertEquals(20000000, L10.size());
    }*/
}