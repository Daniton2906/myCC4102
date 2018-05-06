package dictionary;

import utils.DNA;
import utils.Tuple2;

import java.util.ArrayList;

public class BTreeLeaf {

    private int offset;
    private final int B;
    private int block_size;
    private ArrayList<DNA> values = new ArrayList<>();

    protected BTreeLeaf(int b, int offset, ArrayList<Integer> values) {
        super();
        this.B = b;
        this.offset = offset;
        this.block_size = values.get(0) & 0x7fffffff;
        for(int i = 1; i <= this.block_size; i++)
            this.values.add(new DNA(values.get(i)));
    }

    protected int getOffset() {
        return this.offset;
    }

    protected ArrayList<DNA> getValues() {
        return this.values;
    }

    protected Integer getBlocksize() {
        return this.block_size;
    }

    protected boolean insert(DNA key) {
        // buscar posicion
        int i = 0;
        while(i < this.values.size()) {
            int cmp = key.compareTo(this.values.get(i));
            if (cmp < 0) { //se puede insertar
                break;
            } else if(cmp == 0) //si clave ya está, no se hará insercion
                return false;
            i++;
        }
        this.values.add(i, key);
        this.block_size++;
        return true;
    }

    protected boolean delete(DNA key) {
        int j = 0;
        for (; j < this.values.size(); j++) {
            if (key.compareTo(this.values.get(j)) == 0) {
                this.values.remove(j);
                this.block_size--;
                return true;
            }
        } return false;
    }

    protected ArrayList<Integer> getIntValues() {
        ArrayList<Integer> int_array = new ArrayList<>();
        for(int i = 0; i < this.block_size; i++)
            int_array.add(this.values.get(i).hashCode());
        return int_array;
    }

    protected BTreeLeaf join(BTreeLeaf other_leaf) {
        ArrayList<Integer> new_leaf = new ArrayList<>();
        for(DNA myDna: this.values)
            new_leaf.add(myDna.hashCode());
        for(DNA otherDna: other_leaf.getValues())
            new_leaf.add(otherDna.hashCode());
        new_leaf.add(0, new_leaf.size());
        return new BTreeLeaf(this.B, this.offset, new_leaf);
    }

}
