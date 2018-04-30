package dictionary;

import utils.DNA;
import utils.FileManager;
import utils.Tuple2;

import java.io.File;
import java.util.ArrayList;

public class BTreeDict implements Dictionary {

    private class BTreeNode{

        private DNA key;
        private int offset;
        private final int B;
        private ArrayList<Tuple2<Integer, DNA>> values;

        BTreeNode(DNA key, int b, int offset) {
            super();
            this.key = key;
            this.B = b;
            this.offset = offset;
            this.values = fm.read_node(offset);
        }

        public ArrayList<Tuple2<Integer, DNA>> getValues() {
            return this.values;
        }

    }

    private class BTreeLeaf {

        private DNA key;
        private int offset;
        private final int B;
        private ArrayList<DNA> values;

        BTreeLeaf(DNA key, int b, int offset) {
            super();
            this.key = key;
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
        while (this.height != actual_height)
        {
            BTreeNode raiz = new BTreeNode(key, this.B, offset);
            ArrayList<Tuple2<Integer, DNA>> dna_array = raiz.getValues();
            int i = 0;
            while(i < dna_array.size()){
                int cmp = key.compareTo(dna_array.get(i).y);
                if(cmp == 0)
                    break;
                i++;
            }
            actual_height++;
        }

/*
        if(this.height == 0) {

            if(i == dna_array.size())

            //fm.write_node();
        }
        else
            this.raiz.insert(key);*/

    }

    public long get(DNA key) {
        return 0;
    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        return false;
    }
}
