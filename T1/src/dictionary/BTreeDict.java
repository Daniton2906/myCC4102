package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class BTreeDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B;
    private final FileManager fm;
    private int height;
    private int offset_raiz;
    private boolean debug;
    private int in_counter = 0;
    private int out_counter = 0;
    private int size = 0;
    private int used_blocks = 0;

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
            first_leaf.add(key.hashCode()); size+= 4;
            this.offset_raiz = fm.append(first_leaf); out_counter++; used_blocks+= 1;
            if(debug) System.out.println("Updating leaf: " + this.offset_raiz + " with blocksize: " + first_leaf.size()
                    + "(" + Integer.toHexString(key.hashCode()) + ")");
            return;
        }
        int offset = offset_raiz;
        // leer considerando que bms del primer valor entrega tipo de nodo
        ArrayList<Integer> values = fm.read(offset, true); in_counter++;
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
            values = fm.read(offset, true); in_counter++;
            isLeaf = (values.get(0) >> 31) & 0x1;
            visited_nodes.add(node); // mantener registro de nodos visitados
            h++;
        }
        // se agrega llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        leaf.insert(key); size+= 4;
        if(debug) System.out.println("Updating leaf: " + leaf.getOffset() + " with blocksize: " + leaf.getBlocksize()
                + "(" + Integer.toHexString(key.hashCode()) + ")");
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
            fm.write(new_leaf1, new_offset1); out_counter++;
            // segunda hoja se pone en el siguiente bloque libre en el archivo
            int new_offset2 = fm.append(new_leaf2); out_counter++;  used_blocks++;
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
                while(node.getBlocksize() + 2 > B){
                    // sino, dividimos y subimos una clave
                    if(debug) System.out.println("Subir clave "
                        + Integer.toHexString(new_key.hashCode())
                        + "a bloque " + node.getOffset());
                    node.insert(new_key, new_offset1, new_offset2); size+= 8;
                    ArrayList<Integer> node_pointers = node.getPointers();
                    ArrayList<DNA> node_keys = visited_nodes.get(k).getKeys();
                    // dividir
                    m = (node_keys.size()-1)/2;
                    ArrayList<Integer> new_node1 = new ArrayList<>(),
                            new_node2 = new ArrayList<>();
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
                    fm.write(new_node1, new_offset1, true); out_counter++;
                    // segundo nodo se pone en el siguiente bloque libre en el archivo
                    new_offset2 = fm.append(new_node2, true); out_counter++;  used_blocks++;
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
                new_node.add(new_offset1); size+= 4;
                new_node.add(new_key.hashCode()); size+= 4;
                new_node.add(new_offset2); size+= 4;
                // nuevo nodo se pone en el siguiente bloque libre
                this.offset_raiz = fm.append(new_node, true); out_counter++;  used_blocks++;
                if(debug) System.out.println("Adding new raiz: "
                        + this.offset_raiz + " with blocksize: " + new_node.size()
                        + "(" + Integer.toHexString(new_key.hashCode()) + ")");
            } else { // si no, solo se tiene que insertar nueva clave y referencias en nodo
                node.insert(new_key, new_offset1, new_offset2); size+= 8;
                ArrayList<Integer> new_node = node.getIntValues();
                //se escribe nodo actualizado en bloque donde estaba
                fm.write(new_node, node.getOffset(), true); out_counter++;
                if(debug) System.out.println("Updating node: " + node.getOffset()
                        + " with blocksize: " + new_node.size()
                        + "(" + Integer.toHexString(new_key.hashCode()) + ")");
            }
        }
        else { //si no hay rebalse, solo se escribe nodo actualizado
            ArrayList<Integer> new_leaf = new ArrayList<>();
            for(DNA dna: dnas)
                new_leaf.add(dna.hashCode());
            fm.write(new_leaf, leaf.getOffset(), false); out_counter++;
        }
    }

    public boolean containsKey(DNA key){
        //si no hay ningun valor, solo se agrega un nuevo bloque hoja
        if(this.offset_raiz == -1)
            return false;
        int offset = offset_raiz;
        // leer considerando que bms del primer valor entrega tipo de nodo
        ArrayList<Integer> values = fm.read(offset, true); in_counter++;
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
            values = fm.read(offset, true); in_counter++;
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

    public void delete(DNA key){
        // si no hay elementos, no hay nada que eliminar
        if(this.offset_raiz == -1) {
            return;
        }
        int offset = offset_raiz;
        // leer considerando que bit mas significativo del primer valor entrega tipo de nodo
        ArrayList<Integer> values = fm.read(offset, true); in_counter++;
        int isLeaf = (values.get(0) >> 31) & 0x1;
        ArrayList<BTreeNode> visited_nodes = new ArrayList<>(); // nodos visitados
        ArrayList<Integer> visited_index = new ArrayList<>(); // indices de ramas visitadas
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
            visited_index.add(i);
            offset = node.getPointers().get(i); //guardar referencia de sgte nodo para ser leido
            //se revisa siguiente bloque
            values = fm.read(offset, true); in_counter++;
            isLeaf = (values.get(0) >> 31) & 0x1;
            visited_nodes.add(node); // mantener registro de nodos visitados
            h++;
        }
        // se elimina llave a hoja
        BTreeLeaf leaf = new BTreeLeaf(this.B, offset, values);
        if (!leaf.delete(key)){
            System.out.println("(!) Key " + Integer.toHexString(key.hashCode())
                    + " is not contained in this Btree");
            return;
        } else {
            size-=4;
            if(debug) System.out.println("Delete " + Integer.toHexString(key.hashCode())
                + " from leaf: " + leaf.getOffset()
                + " with blocksize: " + leaf.getValues().size());
        }
        // Si menos de la mitad del bloque queda ocupado en un nodo que no es raiz, se fusiona con el vecino
        if(leaf.getValues().size() < B/2 && offset != this.offset_raiz) {
            int n_k = h - 1; // indice ultimo nodo visitado
            int v_k = visited_index.get(n_k); // indice de offset de hoja en ultimo nodo visitado (padre)
            BTreeNode father_node = visited_nodes.get(n_k); // referencia a nodo padre
            int n = father_node.getKeys().size(), // numero de claves en nodo padre
                    v_i = v_k == n ? v_k - 1 : v_k + 1; // se revisa hoja vecina anterior o siguiente
            int v_offset = father_node.getPointers().get(v_i),
                    v_min = Math.min(v_k, v_i); // referencia al menor indicee
            values = fm.read(v_offset, true); in_counter++; // leer valores de hoja vecina
            BTreeLeaf v_leaf = new BTreeLeaf(this.B, v_offset, values); // guardar en un BTreeLeaf
            // unir hojas
            BTreeLeaf new_leaf = v_k == v_min? leaf.join(v_leaf): v_leaf.join(leaf);
            if (debug) System.out.println("Joining leafs " + offset
                    + " and " + v_offset + " in leaf: " + offset
                    + " with blocksize: " + new_leaf.getValues().size());
            //se revisa que no haya rebalse al unir hojas
            if (new_leaf.getValues().size() > B) {
                int m = (new_leaf.getValues().size() - 1) / 2;
                ArrayList<Integer> new_leaf1 = new ArrayList<>(),
                        new_leaf2 = new ArrayList<>();
                for (int j = 0; j < new_leaf.getValues().size(); j++) {
                    if (j <= m) // nuevo nodo izquierdo se queda con mediana y sus predecesores
                        new_leaf1.add(new_leaf.getValues().get(j).hashCode());
                    else if (j > m) // nuevo nodo derecho se queda con lo sucesores de la mediana
                        new_leaf2.add(new_leaf.getValues().get(j).hashCode());
                }
                // primera hoja se guarda en mismo bloque usado por hoja original
                // segunda hoja se pone en donde estaba la otra hoja
                if(v_k < v_i) {
                    fm.write(new_leaf1, offset); out_counter++;
                    fm.write(new_leaf2, v_offset); out_counter++;
                } else {
                    fm.write(new_leaf1, v_offset); out_counter++;
                    fm.write(new_leaf2, offset); out_counter++;
                }
                if (debug) System.out.println("Split leaf " + offset
                        + " into leaf " + offset + " with blocksize: " + new_leaf1.size()
                        + " and leaf " + v_offset + " with blocksize: " + new_leaf2.size());
                if (debug) System.out.println("Put " + Integer.toHexString(new_leaf.getValues().get(m).hashCode())
                        + " at key position " + Math.min(v_k, v_i)
                        + " in node " + father_node.getOffset());
                father_node.replace_key(Math.min(v_k, v_i), new_leaf.getValues().get(m));
                fm.write(father_node.getIntValues(), father_node.getOffset(), true); out_counter++;
            } else { //si no hay rebalse, se escribe hoja
                // primera hoja se guarda en mismo bloque usado por hoja izquierda
                fm.write(new_leaf.getIntValues(), offset, false); out_counter++; used_blocks--;
                // hay una llave y referencia por eliminar en nodo padre
                DNA deleted_key;
                if (v_i < v_k) { // se elimina llave previa
                    deleted_key = father_node.getKeys().get(v_i);
                    father_node.delete(deleted_key, true);
                } else { // se elimina llave siguiente
                    deleted_key = father_node.getKeys().get(v_k);
                    father_node.delete(deleted_key, false);
                } size-=8;
                if (debug) System.out.println("Delete key " + Integer.toHexString(deleted_key.hashCode())
                        + " from node: " + father_node.getOffset()
                        + " with blocksize: " + father_node.getBlocksize());
                // revisar que nodos visitados tengan al menos la mitad del bloque (abajo hacia arriba)
                n_k = n_k - 1; // indice del padre
                BTreeNode current_node = father_node;
                // mientras no estemos en la raiz, existe un padre que revisar
                while (current_node.getOffset() != this.offset_raiz && current_node.getBlocksize() < B/2) {
                    // se fusionan nodos si tienen menos de la mitad del bloque
                    // buscar nodo vecino
                    father_node = visited_nodes.get(n_k); // referencia a este nuevo nodo padre
                    n = father_node.getKeys().size(); // numero de claves en nodo padre
                    v_k = visited_index.get(n_k); // indice de offset de padre del padre
                    v_i = v_k == n ? v_k - 1 : v_k + 1; // se revisa nodo vecino anterior o siguiente
                    v_offset = father_node.getPointers().get(v_i); // referencia de este nodo vecino
                    v_min = Math.min(v_k, v_i);
                    // leer info nodo vecino
                    values = fm.read(v_offset, true); in_counter++; // leer valores de nodo vecino
                    BTreeNode v_node = new BTreeNode(this.B, v_offset, values); // guardar en un BTreeNode
                    int cp_index = v_k < v_i? current_node.getKeys().size() : 0,
                            vp_index = v_k < v_i? 0 : v_node.getKeys().size();
                    DNA z = father_node.getKeys().get(v_min);
                    // si suma de largos es >= B, hacer swap de claves
                    if (current_node.getBlocksize() + v_node.getBlocksize() >= B ) {
                        int vk_index = v_k < v_i? 0 : v_node.getKeys().size() - 1;
                        DNA x1 = v_node.getKeys().get(vk_index);
                        int x1_offset = v_node.getPointers().get(vp_index),
                                yb2_offset =  current_node.getPointers().get(cp_index);
                        if(v_k < v_i) {
                            current_node.insert(z, yb2_offset, x1_offset);
                            v_node.delete(x1, true);
                        } else {
                            current_node.insert(z, x1_offset, yb2_offset);
                            v_node.delete(x1, false);
                        }
                        father_node.replace_key(v_min, x1);
                        fm.write(current_node.getIntValues(), current_node.getOffset(), true); out_counter++;
                        fm.write(v_node.getIntValues(), v_node.getOffset(), true); out_counter++;
                        if (debug) System.out.println("Moved key " + Integer.toHexString(x1.hashCode())
                                + " from node: " + v_node.getOffset()
                                + " with new blocksize: " + v_node.getBlocksize()
                                + " to node: " + father_node.getOffset()
                                + " with new blocksize: " + father_node.getBlocksize());
                        if (debug) System.out.println("Moved key " + Integer.toHexString(z.hashCode())
                                + " from node: " + father_node.getOffset()
                                + " with new blocksize: " + father_node.getBlocksize()
                                + " to node: " + current_node.getOffset()
                                + " with new blocksize: " + current_node.getBlocksize());
                    } else { //sino, juntar nodos bajando la clave que se eliminará arriba
                        // join nodes
                        ArrayList<Integer> new_node = new ArrayList<>();
                        BTreeNode first_node, second_node;
                        if(v_k < v_i) {
                            first_node = current_node;
                            second_node = v_node;
                            father_node.delete(z, false);
                        } else {
                            first_node = v_node;
                            second_node = current_node;
                            father_node.delete(z, true);
                        }
                        for (int i = 0; i < first_node.getKeys().size(); i++) {
                            new_node.add(first_node.getPointers().get(i));
                            new_node.add(first_node.getKeys().get(i).hashCode());
                        }
                        new_node.add(first_node.getPointers().get(first_node.getKeys().size()));
                        new_node.add(z.hashCode());
                        new_node.add(second_node.getPointers().get(0));
                        for (int i = 0; i < second_node.getKeys().size(); i++) {
                            new_node.add(second_node.getKeys().get(i).hashCode());
                            new_node.add(second_node.getPointers().get(i + 1));
                        }
                        fm.write(new_node, current_node.getOffset(), true); size-=8; used_blocks--;
                        if (debug) System.out.println("Joining nodes " + current_node.getOffset()
                                + " and " + v_offset + " in node: " + current_node.getOffset()
                                + " with new blocksize: " + new_node.size());
                        if (debug) System.out.println("Delete key " + Integer.toHexString(z.hashCode())
                                + " from node: " + father_node.getOffset()
                                + " with new blocksize: " + father_node.getBlocksize());
                    }
                    // revisar padre del padre
                    n_k -= 1;
                    current_node = father_node;
                }
                // si nodo es raiz, puede quedar con 0 clave y un hijo, que seria nueva raiz
                if(current_node.getKeys().size() == 0) {
                    if (debug) System.out.println("Raiz " + this.offset_raiz
                            + " without keys, node " + current_node.getPointers().get(0)
                            + " become the new raiz with blocksize: " + current_node.getBlocksize());
                    this.offset_raiz = current_node.getPointers().get(0); size-=4; used_blocks--;
                }
                fm.write(current_node.getIntValues(), current_node.getOffset(), true); out_counter++;
            }
        }
        else { //si no hay escasez, se actualiza hoja
            //si ya no quedan elementos en la raiz (tiene que serlo), arbol esta vacio
            if(leaf.getBlocksize() == 0) {
                this.offset_raiz = -1;
                used_blocks--;
            }
            // si no es la raiz, entonces no hay escasez
            // entonces solo se actualiza hoja
            fm.write(leaf.getIntValues(), leaf.getOffset(), false); out_counter++;
        }

    }

    public void resetIOCounter(){
        in_counter = 0; out_counter = 0;
    }

    public int getIOs(){return in_counter + out_counter;}

    public int getUsedSpace(){
        if(debug) System.out.println("Espacio usado: " + (size) + ", bloques usados: " + used_blocks);
        if(this.offset_raiz == -1)
            return 0;
        return size / used_blocks + 4;}
}
