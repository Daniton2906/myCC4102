package dictionary;

import utils.DNA;
import utils.FileManager;
import utils.Tuple2;

import java.io.File;
import java.util.ArrayList;

public class LinealHashDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B, t, p, last;
    private final FileManager fm;

    // Idea: utilizar dos bloques para guardar referencias a bloques, uno mantiene referencia a bloques de memoria
    //       que estan actualmente siendo utilizados, y el segundo mantiene referencia a bloques que han sido
    //       utilizados pero actualmente estan vacios.
    public LinealHashDict(String filename, int B) {
        // caso en que se usa un hashing lineal ya creado(?).


        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.t = 0;
        this.p = 1;
        this.last = 3;

        // referencias en bloque de uso.
        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(1); id.add(2);
        this.fm.write(id, 0);

        // referencias en bloque de desuso.
        id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 1);

        // preparacion de primer bloque activo.
        id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 2);
    }

    // inserta una referencia en un bloque, utilizado para agregar referencia
    // a pagina de bloques en uso y en desuso (listo).
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
    private int getReferenc(DNA key) {
        // page: referencia a la pagina que contiene key.
        int page = 1 + key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = 1 + key.hashCode() % (1 << t);

        ArrayList<Integer> reference_block = this.fm.read(0);

        // obtener cantidad de bloques activos.
        int cant_active_block = reference_block.get(0);

        // si la pagina que se busca sale del arreglo de referencias que se obtuvieron,
        // empezar a buscar en la siguiente pagina de referencias.
        while(cant_active_block < page) {
            page -= cant_active_block;
            reference_block = this.fm.read(reference_block.get(B-1));
            cant_active_block = reference_block.get(0);
        }

        return reference_block.get(page);

    }

    // expande estructura de hashing lineal (inclompleto).
    private void expand() {
        // determinar si existen bloques en desuso.
        ArrayList<Integer> reference = this.fm.read(1);

        int ref_expand = 0;
        int cant_reference = reference.get(0);
        if(cant_reference != 0) {
            while(true) {
                if(cant_reference == 0)
                    break;

                ref_expand = reference.get(cant_reference);
                reference = this.fm.read(reference.get(B-1));
                cant_reference = reference.get(0);
            }

        } else {
            ref_expand = this.last;
            this.last++;
        }
        this.addReference(0, ref_expand);

        // ref_expand tiene indice de bloque vacio.
        ArrayList<Integer> content1 = new ArrayList<>();
        ArrayList<Integer> content2 = new ArrayList<>();

        int p1 = (p + 1) % (1 << t), p2 = p + 1;

        int ref1 = this.getReferenc(new DNA(p1));
        int ref2 = ref_expand;

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

        int ind = 0;
        aux_cont.clear();
        ArrayList<Integer> cont_ref1 = this.fm.read(ref1);
        while(true) {
            if(aux_cont.size() == B - 2 || ind == content1.size()) {
                if(aux_cont.size() == 0)
                    break;

                aux_cont.add(0, aux_cont.size());
                if(aux_cont.get(0) == B - 2) {
                    aux_cont.add(cont_ref1.get(B-1));
                }
                this.fm.write(aux_cont, ref1);
                ref1 = cont_ref1.get(B-1);
                cont_ref1 = this.fm.read(ref1);

                continue;
            }
            aux_cont.add(content1.get(ind));
            ind++;
        }

        aux_cont.clear();
        ArrayList<Integer> cont_ref2 = this.fm.read(ref2);
        for(int i=0; i<content2.size(); i++) {

        }

        this.p++;
        if(this.p == (1 << (this.t+1))) {
            this.t++;
        }
    }

    // inserta un elemento en el diccionario (completo, falta terminar expand).
    public void put(DNA key, long value) {
        int reference_page = this.getReferenc(key);
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
    private void compress() { }

    // metodo para eliminar un elemento de un diccionario (posiblemente completo, falta terminar compress).
    public void delete(DNA key){
        // obtencion referencia a pagina y contenido de pagina objetivo.
        // reference_page: referencia a la pagina donde esta key.
        // page_content: bloque donde deberia esta la key buscada.
        int reference_page = this.getReferenc(key);
        ArrayList<Integer> page_content = this.fm.read(reference_page);

        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y (posiblemente) una referencia.
        int cant_elements = page_content.get(0);
        boolean res = false, same_block = true;
        int pos_chain_to_delete = 0;
        while(true) {
            for(int i=1; i<=cant_elements; i++) {
                if(key.hashCode() == page_content.get(i)) {
                    pos_chain_to_delete = i;
                    res = true;
                }
            }

            if(res)
                break;

            if(cant_elements < B - 2)
                break;

            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page);
            cant_elements = page_content.get(0);
        }

        // page_content: bloque/pagina en la lista enlazada, que contiene la cadena que se estaba buscando.
        // reference_page: referencia al bloque donde esta la cadena buscada.

        // si res == true, se encontro la cadena 'key' en algun bloque.
        if(res) {
            // se busca el ultimo bloque no vacio de la lista de bloques.
            int last_reference_page = reference_page;
            ArrayList<Integer> last_page_content = this.fm.read(last_reference_page);
            int last_chain;

            // last_chain es la cadena por la que se reemplazara la cadena borrada.
            while(true) {
                int cant_last_page = last_page_content.get(0);
                ArrayList<Integer> change_page = new ArrayList<Integer>();

                if (0 < cant_last_page && cant_last_page < B - 2) {
                    // cambio por ultima cadena
                    last_chain = last_page_content.get(cant_last_page);
                    change_page.add(cant_last_page - 1);
                    for(int i=1; i<cant_last_page; i++)
                        change_page.add(last_page_content.get(i));

                    this.fm.write(change_page, last_reference_page);

                    break;
                }

                int next_reference = last_page_content.get(B-1);
                ArrayList<Integer> next_page_content = this.fm.read(next_reference);

                if(next_page_content.get(0) == 0) {
                    // cambio por ultima cadena.
                    // en el caso de que el bloque tenga B-2 elementos, eliminar referencia a siguiente bloque en
                    // la lista enlazada.
                    // colocar referencia en bloques de referencias en desuso (usar funcion addReference).
                    last_chain = last_page_content.get(cant_last_page);
                    change_page.add(cant_last_page - 1);
                    for(int i=1; i<cant_last_page; i++)
                        change_page.add(last_page_content.get(i));

                    this.addReference(1, last_page_content.get(B-1));

                    this.fm.write(change_page, last_reference_page);

                    break;
                }
                // same_block: con esto se determina si el elemento eliminado esta en el ultimo bloque de la
                //             lista enlazada.
                same_block = false;

                last_reference_page = next_reference;
                last_page_content = next_page_content;

            }

            // last_chain: ultima cadena en la lista enlazada.
            // page_content: bloque/pagina que contiene la cadena que se estaba buscando.
            // reference_page: referencia al bloque donde esta la cadena buscada.
            // en caso de que la cadena a eliminar no es la ultima de la lista enlazada, hacer el cambio.
            ArrayList<Integer> new_content = new ArrayList<Integer>();
            if(!same_block && pos_chain_to_delete < last_page_content.get(0)) {
                for(int i=1; i<=cant_elements; i++) {
                    if(i == pos_chain_to_delete)
                        new_content.add(last_chain);
                    else
                        new_content.add(page_content.get(i));
                }

                this.fm.write(new_content, reference_page);
            }
            // en caso que no se cumpla if, ya se elimino el ultimo elemento dentro del while.

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
        int reference_page = this.getReferenc(key);
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
