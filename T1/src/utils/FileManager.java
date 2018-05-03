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

    public FileManager(int B, File fd){
        this.B = B + 1;
        this.fd = fd;
        try {
            fd.getParentFile().mkdirs();
            fd.createNewFile();
        }
        catch(IOException e){
            System.err.println("Error desconocidooooooo");
            this.fd = null;
        }
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
            fileIn.skip(block_offset * this.B * 4);
            // Get Block size
            byte[] bs = new byte[4];
            if((nRead = fileIn.read(bs)) == -1){
                return null;
            }
            //System.out.println("Primer numero: " + nRead);
            int seq = ByteBuffer.wrap(bs).getInt(), block_size;
            //System.out.println("seq:" + seq);
            if (lastBit) {
                block_size = 4 * (seq & 0x7fffffff);
                int_array.add(seq);
            } else block_size = 4 * seq;
            // Get DNA values
            //System.out.println("Block size:" + block_size);
            byte[] blocks = new byte[block_size * 4];
            if((nRead = fileIn.read(blocks)) == -1) {
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
        if(block_size > B - 1 || block_offset > this.counter) // no se cabe en bloque
            return false;
        try {
            FileInputStream fileIn = new FileInputStream(this.fd);
            byte[] bsPrev = new byte[block_offset * this.B * 4];
            byte[] bsNext = new byte[Math.max(this.counter - (block_offset + 1) * B  * 4, 0)];
            fileIn.read(bsPrev);
            fileIn.skip(B * 4);
            fileIn.read(bsNext);
            fileIn.close();

            FileOutputStream fileOut = new FileOutputStream(this.fd);
            ByteBuffer dbuf = ByteBuffer.allocate(this.B * 4);
            int mask = lastBit? 0x80000000: 0;
            //System.out.println("Block size: " + block_size);
            //System.out.println("seq: " + (block_size | mask));
            dbuf.putInt(block_size | mask);
            //, block_offset * B  * 4, 4
            // Write all block
            for (int i = 0; i < block_size; i++) {
                dbuf.putInt(int_array.get(i));
            }
            // Re-escribir informacion antes y despues
            fileOut.write(bsPrev);
            // Escribir bloque actualizado
            fileOut.write(dbuf.array());
            fileOut.write(bsNext);
            fileOut.close();
            System.out.println("Serialized data[" + block_size + "/" + (this.B-1) + "] is saved in " + fd.getName());
        } catch (IOException i) {
            // i.printStackTrace();
            return false;
        }
        if(block_offset == this.counter)
            counter++;
        return true;
    }

    public int append(ArrayList<Integer> int_array) {
        return append(int_array, false);
    }

    public int append(ArrayList<Integer> int_array, boolean lastBit) {
        boolean bool = write(int_array, counter, lastBit);
        if (!bool)
            return -1;
        return counter-1;
    }

}
