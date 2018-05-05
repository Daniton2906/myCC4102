package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.util.ArrayList;

public class ExtHashDict implements Dictionary {

    private class Node {

        private boolean L = false, R = false, has_page = false;
        private Node left = null, right = null;
        private int reference;

        public Node(int reference) {
            this.has_page = true;
            this.reference = reference;
        }

        public Node getLeftNode() {
            return this.left;
        }

        public void addLeftNode(Node left) {
            this.left = left;
        }

        public Node getRightNode() {
            return this.right;
        }

        public void addRightNode(Node right) {
            this.right = right;
        }

        public int getReference() {
            return this.reference;
        }

        public void deleteReference() {
            this.has_page = false;
        }

        public boolean hasReference() {
            return this.has_page;
        }

    }

    // tamano bloque = 4KB o 512B
    private int B, last;
    private final FileManager fm;
    private Node tree_reference;

    public ExtHashDict(String filename, int B) {
        // caso en que se usa un hashing lineal ya creado(?).


        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.tree_reference = new Node(1);
        this.last = 2;

    }

    private int getReference(DNA key) {
        int hash = key.hashCode();

        Node actual_node = this.tree_reference;
        int shift = 0;
        while(!actual_node.hasReference()) {
            if(shift > 31)
                break;

            if((hash & (1 << shift)) != 0) {
                actual_node = this.tree_reference.getLeftNode();
            } else {
                actual_node = this.tree_reference.getRightNode();
            }
            shift++;

        }

        return actual_node.getReference();

    }

    public void put(DNA key, long value) {

        int reference_page = getReference(key);
        ArrayList<Integer> content = this.fm.read(reference_page);



    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        int reference_page = getReference(key);
        ArrayList<Integer> content = this.fm.read(reference_page);

        return false;
    }

    public void resetIOCounter(){}

    public int getIOs(){return 0;}

    public int getUsedSpace(){return 0;}
}
