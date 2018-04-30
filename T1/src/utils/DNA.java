package utils;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DNA implements Serializable {

    private static final long serialVersionUID = 7829136421241571165L;

    private ArrayList<Character> myChain = new ArrayList<>();
    private int CHAIN_LENGTH;

    DNA(ArrayList<Character> chain) throws InvalidAttributeValueException {
        this(chain, 15);
    }

    private DNA(ArrayList<Character> chain, int n) throws InvalidAttributeValueException {
        super();
        this.CHAIN_LENGTH = n;
        if (chain.size() != CHAIN_LENGTH)
            throw new InvalidAttributeValueException();
        myChain.addAll(chain);
    }

    @Override
    public int hashCode(){
        int codeNum = 0;
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            char b = myChain.get(i);
            switch (b){
                case 'C':
                    codeNum |= 0x1;
                    break;
                case 'A':
                    codeNum |= 0x2;
                    break;
                case 'T':
                    codeNum |= 0x3;
                    break;
                default: // 'G'
                    break;
            }
            codeNum <<= 2;
        }
        return codeNum;
    }

    public ArrayList<Character> getChain() {
        return myChain;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ADN { ");
        for (char b : myChain) {
            sb.append(b);
            sb.append(" ");
        }
        sb.append("}");
        return sb.toString();
    }

    //Setters and Getters
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        int codeNum = aInputStream.readInt() >> 2;
        myChain = new ArrayList<>();
        CHAIN_LENGTH = 15;
        for (int i = 0; i < CHAIN_LENGTH; i++) {
            switch (codeNum & 0x3){
                case 0x1:
                    myChain.add(0, 'C');
                    break;
                case 0x2:
                    myChain.add(0, 'A');
                    break;
                case 0x3:
                    myChain.add(0, 'T');
                    break;
                default: // 'G'
                    myChain.add(0, 'G');
                    break;
            }
            codeNum >>= 2;
        }
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeInt(this.hashCode());
    }
}
