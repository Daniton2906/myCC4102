package utils;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class DataManager {

    private Vector<Integer> data = new Vector<>();
    private final Random rand;

    public DataManager(int n, long seed) {
        this.rand = new Random(seed);
        for(int i = n - 1; i >= 0; i--) {
            data.add(i);
        }
    }

    public Vector<Integer> getSuffleData() {
        int n = data.size();
        Object[] int_array = new Object[n];
        data.copyInto(int_array);
        Vector<Integer> new_vector = new Vector<>();
        while(n > 1) {
            int i = rand.nextInt(n),
                    next_element = (int) int_array[i];
            new_vector.add(next_element);
            int_array[i] = int_array[n - 1];
            int_array[n - 1] = next_element;
            n--;
        }
        new_vector.add((int) int_array[0]);
        return new_vector;
    }

    public Vector<Node> toNodeArray(List<Integer> ints) {
        Vector<Node> new_vector = new Vector<>();
        for (int i: ints) {
            new_vector.add(new Node(i));
        }
        return new_vector;
    }


    public int get(int index) {
        return data.get(index);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
