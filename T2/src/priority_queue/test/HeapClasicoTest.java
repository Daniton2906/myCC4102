package priority_queue.test;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import priority_queue.HeapClasico;
import priority_queue.PriorityQueue;
import utils.DataManager;
import utils.Node;

import java.util.Arrays;
import java.util.Vector;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeapClasicoTest {

    private HeapClasico hp1, hp2, hp3, hp4, hp5;
    private Vector<Object> vec1, vec2, vec3, vec4, vec5;

    @org.junit.Before
    public void setUp() throws Exception {
        hp1 = new HeapClasico();
        hp2 = new HeapClasico();
        hp3 = new HeapClasico();
        hp4 = new HeapClasico();
        hp5 = new HeapClasico();
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

    @org.junit.Test
    public void insertar() {
        for (int i = 0; i < 10; i++) {
            hp1.insertar((int) vec1.get(i), (int) vec1.get(i));
            hp2.insertar((int) vec2.get(i), (int) vec2.get(i));
            hp3.insertar((int) vec3.get(i), (int) vec3.get(i));
        }
        assertFalse(hp1.isEmpty());
        assertFalse(hp2.isEmpty());
        assertFalse(hp3.isEmpty());
    }

    @org.junit.Test
    public void extraer_siguiente() {
        insertar();
        for (int i = 0; i < 10; i++) {
            int n1 = hp1.extraer_siguiente().getValue(),
                    n2 = hp2.extraer_siguiente().getValue(),
                    n3 = hp3.extraer_siguiente().getValue();
            assertEquals(n1, vec1.get(i));
            assertEquals(n2, vec1.get(i));
            assertEquals(n3, vec1.get(i));
        }
    }

    @org.junit.Test
    public void meld() {
        for (int i = 0; i < 5; i++) {
            hp4.insertar((int) vec4.get(i), (int) vec4.get(i));
            hp5.insertar((int) vec5.get(i), (int) vec5.get(i));
        }
        HeapClasico hp6 = new HeapClasico(hp4, hp5);
        for (int i = 0; i < 10; i++) {
            assertEquals(hp6.extraer_siguiente().getValue(), vec1.get(i));
        }
    }

    @org.junit.Test
    public void z_massive_test() {
        int n_test = 1000;
        DataManager dm = new DataManager(10000, 0);
        PriorityQueue hp = new HeapClasico();
        for (int i = 0; i < n_test; i++) {
            Vector<Integer> vec = dm.getSuffleData();
            for(int num: vec){
                hp.insertar(num, num);
            }
            for (int j = 0; !hp.isEmpty(); j++) {
                assertEquals(hp.extraer_siguiente().getValue(), dm.get(j));
            }
        }
    }
}