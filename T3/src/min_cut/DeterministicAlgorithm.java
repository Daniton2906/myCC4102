package min_cut;

import utils.Graph;
import utils.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

// Usamos algoritmo de Ford Fulkerson
public class DeterministicAlgorithm implements MinCutApp {

    Graph G, resG;
    int maxFlow;
    ArrayList<Pair> minCut;

    public DeterministicAlgorithm(Graph _G) {
        G = _G;
        resG = new Graph(_G);
    }

    private ArrayList<Pair> dfs_aumentante(int s) {
        ArrayList<Pair> parent = new ArrayList<>();

        return parent;
    }

    public void maxFlow(int s, int t) {
        return ;
    }

    public void minCut() {
        return ;
    }

    public static void main(String[] args) {
        Graph G3 = new Graph(8);
        G3.randomGraph(0.2);
        System.out.println(G3.toString());

        DeterministicAlgorithm dt = new DeterministicAlgorithm(G3);
        ArrayList<Pair> parents = dt.dfs_aumentante(0);
    }



}
