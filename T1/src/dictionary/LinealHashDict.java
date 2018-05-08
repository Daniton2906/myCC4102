package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LinealHashDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B, t, p, last, type, in_counter, out_counter;
    private final FileManager fm;
    private ArrayList<Integer> index_reference;
    private boolean debug;
    private int total_in = 0, total_active_block = 1;

    // Idea: utilizar dos bloques para guardar referencias a bloques, uno mantiene referencia a bloques de memoria
    //       que estan actualmente siendo utilizados, y el segundo mantiene referencia a bloques que han sido
    //       utilizados pero actualmente estan vacios.
    public LinealHashDict(String filename, int B, int type, boolean debug) {
        // caso en que se usa un hashing lineal ya creado(?).

        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.type = type;
        this.B = B;
        this.t = 0;
        this.p = 0;
        this.last = 1;

        this.debug = debug;
        this.in_counter = 0;
        this.out_counter = 0;
        this.index_reference = new ArrayList<>();
        index_reference.add(0);

        ArrayList<Integer> id = new ArrayList<>();
        id.add(0);
        this.fm.write(id, 0); this.out_counter++;
    }

    // retorna referencia al primer bloque de la lista enlazada que contine a key (listo).
    private int getReference(DNA key) {
        // page: referencia a la pagina que contiene key.
        int page = key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = key.hashCode() % (1 << t);

        if(this.debug)
            System.out.println("LinealHash::getReference >> llave " + key.hashCode() + " esta guardada en " + page);

        return this.index_reference.get(page);
    }

    // expande estructura de hashing lineal (completo, con test).
    private void expand() {
        if(this.debug)
            System.out.println("LinealHash::expand >> expandiendo...");

        ArrayList<Integer> content1 = new ArrayList<>(), content2 = new ArrayList<>();

        int p1 = (p + 1) % (1 << t), p2 = p + 1;
        if(this.debug)
            System.out.println("LinealHash::expand >> posiciones en arreglo de indices: " + p1 + " " + p2);

        // separacion del contenido.
        int aux_ref = this.index_reference.get(p1);
        ArrayList<Integer> aux_cont = this.fm.read(aux_ref); this.in_counter++;
        while(true) {
            this.total_active_block--;
            if(this.debug)
                System.out.println("LinealHash::expand >> procesando pagina: " + aux_ref);

            for(int i=1; i<=aux_cont.get(0); i++) {
                int hash = aux_cont.get(i);
                if(hash % (1 << (t + 1)) == p1)
                    content1.add(hash);
                else
                    content2.add(hash);
            }

            if(aux_cont.get(0) < B - 2) {
                if(this.debug)
                    System.out.println("LinealHash::expand >> lectura completa de paginas");
                break;
            }

            aux_ref = aux_cont.get(B - 1);
            aux_cont = this.fm.read(aux_ref); this.in_counter++;
        }

        if(this.debug) {
            System.out.println("LinealHash::expand >> contenido 1:");
            for(int i=0; i<content1.size(); i++)
                System.out.println("  " + content1.get(i));
        }

        if(this.debug) {
            System.out.println("LinealHash::expand >> contenido 2:");
            for(int i=0; i<content2.size(); i++)
                System.out.println("  " + content2.get(i));
        }

        if(this.debug) {
            System.out.println("LinealHash::expand >> insercion contenido 1:");
        }

        int ref1 = this.index_reference.get(p1);
        if(this.debug)
            System.out.println("LinealHash::expand >> referencia 1: " + ref1);

        aux_cont.clear();
        for(int i=0; i<content1.size(); i++) {
            aux_cont.add(content1.get(i));
            if(aux_cont.size() == B - 2 || i == content1.size() - 1) {
                this.total_active_block++;
                int cant = aux_cont.size();
                aux_cont.add(0, cant);

                ArrayList<Integer> last = new ArrayList<>();

                if(cant == B - 2) {
                    if(this.debug)
                        System.out.println("LinealHash::expand >> llenado de pagina contenido 1, nueva pagina: " + this.last);

                    aux_cont.add(this.last);

                    if(this.debug)
                        System.out.println("LinealHash::expand >> escribiendo en: " + ref1);

                    this.fm.write(aux_cont, ref1); this.out_counter++;
                    last.add(0);
                    this.fm.write(last, this.last); this.out_counter++;

                    ref1 = this.last;
                    this.last++;
                } else {
                    if(this.debug)
                        System.out.println("LinealHash::expand >> escribiendo en: " + ref1);
                    this.fm.write(aux_cont, ref1); this.out_counter++;
                }

                if(this.debug) {
                    System.out.println("LinealHash::expand >> nuevo contenido:");
                    for(int j=0; j<aux_cont.size(); j++)
                        System.out.println("  " + aux_cont.get(j));
                }

                aux_cont.clear();

            }
        }

        int ref2 = this.last;
        this.index_reference.add(ref2);
        if(this.debug)
            System.out.println("LinealHash::expand >> referencia 2: " + ref2);
        this.last++;

        if(this.debug)
            System.out.println("LinealHash::expand >> fin insercion contenido 1:");

        if(this.debug)
            System.out.println("LinealHash::expand >> insercion contenido 2:");

        aux_cont.clear();
        for(int i=0; i<content2.size(); i++) {
            aux_cont.add(content2.get(i));
            if(aux_cont.size() == B - 2 || i == content2.size() - 1) {
                this.total_active_block++;
                int cant = aux_cont.size();
                aux_cont.add(0, cant);

                ArrayList<Integer> last = new ArrayList<>();

                if(cant == B - 2) {
                    if(this.debug)
                        System.out.println("LinealHash::expand >> llenado de pagina contenido 2, nueva pagina: " + this.last);

                    aux_cont.add(this.last);

                    if(this.debug)
                        System.out.println("LinealHash::expand >> escribiendo en: " + ref1);
                    this.fm.write(aux_cont, ref2); this.out_counter++;
                    last.add(0);
                    this.fm.write(last, this.last); this.out_counter++;

                    ref2 = this.last;
                    this.last++;
                } else {
                    if(this.debug)
                        System.out.println("LinealHash::expand >> escribiendo en: " + ref1);
                    this.fm.write(aux_cont, ref2); this.out_counter++;
                }

                if(this.debug) {
                    System.out.println("LinealHash::expand >> nuevo contenido:");
                    for(int j=0; j<aux_cont.size(); j++)
                        System.out.println("  " + aux_cont.get(j));
                }
                aux_cont.clear();
            }
        }

        if(this.debug)
            System.out.println("LinealHash::expand >> fin insercion contenido 2:");

        if(this.debug)
            System.out.println("LinealHash::expand >> expancion valor p: " + this.p + " -> " + (this.p + 1));
        this.p++;
        if(this.p + 1 == (1 << (this.t + 1))) {
            if(this.debug)
                System.out.println("LinealHash::expand >> expancion valor t: " + this.t + " -> " + (this.t + 1));
            this.t++;
        }
    }

    // inserta un elemento en el diccionario (completo, con test).
    public void put(DNA key, long value) {
        this.total_in++;
        if(this.debug)
            System.out.println("LinealHash::put >> valor de hash: " + key.hashCode());

        int reference_page = this.getReference(key);
        ArrayList<Integer> page_content = this.fm.read(reference_page); this.in_counter++;

        if(this.debug){
            System.out.println("LinealHash::put >> contenido en pagina " + reference_page + ": ");
            for(int i=0; i<page_content.size(); i++) {
                System.out.println("  " + page_content.get(i));
            }
        }

        int cant_elements = page_content.get(0);
        int total_elements = cant_elements, prom_acces = cant_elements, mult = 2;
        while(cant_elements == B - 2) {
            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page); this.in_counter++;
            cant_elements = page_content.get(0);

            total_elements += cant_elements;
            prom_acces += (cant_elements * mult);
            mult++;

            if(this.debug)
                System.out.println("LinealHash::put >> pagina llena, cambiando referencia por " + reference_page);
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
            total_active_block++;
            if(this.debug)
                System.out.println("LinealHash::put >> pagina llenada, agreagando referencia " + this.last);

            ArrayList<Integer> last = new ArrayList<>();
            last.add(0);
            this.fm.write(last, this.last); this.out_counter++;

            new_content.add(this.last);
            this.last++;
        }

        if(this.debug){
            System.out.println("LinealHash::put >> contenido en pagina " + reference_page + " + hash " + key.hashCode() + ": ");
            for(int i=0; i<new_content.size(); i++) {
                System.out.println("  " + new_content.get(i));
            }
        }
        total_elements++;
        prom_acces += (mult - 1);
        this.fm.write(new_content, reference_page); this.out_counter++;


        // caso en que se cumple condicion de expansion.
        if(this.debug)
            System.out.println("LinealHash::put >> cantidad de elementos en lista: " + total_elements);
        if((total_elements >= 3*B/2 && this.type == 0) || ((1.0*prom_acces)/total_elements > 2 && this.type == 1)){
            if(this.debug)
                System.out.println("LinealHash::put >> cumple condicion de expancion");
            expand();
        }

    }

    // contrae estructura de hashing lineal (completo, falta testear).
    private void compress() {
        if(this.p == 0)
            return;

        if(this.p == (1 << this.t) - 1)
            this.t--;

        int last_index = this.p;

        if(this.debug) {
            System.out.println("LinealHash::compress >> valor p: " + this.p);
            System.out.println("LinealHash::compress >> valor t: " + this.t);
        }

        int reference_p = this.index_reference.get(last_index);
        ArrayList<Integer> last_cont = this.fm.read(reference_p), content = new ArrayList<>(); this.in_counter++;

        while(true) {
            this.total_active_block--;
            for(int i=1; i<=last_cont.get(0); i++) {
                content.add(last_cont.get(i));
            }

            if(last_cont.get(0) < B-2)
                break;

            last_index = last_cont.get(B - 1);
            last_cont = this.fm.read(last_index); this.in_counter++;
        }

        if(this.debug) {
            System.out.println("LinealHash::compress >> contenido a importar");
            for(int i=0; i<content.size(); i++) {
                System.out.println("  " + content.get(i));
            }
        }

        int push_index = this.p % (1 << t);
        /*
        if(this.p < (1 << t)) {
            push_index = this.p & (1 << t - 1);
        }
        */
        int reference_index = this.index_reference.get(push_index);

        ArrayList<Integer> push_cont = this.fm.read(reference_index); this.in_counter++;
        while(true) {
            if(push_cont.size() == B) {
                push_index = push_cont.get(B - 1);
                push_cont = this.fm.read(push_index); this.in_counter++;

            } else {
                ArrayList<Integer> new_content = new ArrayList<>();
                for(int i=1; i<=push_cont.get(0); i++) {
                    new_content.add(push_cont.get(i));
                }

                for(int i=0; i<content.size(); i++) {
                    new_content.add(content.get(i));
                    if(new_content.size() == B - 2 || i == content.size() - 1) {
                        this.total_active_block++;
                        int cant = new_content.size();
                        new_content.add(0, cant);

                        ArrayList<Integer> last = new ArrayList<>();

                        if(cant == B - 2) {
                            new_content.add(this.last);

                            this.fm.write(new_content, push_index); this.out_counter++;
                            last.add(0);
                            this.fm.write(last, this.last); this.out_counter++;

                            push_index = this.last;
                            this.last++;
                        } else {
                            this.fm.write(new_content, push_index); this.out_counter++;
                        }

                    }
                }
                break;
            }
        }
        ArrayList<Integer> prev_index = this.index_reference;
        for(int i=0; i<index_reference.size() - 1; i++) {
            prev_index.add(index_reference.get(i));
        }

        this.p--;
        if(this.p < (1 << this.t)) {
            this.t--;
        }
    }

    // metodo para eliminar un elemento de un diccionario (completo, con testear, falta terminar compress).
    public void delete(DNA key){
        int reference_page = this.getReference(key);
        ArrayList<Integer> content = this.fm.read(reference_page); this.in_counter++;

        if(this.debug)
            System.out.println("LinealHash::delete >> borrar elemento " + key.hashCode());

        int last_page = reference_page, last_chain = 0;
        ArrayList<Integer> last_content = content, search_content, new_content = new ArrayList<>();

        if(this.debug) {
            System.out.println("LinealHash::delete >> contenido en pagina " + reference_page);
            for(int i=0; i<last_content.size(); i++) {
                System.out.println("  " + last_content.get(i));
            }
        }

        // last_block: referencia al ultimo bloque.
        // search_block: referencia al bloque con el elemento buscado.
        int last_block = reference_page, search_block = -1, search_pos = -1;
        int total_elements = 0, prom_acces = 0, mult = 1;
        while(true) {
            if(this.debug)
                System.out.println("LinealHash::delete >> busqueda en pagina " + last_page);

            if(search_block == -1) {
                for (int i = 1; i <= last_content.get(0); i++) {
                    if (last_content.get(i) == key.hashCode()) {
                        this.total_in--;
                        if(this.debug)
                            System.out.println("LinealHash::delete >> elemento encontrado, pos: " + i);
                        search_pos = i;
                        search_block = last_page;
                        break;
                    }
                }
            }
            total_elements += last_content.get(0);
            prom_acces += (last_content.get(0) * mult);
            mult++;

            if(last_content.get(0) != 0) {
                last_block = last_page;
                last_chain = last_content.get(last_content.get(0));
            }

            if(last_content.get(0) != B - 2)
                break;

            last_page = last_content.get(B-1);
            last_content = this.fm.read(last_page); this.in_counter++;
        }

        if(this.debug) {
            System.out.println("LinealHash::delete >> resultados de busqueda:");
            System.out.println("  search_block = " + search_block);
            System.out.println("  last_block = " + last_block);
        }

        if(search_block != -1) {
            total_elements--;
            if(this.debug)
                System.out.println("LinealHash::delete >> elemento encontrado en pagina " + search_block);
            // se encontro el elemento buscado.
            // search_block: referencia al bloque que contiene la buscado.
            // last_block: referencia al ultimo bloque de la lista enlazada.

            search_content = this.fm.read(search_block); this.in_counter++;
            last_content = this.fm.read(last_block); this.in_counter++;

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
                new_content.add(search_content.get(B - 1));

                ArrayList<Integer> new_last_content = new ArrayList<>();
                new_last_content.add(last_content.get(0) - 1);
                for(int i=1; i<last_content.get(0); i++) {
                    new_last_content.add(last_content.get(i));

                }
                this.fm.write(new_last_content, last_block); this.out_counter++;

            }
            if(this.debug) {
                System.out.println("LinealHash::delete >> contenido actualizado en " + search_block);
                for(int i=0; i<new_content.size(); i++) {
                    System.out.println("  " + new_content.get(i));
                }
            }

            this.fm.write(new_content, search_block); this.out_counter++;
        }

        // caso en que se cumple condicion de compresion.
        if(this.debug)
            System.out.println("LinealHash::put >> cantidad de elementos en lista: " + total_elements);

        if((total_elements >= 3*B/2 && this.type == 0) || ((1.0*prom_acces)/total_elements <= 2 && this.type == 1) ){
            if(this.debug)
                System.out.println("LinealHash::put >> cumple condicion de compresion");

            compress();
        }
    }

    // metodo que determina si un elemento se encuentra dentro del diccionario (listo)
    public boolean containsKey(DNA key){
        if(this.debug)
            System.out.println("LinealHash::containsKey >> valor de hash buscado: " + key.hashCode());

        int reference_page = this.getReference(key);
        ArrayList<Integer> page_content = this.fm.read(reference_page); this.in_counter++;

        boolean res = false;
        int cant_elements = page_content.get(0);

        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.

        while(true) {
            if(this.debug) {
                System.out.println("LinealHash::containsKey >> buscando en pagina " + reference_page);
                for(int i=0; i<page_content.get(0); i++)
                    System.out.println(page_content.get(i));
            }

            for(int i=1; i<=cant_elements; i++) {
                if(page_content.get(i) == key.hashCode()) {
                    if(this.debug)
                        System.out.println("LinealHash::containsKey >> encontrado en " + reference_page);
                    res = true;
                }
            }

            if(cant_elements < B - 2)
                break;

            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page); this.in_counter++;
            cant_elements = page_content.get(0);
        }
        if(this.debug)
            System.out.println("LinealHash::containsKey >> se encontro la llave: " + res);

        return res;
    }

    public void resetIOCounter(){
        this.in_counter = 0; this.out_counter = 0;
    }

    public int getIOs(){return this.in_counter + this.out_counter;}

    public int getUsedSpace(){
        int total = (this.total_in + this.total_active_block) / this.total_active_block;
        return 0;
    }
}
