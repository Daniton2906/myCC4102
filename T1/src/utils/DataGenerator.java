package utils;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;

public class DataGenerator {

    static private final int CHAIN_LENGTH = 15;

    static public ArrayList<DNA> generateRandomChains(long N) {
        ArrayList<DNA> dna_chains = new ArrayList<>();
        ArrayList<Character> tmp_chain;
        for (int i = 0; i < N; i++) {
            tmp_chain = new ArrayList<>();
            for (int k = 0; k < CHAIN_LENGTH; k++) {
                tmp_chain.add('C'); // TODO: add random nitrogen base
            }
            try {
                dna_chains.add(new DNA(tmp_chain));
            } catch (InvalidAttributeValueException e) {
                break; // TODO: add message error
            }
        }
        return dna_chains;
    }



}
