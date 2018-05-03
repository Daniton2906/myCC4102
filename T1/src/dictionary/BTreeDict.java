package dictionary;

import utils.DNA;
import utils.FileManager;
import utils.Tuple2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BTreeDict implements Dictionary {

    private class BTreeNode{

        private int offset;
        private final int B;
        private int block_size;
        private ArrayList<Integer> pointers = new ArrayList<>();
        private ArrayList<DNA> keys = new ArrayList<>();

        BTreeNode(int b, int offset, ArrayList<Integer> values) {
            super();
            this.B = b;
            this.offset = offset;
            this.block_size = values.get(0) & 0xbfffffff;
            for(int i = 0; i < this.block_size; i++){
                if(i % 2 == 0)
                    this.pointers.add(values.get(i));
                else
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

    }

    private class BTreeLeaf {

        private int offset;
        private final int B;
        private int block_size;
        private ArrayList<DNA> values = new ArrayList<>();

        BTreeLeaf(int b, int offset, ArrayList<Integer> values) {
            super();
            this.B = b;
            this.offset = offset;
            this.block_size = values.get(0) & 0xbfffffff;
            for(int i = 0; i < this.block_size; i++)
                this.values.add(new DNA(values.get(i)));

        }

        protected ArrayList<DNA> getValues() {
            return this.values;
        }

        protected ArrayList<Integer> getIntValues() {
            ArrayList<Integer> int_array = new ArrayList<>();
            for(int i = 0; i < this.block_size; i++)
                int_array.add(this.values.get(i).hashCode());
            return int_array;
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
        int offset = offset_raiz;
        ArrayList<Integer> values = fm.read(offset);
        int isLeaf = (values.get(0) >> 31) & 0x1;
        ArrayList<BTreeNode> visited_nodes = new ArrayList<>();
        int h = 0;
        // Buscar hoja bajando por nodos correspondiente
        while (isLeaf != 0) //hasta encontrar una hoja
        {
            BTreeNode node = new BTreeNode(this.B, offset, values);
            ArrayList<DNA> dnas = node.getKeys();
            int i = 0;
            while(i < dnas.size()) {
                int cmp = key.compareTo(dnas.get(i));
                if(cmp <= 0)
                    break;
                i++;
            }
            offset = node.getPointers().get(i);
            visited_nodes.add(node);
            h++;
        }
        // se agrega llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        ArrayList<DNA> dnas = leaf.getValues();
        int i = 0;
        // buscar posicion
        while(i < dnas.size()) {
            int cmp = key.compareTo(dnas.get(i));
            if (cmp < 0) {
                dnas.add(i, key);
                break;
            }
            i++;
        }
        // Si hay overflow de la hoja, se parte y se agrega mediana al padre
        if(dnas.size() > B){
            int m = dnas.size()/2;
            ArrayList<Integer> new_leaf1 = new ArrayList<>();
            ArrayList<Integer> new_leaf2 = new ArrayList<>();
            // dividir segun mediana
            for (int j = 0; j < dnas.size(); j++) {
                if(i < m)
                    new_leaf1.add(dnas.get(j).hashCode());
                else if (i > m)
                    new_leaf2.add(dnas.get(j).hashCode());
            }
            int new_offset1 = offset;
            // primera hoja se guarda en mismo bloque usado por hoja original
            fm.write(new_leaf1, new_offset1);
            // segunda hoja se pone en el siguiente bloque libre
            int new_offset2 = fm.append(new_leaf2);

            // se sube clave mediana
            DNA new_key = dnas.get(m);
            int k = visited_nodes.size() - 1;
            // se agrega una nueva clave y una nueva referencia
            // entonces el nodo padre debe tener ese espacio
            // sino, dividimos y subimos una clave
            BTreeNode node = visited_nodes.get(k);
            while(node.block_size + 2 > B){
                ArrayList<Integer> node_pointers = node.getPointers();
                ArrayList<DNA> node_keys = visited_nodes.get(k).getKeys();
                // agregar mediana
                for (int j = 0; j < node_keys.size(); j++) {
                    if(key.compareTo(node_keys.get(j)) < 0){
                        node_keys.add(j, new_key);
                        node_pointers.set(j, new_offset1);
                        node_pointers.add(j + 1, new_offset2);
                    }
                }
                m = node_keys.size()/2;
                ArrayList<Integer> new_node1 = new ArrayList<>();
                ArrayList<Integer> new_node2 = new ArrayList<>();
                // el primer nodo no recibe la referencia del indice m
                // 1 1 2 2 3 3 4 4 5 5 6 6 7
                // x o x o x ô x o x o x
                // x o x o x ô x o x o x o x
                for (int j = 0; j < node_keys.size(); j++) {
                    if(j < m) {
                        new_node1.add(node_pointers.get(j));
                        new_node1.add(node_keys.get(j).hashCode());
                    } else if (j > m) {
                        new_node2.add(node_pointers.get(j));
                        new_node2.add(node_keys.get(j).hashCode());
                    }
                }
                new_node1.add(node_pointers.get(m)); //agregar referencia m
                new_node2.add(node_pointers.get(node_keys.size())); //agregar ultima referencia
                new_offset1 = node.getOffset();
                fm.write(new_node1, new_offset1, true);
                new_offset2 = fm.append(new_node2, true);
                new_key = node_keys.get(m);
                k--;
                if(k < 0)
                    break;
                node = visited_nodes.get(k);
            }
            // se dividimos la raiz, se tiene que crear una raiz nueva
            // con una clave y dos referencias
            if(k < 0) {
                ArrayList<Integer> new_node = new ArrayList<>();
                new_node.add(new_offset1);
                new_node.add(new_key.hashCode());
                new_node.add(new_offset2);
                // nuevo nodo se pone en el siguiente bloque libre
                this.offset_raiz = fm.append(new_node, true);
            } else { // si no, solo se tiene que insertar nueva clave y referencias en nodo
                ArrayList<Integer> node_pointers = node.getPointers();
                ArrayList<DNA> node_keys = visited_nodes.get(k).getKeys();
                for (int j = 0; j < node_keys.size(); j++) {
                    if(key.compareTo(node_keys.get(j)) < 0){
                        node_keys.add(j, new_key);
                        node_pointers.set(j, new_offset1);
                        node_pointers.add(j + 1, new_offset2);
                    }
                }
            }
        }
        else //si no hay rebalse, solo se escribe nodo actualizado
            fm.write(leaf.getIntValues(), offset, false);
    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        int offset = offset_raiz;
        ArrayList<Integer> values = fm.read(offset);
        int isLeaf = (values.get(0) >> 31) & 0x1;
        int h = 0;
        // Buscar hoja bajando por nodos correspondiente
        while (isLeaf != 0) //hasta encontrar una hoja
        {
            BTreeNode node = new BTreeNode(this.B, offset, values);
            ArrayList<DNA> dnas = node.getKeys();
            int i = 0;
            while(i < dnas.size()) {
                int cmp = key.compareTo(dnas.get(i));
                if(cmp <= 0)
                    break;
                i++;
            }
            offset = node.getPointers().get(i);
            h++;
        }
        // se agrega llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        ArrayList<DNA> dnas = leaf.getValues();
        int i = 0;
        // buscar posicion, retornar true si encuentra la llave
        while(i < dnas.size()) {
            if (key.compareTo(dnas.get(i)) == 0) {
                return true;
            }
            i++;
    }
        return false;
    }
}
