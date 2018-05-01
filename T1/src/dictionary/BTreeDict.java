package dictionary;

import utils.DNA;
import utils.FileManager;
import utils.Tuple2;

import java.io.File;
import java.util.ArrayList;

public class BTreeDict implements Dictionary {

    private class BTreeNode{

        private int offset;
        private final int B;
        private int block_size;
        private ArrayList<Integer> pointers;
        private ArrayList<DNA> keys;

        BTreeNode(int b, int offset) {
            super();
            this.B = b;
            this.offset = offset;
            ArrayList<Integer> values = fm.read(offset);
            this.block_size = values.get(0);
            for(int i = 0; i < this.block_size; i++){
                if(i % 2 == 0)
                    this.pointers.add(values.get(i));
                else
                    this.keys.add(new DNA(values.get(i)));
            }
        }

        public ArrayList<Integer> getPointers() {
            return this.pointers;
        }

        public ArrayList<DNA> getKeys() {
            return this.keys;
        }

    }

    private class BTreeLeaf {

        private int offset;
        private final int B;
        private ArrayList<DNA> values;

        BTreeLeaf(int b, int offset) {
            super();
            this.B = b;
            this.offset = offset;
            this.values = fm.read_block(offset);
        }

        public ArrayList<DNA> getValues() {
            return this.values;
        }

    }

    // tamano bloque = 4KB o 512B
    private int B;
    private final FileManager fm;
    private int height;
    private int offset_raiz;

    public BTreeDict(String filename, int B) {
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.height = 0;
        this.offset_raiz = 0;
    }

    public void put(DNA key, long value) {
        int actual_height = 0;
        int offset = offset_raiz;
        while (actual_height != this.height)
        {
            BTreeNode node = new BTreeNode(this.B, offset);
            ArrayList<DNA> dnas = node.getKeys();
            int i = 0;
            while(i < dnas.size()) {
                int cmp = key.compareTo(dnas.get(i));
                if(cmp <= 0)
                    break;
                i++;
            }
            offset = node.getPointers().get(i);
            actual_height++;
        }

        BTreeLeaf leaf = new BTreeLeaf(this.B, offset);
        ArrayList<DNA> dnas = leaf.getValues();
        int i = 0;
        while(i < dnas.size()) {
            int cmp = key.compareTo(dnas.get(i));
            if (cmp < 0) {
                dnas.add(i, key);
                break;
            }
            i++;
        }

        // Overflow
        if(dnas.size() > B){

        }
        else
            fm.write_block(dnas, offset);


    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        return false;
    }
}
