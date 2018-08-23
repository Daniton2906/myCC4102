package min_cut;

import utils.Graph;
import utils.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

// Usamos algoritmo de Ford Fulkerson
public class KargerAlgorithm implements MinCutApp {

    Graph G, resG;
    int V, E;
    int asignacion[], renombre[];
    ArrayList<Pair> bestMinCut = new ArrayList<>();

    public KargerAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);
        V = G.getV(); E = G.getE();

        asignacion = new int[G.getV()];
        renombre = new int[G.getV()];
        for(int i=0; i<G.getV(); i++) asignacion[i] = i;
    }

    private void collapseEdge() {




    }

    public void minCut(int num_colapse) {
        num_colapse = Math.min(num_colapse, V - 2);
        for(int i=0; i<num_colapse; i++)
            collapseEdge();



    }

    public static void main(String[] args) {

    }
}
