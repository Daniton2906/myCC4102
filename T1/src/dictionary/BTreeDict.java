package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class BTreeDict implements Dictionary {

    private class BTreeNode implements Serializable {

        private DNA key;
        private ArrayList<BTreeNode> values = new ArrayList<>();
        private int offset;
        private final int B;

        BTreeNode(DNA key, int b, int offset) {
            super();
            this.key = key;
            this.B = b;
            this.offset = offset;
        }

        BTreeNode(DNA key, int b, int offset, ArrayList<BTreeNode> values) {
            this(key, b, offset);
            this.values = values;
        }

        void insert(DNA key) {
            ArrayList<DNA> dnas = fm.read(offset);
            int i = 0;

            while(i < dnas.size()){
                int cmp = key.compareTo(dnas.get(i));
                if(cmp == 0)
                    break;
                i++;
            }

        }

    }

    private int B;
    private BTreeNode raiz;
    private final FileManager fm;

    public BTreeDict(String filename, int B) {
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
    }

    public void put(DNA key, long value) {
        if(this.raiz == null)
            this.raiz = new BTreeNode(key, this.B, 0);
        else
            this.raiz.insert(key);

    }

    public long get(DNA key) {
        return 0;
    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        return false;
    }
}
