package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LinealHashDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B, t, p, last;
    private final FileManager fm;
    private ArrayList<Integer> index_reference;

    // Idea: utilizar dos bloques para guardar referencias a bloques, uno mantiene referencia a bloques de memoria
    //       que estan actualmente siendo utilizados, y el segundo mantiene referencia a bloques que han sido
    //       utilizados pero actualmente estan vacios.
    public LinealHashDict(String filename, int B) {
        // caso en que se usa un hashing lineal ya creado(?).


        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.t = 0;
        this.p = 0;
        this.last = 3;

        this.index_reference = new ArrayList<>();
        index_reference.add(2);

        /*
        // referencias en bloque de uso (posiblemente se elimine)..
        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(1); id.add(2);
        this.fm.write(id, 0);

        // referencias en bloque de desuso (posiblemente se elimine).
        id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 1);
        */

        // preparacion de primer bloque activo.
        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 2);
    }

    // inserta una referencia en un bloque, utilizado para agregar referencia
    // a pagina de bloques en uso y en desuso (listo, muy probable que no se use).
    private void addReference(int block_inndex, int reference) {
        int new_reference = reference;
        int last_block = block_inndex;
        ArrayList<Integer> content = this.fm.read(last_block);
        ArrayList<Integer> new_content = new ArrayList<>();

        while(true) {
            if(content.get(0) < B-2) {
                // add reference

                new_content.add(content.get(0) + 1);
                for(int i=1; i<content.get(0); i++)
                    new_content.add(content.get(i));

                new_content.add(new_reference);
                break;

            } else {
                last_block = content.get(B-1);
                content = this.fm.read(last_block);
            }
        }

        if(new_content.get(0) == B-2) {
            new_content.add(this.last);
            this.fm.write(new_content, last_block);

            ArrayList<Integer> last = new ArrayList<Integer>();
            last.add(0);

            this.fm.write(last, this.last);
            this.last++;
        }
    }

    // retorna referencia al primer bloque de la lista enlazada que contine a key (listo).
    private int getReference(DNA key) {
        // page: referencia a la pagina que contiene key.
        int page = key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = key.hashCode() % (1 << t);

        return this.index_reference.get(page);
    }

    // expande estructura de hashing lineal (completo, sin testear).
    private void expand() {
        int ref_expand = this.last;
        this.index_reference.add(ref_expand);
        this.last++;

        ArrayList<Integer> content1 = new ArrayList<>(), content2 = new ArrayList<>();

        int p1 = (p + 1) % (1 << t), p2 = p + 1;

        int ref1 = this.index_reference.get(p1);
        int ref2 = ref_expand;

        // separacion del contenido.
        int aux_ref = ref1;
        ArrayList<Integer> aux_cont = this.fm.read(aux_ref);
        while(true) {
            if(aux_cont.get(0) == 0)
                break;

            for(int i=1; i<=aux_cont.get(0); i++) {
                int hash = aux_cont.get(i);
                if(hash % (1 << (t + 1)) == p1)
                    content1.add(hash);
                else
                    content2.add(hash);
            }

            aux_ref = aux_cont.get(B - 1);
            aux_cont = this.fm.read(aux_ref);
        }

        aux_cont.clear();
        for(int i=0; i<content1.size(); i++) {
            aux_cont.add(content1.get(i));
            if(aux_cont.size() == B - 2 || i == content1.size() - 1) {
                int cant = aux_cont.size();
                aux_cont.add(0, cant);

                ArrayList<Integer> last = new ArrayList<>();

                if(cant == B - 2) {
                    aux_cont.add(this.last);

                    this.fm.write(aux_cont, ref1);
                    last.add(0);
                    this.fm.write(last, this.last);

                    ref1 = this.last;
                    this.last++;
                } else {
                    this.fm.write(aux_cont, ref1);
                }

            }
        }

        aux_cont.clear();
        for(int i=0; i<content2.size(); i++) {
            aux_cont.add(content2.get(i));
            if(aux_cont.size() == B - 2 || i == content2.size() - 1) {
                int cant = aux_cont.size();
                aux_cont.add(0, cant);

                ArrayList<Integer> last = new ArrayList<>();

                if(cant == B - 2) {
                    aux_cont.add(this.last);

                    this.fm.write(aux_cont, ref2);
                    last.add(0);
                    this.fm.write(last, this.last);

                    ref2 = this.last;
                    this.last++;
                } else {
                    this.fm.write(aux_cont, ref2);
                }
            }
        }

        this.p++;
        if(this.p == (1 << (this.t + 1))) {
            this.t++;
        }

    }

    // inserta un elemento en el diccionario (completo, sin testear).
    public void put(DNA key, long value) {
        int reference_page = this.getReference(key);
        ArrayList<Integer> page_content = this.fm.read(reference_page);

        int cant_elements = page_content.get(0);
        while(cant_elements == B - 2) {
            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page);
            cant_elements = page_content.get(0);
        }

        // adicion de la nueva cadena.
        page_content.add(key.hashCode());
        ArrayList<Integer> new_content = new ArrayList<>();
        new_content.add(page_content.get(0) + 1);

        // reescritura del contenido + la insercion
        for(int i=1; i<page_content.size(); i++)
            new_content.add(page_content.get(i));

        // caso en que el bloque se llena.
        if(new_content.get(0) == B - 2) {
            ArrayList<Integer> last = new ArrayList<>();
            last.add(0);
            this.fm.write(last, this.last);

            new_content.add(this.last);
            this.last++;
        }

        this.fm.write(new_content, reference_page);

        /*
        // caso en que se cumple condicion de expansion.
        if(CONDICION){
            expand()
        }
        */
    }

    // contrae estructura de hashing lineal (incompleto).
    private void compress() {
        int last_index = this.p;
        ArrayList<Integer> last_cont = this.fm.read(last_index), content = new ArrayList<>();

        while(true) {
            for(int i=1; i<=last_cont.get(0); i++) {
                content.add(last_cont.get(i));
            }

            if(last_cont.get(0) < B-2)
                break;

            last_index = last_cont.get(B - 1);
            last_cont = this.fm.read(last_index);
        }

        int push_index = this.p % (1 << t);
        ArrayList<Integer> push_cont = this.fm.read(push_index);
        while(true) {
            if(push_cont.size() == B) {
                push_index = push_cont.get(B - 1);
                push_cont = this.fm.read(push_index);

            } else {
                ArrayList<Integer> new_content = new ArrayList<>();
                for(int i=1; i<=push_cont.get(0); i++) {
                    new_content.add(push_cont.get(i));
                }

                for(int i=0; i<content.size(); i++) {
                    new_content.add(content.get(i));
                    if(new_content.size() == B - 2 || i == content.size() - 1) {
                        int cant = new_content.size();
                        new_content.add(0, cant);

                        ArrayList<Integer> last = new ArrayList<>();

                        if(cant == B - 2) {
                            new_content.add(this.last);

                            this.fm.write(new_content, push_index);
                            last.add(0);
                            this.fm.write(last, this.last);

                            push_index = this.last;
                            this.last++;
                        } else {
                            this.fm.write(new_content, push_index);
                        }

                    }
                }
                break;
            }
        }

        this.p--;
        if(this.p < (1 << this.t)) {
            this.t--;
        }
    }

    // metodo para eliminar un elemento de un diccionario (completo, sin testear, falta terminar compress).
    public void delete(DNA key){
        int reference_page = this.getReference(key);
        ArrayList<Integer> content = this.fm.read(reference_page);

        int last_page = reference_page, last_chain = 0;
        ArrayList<Integer> last_content = content, search_content, new_content = new ArrayList<>();

        // last_block: referencia al ultimo bloque.
        // search_block: referencia al bloque con el elemento buscado.
        int last_block = reference_page, search_block = -1, search_pos = -1;

        while(true) {
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

        /*
        // caso en que se cumple condicion de compresion.
        if(CONDICION){
            compress()
        }
        */
    }

    // metodo que determina si un elemento se encuentra dentro del diccionario (listo)
    public boolean containsKey(DNA key){
        // obtencion de referencia y contenido de la pagina buscada.
        int reference_page = this.getReference(key);
        ArrayList<Integer> page_content = this.fm.read(reference_page);

        boolean res = false;
        int cant_elements = page_content.get(0);

        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.

        while(true) {
            for(int i=1; i<=cant_elements; i++) {
                if(page_content.get(i) == key.hashCode())
                    res = true;
            }

            if(cant_elements < B - 2)
                break;

            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page);
            cant_elements = page_content.get(0);
        }

        return res;
    }

    public void resetIOCounter(){}

    public int getIOs(){return 0;}

    public int getUsedSpace(){return 0;}
}
