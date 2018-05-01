package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class FileManager {

    private int B;
    private File fd;
    private int counter = 0;

    public FileManager(int B, File fd) {
        this.B = B;
        this.fd = fd;
        this.counter = 0;
    }

    public int logB(double x) {
        double num = Math.log(x);
        double den = Math.log(this.B);
        return (int) (num/den);
    }

    public ArrayList<Integer> read(int block_offset) {
        ArrayList<Integer> int_array = new ArrayList<>();
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            int nRead = 0;

            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs, block_offset * B * 4, 4)) == -1){
                return null;
            }
            int block_size = 4 * ByteBuffer.wrap(bs).getInt();

            // Get DNA values
            byte[] blocks = new byte[block_size];
            if((nRead = fileIn.read(blocks, block_offset * B * 4, block_size)) == -1) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.wrap(blocks);
            for (int i = 0; i < block_size; i += 4) {
                int_array.add(bb.getInt(i));
            }

            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return int_array;
    }

    public boolean write(ArrayList<Integer> int_array, int block_offset){
        int block_size = int_array.size();
        try {
            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(32);
            dbuf.putInt(0, block_size);
            fileOut.write(dbuf.array(), block_offset * B  * 4, 4);
            // Write all block
            for (int i = 0; i < block_size; i++) {
                dbuf.putInt(0, int_array.get(i));
                fileOut.write(dbuf.array(), block_offset * B * 4, 4);
            }
            fileOut.close();
            System.out.println("Serialized data is saved in " + fd.getName());
        } catch (IOException i) {
            // i.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean append(ArrayList<Integer> int_array) {
        boolean bool = write(int_array, counter);
        if (bool)
            counter++;
        return bool;
    }

    public ArrayList<Tuple2<Integer, DNA>> read_node(int block_offset) {
        ArrayList<Tuple2<Integer, DNA>> tuple_array = new ArrayList<>();
        Tuple2<Integer, DNA> tuple = null;
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            int nRead = 0;

            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs, block_offset * B * 4, 4)) == -1){
                return null;
            }
            int block_size = 4 * ByteBuffer.wrap(bs).getInt();

            // Get DNA values
            byte[] blocks = new byte[block_size];
            System.out.println(block_size);
            if((nRead = fileIn.read(blocks, block_offset * B * 4, block_size)) == -1) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.wrap(blocks);
            for (int i = 0; i < block_size/2; i += 8) {
                int bo = bb.getInt(i);
                int codeNum = bb.getInt(i + 4);
                tuple = new Tuple2<>(bo, new DNA(codeNum));
                tuple_array.add(tuple);
            }

            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return tuple_array;
    }

    public boolean write_node(ArrayList<Tuple2<Integer, DNA>> tuple_array, int block_offset){
        int block_size = tuple_array.size() * 2;
        try {
            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(4 * B);
            dbuf.putInt(block_size);
            // Write all block
            for (int i = 0; i < block_size; i++) {
                dbuf.putInt(tuple_array.get(i).x);
                dbuf.putInt(tuple_array.get(i).y.hashCode());
            }
            fileOut.write(dbuf.array(), block_offset * B * 4, 4 * B);
            fileOut.close();
            System.out.println("Serialized data is saved in " + fd.getName());
        } catch (IOException i) {
            // i.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<DNA> read_block(int block_offset) {
        ArrayList<DNA> dna_array = new ArrayList<>();
        DNA dna = null;
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            int nRead = 0;

            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs, block_offset* B * 4, 4)) == -1){
                return null;
            }
            int block_size = 4 * ByteBuffer.wrap(bs).getInt();

            // Get DNA values
            byte[] blocks = new byte[block_size];
            System.out.println(block_size);
            if((nRead = fileIn.read(blocks, block_offset* B * 4, block_size)) == -1) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.wrap(blocks);
            for (int i = 0; i < block_size; i += 4) {
                int codeNum = bb.getInt(i);
                dna = new DNA(codeNum);
                dna_array.add(dna);
            }

            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return dna_array;
    }

    public boolean write_block(ArrayList<DNA> dna_array, int block_offset){
        int block_size = dna_array.size();
        try {
            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(4 * B);
            dbuf.putInt(block_size);
            // Write all block
            for (int i = 0; i < block_size; i++) {
                dbuf.putInt(dna_array.get(i).hashCode());
            }
            fileOut.write(dbuf.array(), block_offset * B * 4, 4 * B);
            fileOut.close();
            System.out.println("Serialized data is saved in " + fd.getName());
        } catch (IOException i) {
            // i.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean append_block(ArrayList<DNA> dna_array) {
        boolean bool = write_block(dna_array, counter);
        if (bool)
            counter++;
        return bool;
    }

    public int append_node(ArrayList<Tuple2<Integer, DNA>> tuple_array) {
        if (write_node(tuple_array, counter)){
            counter++;
            return counter - 1;
        } else
            return -1;
    }

    public void test(ArrayList<DNA> dna_array) {

        ArrayList<DNA> L = new ArrayList<>(dna_array.subList(0, (int) Math.pow(2, 20)));
        ArrayList<Integer> LI = new ArrayList<>();
        for(DNA dna: L){
            LI.add(dna.hashCode());
            System.out.println(dna.hashCode());
        }
        this.write(LI, 0);

        ArrayList<Integer> L2 = this.read(0);
        for(int dna: L2)
            System.out.println(dna);
    }

}
