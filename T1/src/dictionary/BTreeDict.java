package dictionary;

import utils.DNA;

import java.io.BufferedReader;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class BTreeDict implements Dictionary {

    private class BTreeNode implements Serializable {

        private DNA key;
        private ArrayList<BTreeNode> values = new ArrayList<>();
        private final int B;
        private final String filename;

        BTreeNode(DNA key, int b, String filename) {
            super();
            this.key = key;
            this.B = b;
            this.filename = filename;
        }

        BTreeNode(DNA key, int b, String filename, ArrayList<BTreeNode> values) {
            this(key, b, filename);
            this.values = values;
        }

        void insert(DNA key) {

        }

    }

    private String fn;
    private int B;
    private BTreeNode raiz;

    public BTreeDict(String filename, int B) {
        this.fn = filename;
        this.B = B;
    }

    public void put(DNA key, long value) {
        if(this.raiz == null)
            this.raiz = new BTreeNode(key, this.B, this.fn);
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
