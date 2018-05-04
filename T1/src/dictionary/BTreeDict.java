package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;

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
            this.block_size = values.get(0) & 0x7fffffff;
            for(int i = 1; i <= this.block_size; i++){
                if(i % 2 != 0) //indices impares son referencias
                    this.pointers.add(values.get(i));
                else //indices pares son claves
                    this.keys.add(new DNA(values.get(i)));
            }
        }


        private int getOffset() {
            return this.offset;
        }

        private ArrayList<Integer> getPointers() {
            return this.pointers;
        }

        private ArrayList<DNA> getKeys() {
            return this.keys;
        }

        private boolean insert(DNA key, int offset1, int offset2) {
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
            return true;
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
            for(int i = 1; i <= this.block_size; i++)
                this.values.add(new DNA(values.get(i)));
        }

        private int getOffset() {
            return this.offset;
        }

        private ArrayList<DNA> getValues() {
            return this.values;
        }

        private boolean insert(DNA key) {
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
            return true;
        }

        private ArrayList<Integer> getIntValues() {
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
    private boolean debug;

    public BTreeDict(String filename, int B) {
        this(filename, B, false);
    }

    public BTreeDict(String filename, int B, boolean debug) {
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.height = 0;
        this.offset_raiz = -1;
        this.debug = debug;
    }

    public void put(DNA key, long value) {
        //si no hay ningun valor, solo se agrega un nuevo bloque hoja
        if(this.offset_raiz == -1) {
            ArrayList<Integer> first_leaf = new ArrayList<>();
            first_leaf.add(key.hashCode());
            this.offset_raiz = fm.append(first_leaf);
            return;
        }
        int offset = offset_raiz;
        // leer considerando que bms del primer valor entrega tipo de nodo
        ArrayList<Integer> values = fm.read(offset, true);
        int isLeaf = (values.get(0) >> 31) & 0x1;
        ArrayList<BTreeNode> visited_nodes = new ArrayList<>();
        int h = 0;
        // Buscar hoja bajando por nodos correspondiente
        while (isLeaf != 0) //hasta encontrar una hoja
        {
            BTreeNode node = new BTreeNode(this.B, offset, values);
            ArrayList<DNA> dnas = node.getKeys();
            int i = 0;
            // buscamos clave en nodo
            while(i < dnas.size()) {
                int cmp = key.compareTo(dnas.get(i));
                if(cmp <= 0) //se encontro clave donde se puede insertar
                    break;
                i++;
            }
            offset = node.getPointers().get(i); //guardar referencia de sgte nodo para ser leido
            //se revisa siguiente bloque
            values = fm.read(offset, true);
            isLeaf = (values.get(0) >> 31) & 0x1;
            visited_nodes.add(node); // mantener registro de nodos visitados
            h++;
        }
        // se agrega llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        leaf.insert(key);
        ArrayList<DNA> dnas = leaf.getValues(); // valores vienen ordenados
        // Si hay rebalse de la hoja, se divide y se agrega mediana al padre
        if(dnas.size() > B){
            int m = (dnas.size()-1)/2;
            ArrayList<Integer> new_leaf1 = new ArrayList<>();
            ArrayList<Integer> new_leaf2 = new ArrayList<>();
            // dividir segun mediana
            for (int j = 0; j < dnas.size(); j++) {
                if(j <= m) // nuevo nodo izquierdo se queda con mediana y sus predecesores
                    new_leaf1.add(dnas.get(j).hashCode());
                else if (j > m) // nuevo nodo derecho se queda con lo sucesores de la mediana
                    new_leaf2.add(dnas.get(j).hashCode());
            }
            // primera hoja se guarda en mismo bloque usado por hoja original
            int new_offset1 = offset;
            fm.write(new_leaf1, new_offset1);
            // segunda hoja se pone en el siguiente bloque libre en el archivo
            int new_offset2 = fm.append(new_leaf2);
            if(debug) System.out.println("Split leaf " + offset
                    + " into leaf " + new_offset1 + " with blocksize: " + new_leaf1.size()
                    + " and leaf " + new_offset2 + " with blocksize: " + new_leaf2.size());

            // se sube clave mediana
            DNA new_key = dnas.get(m);
            // se revisara registro de nodos visitados de abajo hacia arriba
            int k = visited_nodes.size() - 1;
            // se agrega una nueva clave y una nueva referencia
            BTreeNode node = null;
            // entonces el nodo padre debe tener ese espacio
            if(debug) System.out.println("Visited nodes: " + (k + 1));
            if(k >= 0) {
                node = visited_nodes.get(k);
                if(debug) System.out.println("Checking node: " + node.getOffset());
                //System.out.println("Checking block: " + node.getOffset() + " with blocksize: " + node.block_size);
                while(node.block_size + 2 > B){
                    // sino, dividimos y subimos una clave
                    if(debug) System.out.println("Subir clave "
                        + Integer.toHexString(new_key.hashCode())
                        + "a bloque " + node.getOffset());
                    node.insert(new_key, new_offset1, new_offset2);
                    ArrayList<Integer> node_pointers = node.getPointers();
                    ArrayList<DNA> node_keys = visited_nodes.get(k).getKeys();
                    // dividir
                    m = (node_keys.size()-1)/2;
                    ArrayList<Integer> new_node1 = new ArrayList<>();
                    ArrayList<Integer> new_node2 = new ArrayList<>();
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
                    // el primer nodo no recibe la referencia del indice m en loop
                    new_node1.add(node_pointers.get(m)); //agregar referencia m
                    new_node2.add(node_pointers.get(node_keys.size())); //agregar ultima referencia
                    // primer nodo se guarda en mismo bloque usado por nodo original
                    new_offset1 = node.getOffset();
                    fm.write(new_node1, new_offset1, true);
                    // segundo nodo se pone en el siguiente bloque libre en el archivo
                    new_offset2 = fm.append(new_node2, true);
                    if(debug) System.out.println("Split node " + node.getOffset()
                        + " into node " + new_offset1 + " with blocksize: " + new_node1.size()
                        + " and node " + new_offset2 + " with blocksize: " + new_node2.size());
                    // se guarda nueva clave
                    new_key = node_keys.get(m);
                    k--;
                    if(k < 0) // si no quedan nodos por revisar, hay que crear uno nuevo
                        break;
                    // agregar clave a siguiente nodo
                    node = visited_nodes.get(k);
                }
            }
            if(debug) System.out.println("Subir clave "
                    + Integer.toHexString(new_key.hashCode())
                    + "a bloque " + this.offset_raiz);
            // si dividimos la raiz, se tiene que crear una raiz nueva
            // con una clave y dos referencias
            if(k < 0) {
                ArrayList<Integer> new_node = new ArrayList<>();
                new_node.add(new_offset1);
                new_node.add(new_key.hashCode());
                new_node.add(new_offset2);
                // nuevo nodo se pone en el siguiente bloque libre
                this.offset_raiz = fm.append(new_node, true);
                if(debug) System.out.println("Adding new raiz: "
                        + this.offset_raiz + " with blocksize: " + new_node.size()
                        + "(" + Integer.toHexString(new_key.hashCode()) + ")");
            } else { // si no, solo se tiene que insertar nueva clave y referencias en nodo
                node.insert(new_key, new_offset1, new_offset2);
                ArrayList<Integer> node_pointers = node.getPointers();
                ArrayList<DNA> node_keys = visited_nodes.get(k).getKeys();
                ArrayList<Integer> new_node = new ArrayList<>();
                for(int j = 0; j < node_keys.size(); j++) {
                    new_node.add(node_pointers.get(j));
                    new_node.add(node_keys.get(j).hashCode());
                }
                new_node.add(node_pointers.get(node_keys.size()));
                //se escribe nodo actualizado en bloque donde estaba
                fm.write(new_node, node.getOffset(), true);
                if(debug) System.out.println("Updating node: " + node.getOffset()
                        + " with blocksize: " + new_node.size()
                        + "(" + Integer.toHexString(new_key.hashCode()) + ")");
            }
        }
        else { //si no hay rebalse, solo se escribe nodo actualizado
            ArrayList<Integer> new_leaf = new ArrayList<>();
            for(DNA dna: dnas)
                new_leaf.add(dna.hashCode());
            fm.write(new_leaf, leaf.getOffset(), false);
            if(debug) System.out.println("Updating leaf: " + leaf.getOffset() + " with blocksize: " + new_leaf.size()
                    + "(" + Integer.toHexString(key.hashCode()) + ")");
        }
    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        //si no hay ningun valor, solo se agrega un nuevo bloque hoja
        if(this.offset_raiz == -1)
            return false;
        int offset = offset_raiz;
        // leer considerando que bms del primer valor entrega tipo de nodo
        ArrayList<Integer> values = fm.read(offset, true);
        int isLeaf = (values.get(0) >> 31) & 0x1;
        int h = 0;
        // Buscar hoja bajando por nodos correspondiente
        while (isLeaf != 0) //hasta encontrar una hoja
        {
            BTreeNode node = new BTreeNode(this.B, offset, values);
            ArrayList<DNA> dnas = node.getKeys();
            int i = 0;
            // buscamos clave en nodo
            while(i < dnas.size()) {
                int cmp = key.compareTo(dnas.get(i));
                if(cmp <= 0) //se encontro clave donde se puede insertar
                    break;
                i++;
            }
            offset = node.getPointers().get(i); //guardar referencia de sgte nodo para ser leido
            //se revisa siguiente bloque
            values = fm.read(offset, true);
            isLeaf = (values.get(0) >> 31) & 0x1;
            h++;
        }
        // se agrega llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        ArrayList<DNA> dnas = leaf.getValues(); // valores vienen ordenados
        // buscar posicion
        int i = 0;
        while(i < dnas.size()) {
            int cmp = key.compareTo(dnas.get(i));
            if (cmp == 0) { //clave existe
                return true;
            }
            i++;
        } //Si se sale del loop, no se encontró la clave
        return false;
    }
}
