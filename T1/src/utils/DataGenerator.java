package utils;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.Random;

public class DataGenerator {

    private final int CHAIN_LENGTH = 15;
    private final char[] BASES = {'G', 'C', 'A', 'T'};
    private final Random rand;

    public DataGenerator(long seed) {
        this.rand = new Random(seed);
    }

    private DNA randomChain(Random rand) throws InvalidAttributeValueException{
        ArrayList<Character> tmp_chain = new ArrayList<>();
        for (int k = 0; k < CHAIN_LENGTH; k++) {
            int nextInd = Math.abs(rand.nextInt()) % BASES.length;
            tmp_chain.add(BASES[nextInd]);
        }
        return new DNA(tmp_chain);
    }

    public ArrayList<DNA> generateRandomChains(long N) {

        ArrayList<DNA> dna_chains = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            try {
                dna_chains.add(randomChain(rand));
            } catch (InvalidAttributeValueException e) {
                System.err.println("Error: chain must be 15 bases long...");
                break;
            }
        }
        return dna_chains;
    }

    public ArrayList<DNA> getRandomChunk(ArrayList<DNA> chains, int size) {

        ArrayList<DNA> sub_chains = new ArrayList<>();
        int n = 0;
        while(n++ < size)
            sub_chains.add(chains.get(Math.abs(rand.nextInt()) % chains.size()));

        // System.out.println("Size: " + sub_chains.size());
        return sub_chains;
    }

    public ArrayList<DNA> getOtherChunk(ArrayList<DNA> chains, int size) {

        ArrayList<DNA> sub_chains = new ArrayList<>();
        int n = 0;
        while(n++ < size) {
            try {
                DNA next = randomChain(rand);
                if (!chains.contains(next))
                    sub_chains.add(next);
                else n--;
            } catch (InvalidAttributeValueException e) {
                System.err.println("Error: chain must be 15 bases long...");
                return null;
            }
        }
        // System.out.println("Size: " + sub_chains.size());
        return sub_chains;
    }

}
