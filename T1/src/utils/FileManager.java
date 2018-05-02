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
        return read(block_offset, false);
    }

    public ArrayList<Integer> read(int block_offset, boolean lastBit) {
        ArrayList<Integer> int_array = new ArrayList<>();
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            int nRead = 0;

            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs, block_offset * B * 4, 4)) == -1){
                return null;
            }
            int seq = ByteBuffer.wrap(bs).getInt(), block_size;
            if (lastBit) {
                block_size = 4 * (seq & 0x7fffffff);
                int_array.add(seq);
            } else block_size = 4 * seq;
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
        return write(int_array, block_offset, false);
    }

    public boolean write(ArrayList<Integer> int_array, int block_offset, boolean lastBit){
        int block_size = int_array.size();
        try {
            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(32);
            int mask = lastBit? 0x80000000: 0;
            dbuf.putInt(0, block_size | mask);
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
        return append(int_array, false);
    }

    public boolean append(ArrayList<Integer> int_array, boolean lastBit) {
        boolean bool = write(int_array, counter, lastBit);
        if (bool)
            counter++;
        return bool;
    }

    public void test(ArrayList<DNA> dna_array, int to) {

        ArrayList<DNA> L = new ArrayList<>(dna_array.subList(0, to));
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
