package dictionary;

import utils.DNA;
import utils.FileManager;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExtHashDict implements Dictionary {

    private class Node {

        private boolean has_page = false;
        private Node left = null, right = null, parent = null;
        private int reference, altura;


        public Node(int reference) {
            this.has_page = true;
            this.reference = reference;
            this.altura = 0;
        }

        public Node getLeftNode() {  return this.left;  }

        public void addLeftNode(Node left) {
            left.setParent(this);
            this.left = left;
        }

        public Node getRightNode() {  return this.right;  }

        public void addRightNode(Node right) {
            right.setParent(this);
            this.right = right;
        }

        public int getReference() {  return this.reference;  }

        public void setReference(int ref) {  this.reference = ref;  }

        public void activateReference(boolean b) {  this.has_page = b;  }

        public boolean hasReference() {  return this.has_page;  }

        public int getAltura() {  return this.altura;  }

        public void setAltura(int altura) {  this.altura = altura;  }

        public Node getParent() {  return this.parent;  }

        public void setParent(Node p) {  this.parent = p;  }

    }

    // tamano bloque = 4KB o 512B
    private int B, last;
    private final FileManager fm;
    private Node tree_reference;

    // una pagina es equivalente a un bloque.
    public ExtHashDict(String filename, int B) {
        // caso en que se usa un hashing lineal ya creado(?).

        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.tree_reference = new Node(1);  // bloque cero mantiene los bloques en desuso.
        this.last = 2;

        ArrayList<Integer> id = new ArrayList<>();
        id.add(0);
        this.fm.write(id, 1);

        this.fm.write(id, 0);

    }

    // retorna nodo al que esta asociado a la pagina buscada segun el hash (completo, sin testear).
    private Node getReference(DNA key) {
        int hash = key.hashCode();

        Node actual_node = this.tree_reference;
        int shift = 0;
        while(!actual_node.hasReference()) {
            if(shift >= 30)
                break;

            if((hash & (1 << shift)) != 0) {
                actual_node = this.tree_reference.getLeftNode();
            } else {
                actual_node = this.tree_reference.getRightNode();
            }
            shift++;

        }

        return actual_node;

    }

    // metodo de duplicacion de nodos (completo, falta testear).
    private void duplicate(Node nodo) {
        int reference_page = nodo.getReference();
        ArrayList<Integer> content = this.fm.read(reference_page);

        int shift = nodo.getAltura();
        nodo.activateReference(false);

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();

        for(int i=1; i<=content.get(0); i++) {
            int chain = content.get(i);
            if((chain & (1 << shift)) == 0) {
                left.add(chain);
            } else {
                right.add(chain);
            }
        }

        left.add(0, left.size());
        right.add(0, right.size());

        this.fm.write(left, reference_page);
        Node l = new Node(reference_page);
        l.setAltura(shift + 1);

        this.fm.write(right, this.last);
        Node r = new Node(this.last); this.last++;
        r.setAltura(shift + 1);

        nodo.addLeftNode(l);
        nodo.addRightNode(r);

        ArrayList<Integer> last = new ArrayList<>();
        last.add(0);
        if(left.get(0) == B-2 && shift + 1 < 30) {
            this.duplicate(l);
        } else if(left.get(0) == B-2 && shift + 1 == 30) {
            left.add(this.last);
            this.fm.write(left, l.getReference());

            this.fm.write(last, this.last);
            this.last++;
        }

        if(right.get(0) == B-2 && shift + 1 < 30) {
            this.duplicate(r);
        } else if(right.get(0) == B-2 && shift + 1 == 30) {
            right.add(this.last);
            this.fm.write(right, r.getReference());

            this.fm.write(last, this.last);
            this.last++;
        }
    }

    // inserta elemento en el hash (completo, falta testear).
    public void put(DNA key, long value) {
        Node actual_node = this.getReference(key);

        int reference_page = actual_node.getReference();
        ArrayList<Integer> content = this.fm.read(reference_page);

        int last_page = reference_page;
        ArrayList<Integer> last_content = content;

        int total_elements = 0, altura = actual_node.getAltura();
        while(true) {
            total_elements += last_content.get(0);
            if(last_content.get(0) != B-2) {
                ArrayList<Integer> new_content = new ArrayList<>();

                new_content.add(last_content.get(0) + 1);
                for(int i=1; i<=last_content.get(0); i++)
                    new_content.add(last_content.get(i));

                new_content.add(key.hashCode());
                if(new_content.get(0) == B-2) {
                    new_content.add(this.last);
                    this.fm.write(new_content, reference_page);

                    ArrayList<Integer> last = new ArrayList<>();
                    last.add(0);
                    this.fm.write(last, this.last);

                    this.last++;

                }
                total_elements++;  // por el elmento que se inserto.
                break;

            }

            last_page = last_content.get(B-1);
            last_content = this.fm.read(last_page);
        }

        // se lleno la pagina y aun no se llega al final del hashing.
        if(total_elements >= B - 2 && altura < 30){
            this.duplicate(actual_node);
        }
    }

    // comprime un par de nodos hermanos, si la cantidad de elementos es muy baja (completo, falta testear).
    private void compress(Node nodo) {
        Node parent = nodo.getParent();

        Node left = parent.getLeftNode();
        Node right = parent.getRightNode();

        int left_reference = left.getReference();
        ArrayList<Integer> left_content = this.fm.read(left_reference);

        int right_reference = right.getReference();
        ArrayList<Integer> right_content = this.fm.read(right_reference);

        // en caso de que sea posible juntar las paginas.
        if(right_content.get(0) + left_content.get(0) < B-2) {
            ArrayList<Integer> new_content = new ArrayList<>();
            new_content.add(right_content.get(0) + left_content.get(0));

            for(int i=1; i<= left_content.get(0); i++)
                new_content.add(left_content.get(i));

            for(int i=1; i<= right_content.get(0); i++)
                new_content.add(right_content.get(i));

            parent.activateReference(true);
            parent.setReference(left_reference);
            // referencia derecha dejarla en las referencias en desuso.

            this.fm.write(new_content, left_reference);
        }
    }

    // elimina el elemento key del diccionario, si es que esta contenido (completo, falta testear).
    public void delete(DNA key){
        Node actual_node = this.getReference(key);

        int reference_page = actual_node.getReference();
        ArrayList<Integer> content = this.fm.read(reference_page);

        int last_page = reference_page, last_chain = 0;
        ArrayList<Integer> last_content = content, search_content;

        int total_elements = 0, altura = actual_node.getAltura();

        // last_block: referencia al ultimo bloque.
        // search_block: referencia al bloque con el elemento buscado.
        int last_block = reference_page, search_block = -1, search_pos = -1;

        while(true) {
            total_elements += last_content.get(0);
            if(search_block == -1) {
                for (int i = 1; i <= last_content.get(0); i++) {
                    if (last_content.get(i) == key.hashCode()) {
                        search_pos = i;
                        search_block = last_page;
                        break;
                    }
                }
            }

            if(last_content.get(0) != 0) {
                last_block = last_page;
                last_chain = last_content.get(last_content.get(0));
            }

            if(last_content.get(0) != B - 2)
                break;

            last_page = last_content.get(B-1);
            last_content = this.fm.read(last_page);
        }

        ArrayList<Integer> new_content = new ArrayList<>();
        if(search_block != -1) {
            // se encontro el elemento buscado.
            // search_block: referencia al bloque que contiene la buscado.
            // last_block: referencia al ultimo bloque de la lista enlazada.

            search_content = this.fm.read(search_block);
            last_content = this.fm.read(last_block);

            if(search_block == last_block) {
                // elemento buscado estaba en la ultima pagina de la lista enlazada.
                new_content.add(search_content.get(0) - 1);
                for(int i=1; i<=search_content.get(0); i++) {
                    if(i != search_pos)
                        new_content.add(search_content.get(i));

                }

            } else {
                // elemento buscado no esta en la ultima pagina de la lista enlazada.
                new_content.add(search_content.get(0));
                for(int i=1; i<=search_content.get(0); i++) {
                    if(i != search_pos)
                        new_content.add(search_content.get(i));
                    else
                        new_content.add(last_chain);

                }

                ArrayList<Integer> new_last_content = new ArrayList<>();
                new_last_content.add(last_content.get(0) - 1);
                for(int i=1; i<last_content.get(0); i++) {
                    new_last_content.add(last_content.get(i));

                }
                this.fm.write(new_last_content, last_block);

            }
            this.fm.write(new_content, search_block);
        }

        // la pagina contiene pocos elementos, y no es parte del primer nodo

        if(total_elements < (B - 2) / 2 && search_block != -1 && 0 < altura){
            this.compress(actual_node);
        }

    }

    // retorna true si la cadena de DNA se encuentra en el diccionario (completo, falta testear).
    public boolean containsKey(DNA key){
        Node actual_node = this.getReference(key);

        int reference_page = actual_node.getReference(), hash = key.hashCode();
        ArrayList<Integer> content = this.fm.read(reference_page);

        boolean res = false;

        while(true) {
            int cant = content.get(0);

            for(int i=1; i<=cant; i++) {
                if(content.get(i) == hash) {
                    res = true;
                }
            }

            if(res)
                break;

            if(content.size() != B)
                break;

            reference_page = content.get(B-1);
            content = this.fm.read(reference_page);

        }

        return res;
    }

    public void resetIOCounter(){}

    public int getIOs(){return 0;}

    public int getUsedSpace(){return 0;}
}
