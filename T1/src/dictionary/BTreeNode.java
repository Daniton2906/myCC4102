package dictionary;

import utils.DNA;

import java.util.ArrayList;

public class BTreeNode{

    private int offset;
    private final int B;
    private int block_size;
    private ArrayList<Integer> pointers = new ArrayList<>();
    private ArrayList<DNA> keys = new ArrayList<>();

    protected BTreeNode(int b, int offset, ArrayList<Integer> values) {
        super();
        this.B = b;
        this.offset = offset;
        this.block_size = values.get(0) & 0x7fffffff;
        for(int i = 1; i <= this.block_size; i++){
            if(i % 2 != 0) //indices impares son referencias
                this.pointers.add(values.get(i));
            else //indices pares son claves
                this.keys.add(new DNA(values.get(i)));
        }
    }

    protected int getOffset() {
        return this.offset;
    }

    protected ArrayList<Integer> getPointers() {
        return this.pointers;
    }

    protected ArrayList<DNA> getKeys() {
        return this.keys;
    }

    public Integer getBlocksize() {
        return this.block_size;
    }

    protected boolean insert(DNA key, int offset1, int offset2) {
        // agregar mediana
        int j = 0;
        for (; j < this.keys.size(); j++) {
            if (key.compareTo(this.keys.get(j)) <= 0)
                break;
        }
        if(j < this.keys.size()) {
            if(key.compareTo(this.keys.get(j)) == 0)
                System.out.println("WUAAAAAAa");
            this.keys.add(j, key);
            this.pointers.set(j, offset1);
            this.pointers.add(j + 1, offset2);
        } else {
            this.keys.add(key);
            this.pointers.set(j, offset1);
            this.pointers.add(offset2);
        }
        this.block_size += 2;
        return true;
    }

    protected boolean delete(DNA key) {
        return delete(key, true);
    }

    protected boolean delete(DNA key, boolean delete_prev) {
        for (int j = 0; j < this.keys.size(); j++) {
            if (key.compareTo(this.keys.get(j)) == 0) {
                this.keys.remove(j);
                if(delete_prev)
                    this.pointers.remove(j);
                else
                    this.pointers.remove(j + 1);
                this.block_size-=2;
                return true;
            }
        } return false;
    }

    protected BTreeNode join(BTreeNode other_node) {
        return null;
    }

    protected DNA replace_key(int index, DNA value) {
        if(index < this.keys.size())
            return this.keys.set(index, value);
        return null;
    }

    protected boolean replace_pointer(int index, DNA value) {
        return false;
    }

    protected ArrayList<Integer> getIntValues() {
        ArrayList<Integer> int_array = new ArrayList<>();
        for(int i = 0; i < this.keys.size(); i++) {
            int_array.add(this.pointers.get(i));
            int_array.add(this.keys.get(i).hashCode());
        }
        int_array.add(this.pointers.get(this.keys.size()));
        return int_array;
    }

}
