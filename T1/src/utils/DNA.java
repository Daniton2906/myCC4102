package utils;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;

public class DNA {

    private ArrayList<Character> myChain = new ArrayList<>();
    private int CHAIN_LENGTH = 15;
    private int hashcode;

    public DNA(ArrayList<Character> chain) throws InvalidAttributeValueException {
        super();
        if (chain.size() != CHAIN_LENGTH)
            throw new InvalidAttributeValueException();
        myChain.addAll(chain);
        // Calculate hashcode
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
        codeNum >>= 2;
        this.hashcode = codeNum;
    }

    public DNA(int codeNum) {
        super();
        this.hashcode = codeNum;
        // codeNum >>= 2;
        myChain = new ArrayList<>();
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

    @Override
    public int hashCode(){
        return this.hashcode;
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

    public int compareTo(DNA dna) {
        return Integer.compare(this.hashCode(), dna.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashcode == obj.hashCode();
    }
}
