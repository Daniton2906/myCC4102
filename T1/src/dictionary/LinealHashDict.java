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

        // referencias en bloque de desuso.
        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 1);

        // referencias en bloque de uso.
        id = new ArrayList<Integer>();
        id.add(1); id.add(2);
        this.fm.write(id, 0);
    }

    private void expand() {
        // determinar si existen bloques en desuso.
        ArrayList<Integer> reference = this.fm.read(1);

        int ref_expand;
        if(reference.get(0) != 0) {
            ref_expand = reference.get(reference.size());
            // remover ultima referencia.
            // actualizar cantidad de bloques en desuso.
            // reescribir bloque de referencias.
        } else {
            ref_expand = this.last;
            this.last++;
        }

        // ref_expand tiene indice de bloque vacio.

        ArrayList<Integer> content = new ArrayList<Integer>();

        // colocar en content los valores que deberian estar segun la formula de hashing % (2 << (t+1))
        // sobreescribir content en el espacio p + 1
        // agregar referencia al bloque de referencia activas.

        p++;
        if(p == (2 << (t+1))) {
            t++;
        }
    }

    private void compress() {}

    public void put(DNA key, long value) {
        // primer numero en el bloque 0 es la cantidad de referencias.
        int page = 1 + key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = 1 + key.hashCode() % (1 << t);

        // obtener bloque con referencias
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

        int reference_page = reference_block.get(page);
        ArrayList<Integer> page_content = this.fm.read(reference_page);

        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.
        int cant_elements = page_content.get(0);
        while(cant_elements == B - 2) {
            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page);
            cant_elements = page_content.get(0);
        }

        // adicion de la nueva cadena.
        page_content.add(key.hashCode());
        ArrayList<Integer> new_content = new ArrayList<Integer>();
        new_content.add(page_content.get(0) + 1);

        for(int i=1; i<page_content.size(); i++)
            new_content.add(page_content.get(i));

        if(new_content.get(0) == B - 2) {
            ArrayList<Integer> last = new ArrayList<Integer>();
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

    public void delete(DNA key){
        // primer numero en el bloque 0 es la cantidad de referencias.
        int page = 1 + key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = 1 + key.hashCode() % (1 << t);

        // obtener bloque con referencias
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

        // obtencion referencia a pagina y pagina objetivo.
        int reference_page = reference_block.get(page);
        ArrayList<Integer> page_content = this.fm.read(reference_page);

        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.
        int cant_elements = page_content.get(0);
        boolean res = false;
        while(true) {
            for(int i=1; i<=cant_elements; i++) {
                if(key.hashCode() == page_content.get(i)) {
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

        // si res == true, se encontro una cadena dada key.
        // page_content tiene la cadena que se estaba buscando.
        // reference_page es la referencia al bloque donde esta la cadena buscada.
        if(res) {
            // se busca el ultimo bloque no vacio de la lista de bloques.
            int last_reference_page = reference_page;
            int change_chain = 0;
            ArrayList<Integer> last_page_content = this.fm.read(last_reference_page);

            while(true) {
                int cant_last_page = last_page_content.get(0);
                ArrayList<Integer> change_page = new ArrayList<Integer>();
                if (0 < cant_last_page && cant_last_page < B - 2) {
                    // cambio por ultima cadena
                    break;
                }

                int next_reference = last_page_content.get(B-1);
                ArrayList<Integer> next_page_content = this.fm.read(next_reference);

                if(next_page_content.get(0) == 0) {
                    // cambio por ultima cadena
                    break;
                }

                last_reference_page = next_reference;
                last_page_content = next_page_content;

            }

            if(last_page_content.get(0) == B - 2) {
                // dejar la siguiente pagina como pagina en desuso.
                // agregar last_reference_page a bloque de referencia de paginas en desuso.
                // con un for copiar last_page_content.
            }

        }

        /*
        // caso en que se cumple condicion de compresion.
        if(CONDICION){
            compress()
        }
        */
    }

    public boolean containsKey(DNA key){
        // primer numero en el bloque 0 es la cantidad de referencias.
        int page = 1 + key.hashCode() % (1 << (t+1));
        if(this.p < page)
            page = 1 + key.hashCode() % (1 << t);

        // obtener bloque con referencias
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

        // obtencion de referencia y contenido de la pagina buscada.
        int reference_page = reference_block.get(page);
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

            if(res)
                break;

            if(cant_elements < B - 2)
                break;

            reference_page = page_content.get(B-1);
            page_content = this.fm.read(reference_page);
            cant_elements = page_content.get(0);
        }

        return res;
    }
}
