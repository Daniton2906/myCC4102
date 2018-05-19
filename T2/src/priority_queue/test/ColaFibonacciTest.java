package priority_queue.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import priority_queue.ColaFibonacci;
import priority_queue.PriorityQueue;
import utils.DataManager;

import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColaFibonacciTest {

    private ColaFibonacci cp1, cp2, cp3, cp4, cp5;
    private Vector<Object> vec1, vec2, vec3, vec4, vec5;

    @Before
    public void setUp() throws Exception {
        cp1 = new ColaFibonacci();
        cp2 = new ColaFibonacci();
        cp3 = new ColaFibonacci();
        cp4 = new ColaFibonacci();
        cp5 = new ColaFibonacci();
        Object[] a1d = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
                a2d = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                a3d = {0, 7, 1, 2, 5, 3, 6, 9, 8, 4},
                a4d = {7, 1, 4, 0, 9},
                a5d = {3, 6, 8, 2, 5};
        vec1 = new Vector<>(Arrays.asList(a1d));
        vec2 = new Vector<>(Arrays.asList(a2d));
        vec3 = new Vector<>(Arrays.asList(a3d));
        vec4 = new Vector<>(Arrays.asList(a4d));
        vec5 = new Vector<>(Arrays.asList(a5d));
    }

    @Test
    public void insertar() {
        for (int i = 0; i < 10; i++) {
            cp1.insertar((int) vec1.get(i), (int) vec1.get(i));
            cp2.insertar((int) vec2.get(i), (int) vec2.get(i));
            cp3.insertar((int) vec3.get(i), (int) vec3.get(i));
        }
        assertFalse(cp1.isEmpty());
        assertFalse(cp2.isEmpty());
        assertFalse(cp3.isEmpty());
    }

    @Test
    public void extraer_siguiente() {
        insertar();
        for (int i = 0; i < 10; i++) {
            int n1 = cp1.extraer_siguiente().getValue(),
                    n2 = cp2.extraer_siguiente().getValue(),
                    n3 = cp3.extraer_siguiente().getValue();
            assertEquals(n1, vec1.get(i));
            assertEquals(n2, vec1.get(i));
            assertEquals(n3, vec1.get(i));
        }
        assertTrue(cp1.isEmpty());
        assertTrue(cp2.isEmpty());
        assertTrue(cp3.isEmpty());
    }

    @org.junit.Test
    public void meld() {
        for (int i = 0; i < 5; i++) {
            cp4.insertar((int) vec4.get(i), (int) vec4.get(i));
            cp5.insertar((int) vec5.get(i), (int) vec5.get(i));
        }
        PriorityQueue cp6 = cp4.meld(cp4, cp5);
        for (int i = 0; i < 10; i++) {
            assertEquals(cp6.extraer_siguiente().getValue(), vec1.get(i));
        }
    }

    @org.junit.Test
    public void z_massive_test() {
        int n_test = 1000;
        DataManager dm = new DataManager(10000, 0);
        PriorityQueue cp = new ColaFibonacci();
        for (int i = 0; i < n_test; i++) {
            Vector<Integer> vec = dm.getSuffleData();
            for(int num: vec){
                cp.insertar(num, num);
            }
            for (int j = 0; !cp.isEmpty(); j++) {
                assertEquals(cp.extraer_siguiente().getValue(), dm.get(j));
            }
        }
    }

}