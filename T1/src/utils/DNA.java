package utils;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;

public class DNA {

    private ArrayList<Character> myChain = new ArrayList<>();
    private char[] allowedBases = {'G', 'C', 'A', 'T'};

    DNA(ArrayList<Character> chain) throws InvalidAttributeValueException {
        if (chain.size() != 15)
            throw new InvalidAttributeValueException();
        myChain.addAll(chain);
    }

    public int getHashCode(){
        return 0;
    }

    public ArrayList<Character> getChain() {
        return myChain;
    }
}
