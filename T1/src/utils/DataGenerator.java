package utils;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class DataGenerator {

    private final int CHAIN_LENGTH = 15;
    private final char[] BASES = {'G', 'C', 'A', 'T'};

    public ArrayList<DNA> generateRandomChains(long N) {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        ArrayList<DNA> dna_chains = new ArrayList<>();
        ArrayList<Character> tmp_chain;
        for (int i = 0; i < N; i++) {
            tmp_chain = new ArrayList<>();
            for (int k = 0; k < CHAIN_LENGTH; k++) {
                int nextInd = Math.abs(rand.nextInt()) % BASES.length;
                tmp_chain.add(BASES[nextInd]);
            }
            try {
                dna_chains.add(new DNA(tmp_chain));
            } catch (InvalidAttributeValueException e) {
                System.err.println("Error: chain must be 15 bases long...");
                break;
            }
        }
        return dna_chains;
    }

}
