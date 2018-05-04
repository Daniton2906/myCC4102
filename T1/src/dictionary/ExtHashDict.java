package dictionary;

import utils.DNA;
import utils.FileManager;

import java.io.File;
import java.util.ArrayList;

public class ExtHashDict implements Dictionary {

    // tamano bloque = 4KB o 512B
    private int B;
    private final FileManager fm;
    private ArrayList<Integer> references;

    public ExtHashDict(String filename, int B) {
        // caso en que se usa un hashing lineal ya creado(?).


        // caso en que se crea un hashing lineal.
        this.fm = new FileManager(B, new File(filename));
        this.B = B;

        this.references = new ArrayList<Integer>();

        // referencias en bloque de uso.
    }

    public void put(DNA key, long value) {
        int hash = key.hashCode();


    }

    public void delete(DNA key){}

    public boolean containsKey(DNA key){
        return false;
    }
}
