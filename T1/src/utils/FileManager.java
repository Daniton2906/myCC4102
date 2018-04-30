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

    public FileManager(int B, File fd) {
        this.B = B;
        this.fd = fd;
    }

    public int logB(double x) {
        double num = Math.log(x);
        double den = Math.log(this.B);
        return (int) (num/den);
    }

    public ArrayList<DNA> read(int block_offset) {
        ArrayList<DNA> dna_array = new ArrayList<>();
        DNA dna = null;
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            int nRead = 0;

            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs, block_offset*(B + 1) * 4, 4)) == -1){
                return null;
            }
            int block_size = 4 * ByteBuffer.wrap(bs).getInt();

            // Get DNA values
            byte[] blocks = new byte[block_size];
            System.out.println(block_size);
            if((nRead = fileIn.read(blocks, block_offset*(B + 1) * 4, block_size)) == -1) {
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

    public boolean write(ArrayList<DNA> dna_array, int block_offset){
        int block_size = dna_array.size();
        try {
            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(4 * (B + 1));
            dbuf.putInt(block_size);
            // Write all block
            for (int i = 0; i < block_size; i++) {
                dbuf.putInt(dna_array.get(i).hashCode());
            }
            fileOut.write(dbuf.array(), block_offset * (B + 1) * 4, 4 * (B + 1));
            fileOut.close();
            System.out.println("Serialized data is saved in " + fd.getName());
        } catch (IOException i) {
            // i.printStackTrace();
            return false;
        }
        return true;
    }

    public void test(ArrayList<DNA> dna_array) {

        ArrayList<DNA> L = new ArrayList<>(dna_array.subList(0, 5));
        for(DNA dna: L)
            System.out.println(dna);
        this.write(L, 0);

        ArrayList<DNA> L2 = this.read(0);
        for(DNA dna: L2)
            System.out.println(dna);
    }

}
