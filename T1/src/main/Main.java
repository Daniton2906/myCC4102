package main;

import utils.DNA;
import utils.DataGenerator;

import java.io.*;
import java.util.ArrayList;

public class Main {

    static final private long CHAINS = (long) Math.pow(2, 20);
    static final private String btree_filename = "/tmp/btree_data.ser";

    public static void main (String [ ] args) {
        System.out.println("Empezamos la ejecución del programa");
        DataGenerator rand_data_gen = new DataGenerator();
        long start = System.currentTimeMillis();
        ArrayList<DNA> dna_array = rand_data_gen.generateRandomChains(CHAINS);
        long end = System.currentTimeMillis();
        System.out.println("Tiempo para crear " + CHAINS + " cadenas: " + (end - start));
        File fd = new File(btree_filename);
        try {
            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fd));
            for (int i = 0; i < 1; i++) {
                System.out.println(dna_array.get(i));
                out.writeObject(dna_array.get(i));
            }
            out.close();/*
            ObjectOutputStream os2 = new ObjectOutputStream(
                    new FileOutputStream(fd, true)) {
                        protected void writeStreamHeader() throws IOException {
                            reset();
                        }
            };
            for (int i = 5; i < 10; i++) {
                DNA mydna = dna_array.get(i);
                os2.writeObject(mydna);
                System.out.println(mydna);
            }
            os2.close();*/
            System.out.println("Serialized data is saved in " + btree_filename);
        } catch (IOException i) {
            i.printStackTrace();
        }

        DNA dna = null;
        try {
            FileInputStream fileIn = new FileInputStream(fd);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while(true) {
                try{
                    dna = (DNA) in.readObject();
                    System.out.println(dna);
                }
                catch(EOFException e) {
                    break;
                }
            }
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("DNA class not found");
            c.printStackTrace();
        }
    }
}
