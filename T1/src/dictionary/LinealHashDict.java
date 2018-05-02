package dictionary;

import utils.DNA;
import utils.FileManager;
import utils.Tuple2;

import java.io.File;
import java.util.ArrayList;

public class LinealHashDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B, t, p;
    private final FileManager fm;

    // Idea: utilizar dos bloques para guardar referencias a bloques, uno mantiene referencia a bloques de memoria
    //       que estan actualmente siendo utilizados, y el segundo mantiene referencia a bloques que han sido
    //       utilizados pero actualmente estan vacios.

    public LinealHashDict(String filename, int B) {
        this.fm = new FileManager(B, new File(filename));
        this.B = B;
        this.t = 0;
        this.p = 1;

        ArrayList<Integer> id = new ArrayList<Integer>();
        id.add(0);
        this.fm.write(id, 1);

        id = new ArrayList<Integer>();
        id.add(1); id.add(2);
        this.fm.write(id, 1);
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
            ref_expand = this.p + 2;
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
        int page = 1 + key.hashCode() % p;

        // obtener bloque con referencias
        ArrayList<Integer> reference = this.fm.read(0);

        /*
        int cant_active_block = reference.get(0);
        // si la pagina que se busca sale del arreglo de referencias que se obtuvieron,
        // empezar a buscar en la siguiente pagina de referencias.
        while(cant_active_block < page) {
            page -= cant_active_block;
            reference = this.fm.read(reference.get(cant_active_block + 2));
            cant_active_block = reference.get(0);
        }
        */
        int reference_page = reference.get(page);

        ArrayList<Integer> content = this.fm.read(reference_page);

        /*
        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.
        long long cant_elements = content.get(0).hashcode();
        while(cant_elements == B - 2) {
            int next_block = content.get(B-1);
            content = this.fm.read_block(next_block);
            cant_elements = content.size();
        }
         */

        content.add(key.hashCode());
        this.fm.write(content, reference_page);

        /*
        // caso en que se cumple condicion de expansion.
        if(CONDICION){
            expand()
        }
         */

    }

    public void delete(DNA key){
        // primer numero en el bloque 0 es la cantidad de referencias.
        int page = 1 + key.hashCode() % p;

        // obtener bloque con referencias
        ArrayList<Integer> reference = this.fm.read(0);

        /*
        int cant_active_block = reference.get(0);
        // si la pagina que se busca sale del arreglo de referencias que se obtuvieron,
        // empezar a buscar en la siguiente pagina de referencias.
        while(cant_active_block < page) {
            page -= cant_active_block;
            reference = this.fm.read(reference.get(cant_active_block + 2));
            cant_active_block = reference.get(0);
        }
        */
        int reference_page = reference.get(page);

        ArrayList<Integer> content = this.fm.read(reference_page);

        /*
        // TODO: while de busqueda y borrado, junto con compresion de lista enlazada.
        }
         */
        this.fm.write(content, reference_page);

        /*
        // caso en que se cumple condicion de compresion.
        if(CONDICION){
            compress();
        }
        */
    }

    public boolean containsKey(DNA key){
        // primer numero en el bloque 0 es la cantidad de referencias.
        int page = 1 + key.hashCode() % p;

        // obtener bloque con referencias
        ArrayList<Integer> reference = this.fm.read(0);

        /*
        int cant_active_block = reference.get(0);
        // si la pagina que se busca sale del arreglo de referencias que se obtuvieron,
        // empezar a buscar en la siguiente pagina de referencias.
        while(cant_active_block < page) {
            page -= cant_active_block;
            reference = this.fm.read(reference.get(cant_active_block + 2));
            cant_active_block = reference.get(0);
        }
        */
        int reference_page = reference.get(page);

        ArrayList<Integer> content = this.fm.read(reference_page);
        boolean res = false;

        /*
        // colocar caso en que la pagina ya esta llena, por lo que es necesario buscar en el siguiente bloque.
        // un bloque contiene los elementos, un numero (cantidad de elementos) y una referencia.
        long long cant_elements = content.get(0).hashcode();
        while(true) {
            for(int i=1; i<content.size() - 1; i++) {
                if(content.get(i).hashcode() == key.hashcode()) {
                    res = true;
                }
            }

            if(cant_elements != B - 2 || res) {
                break;
            }

            int next_block = content.get(B-1);
            content = this.fm.read_block(next_block);
            cant_elements = content.size();
        }
         */

        return res;
    }
}
