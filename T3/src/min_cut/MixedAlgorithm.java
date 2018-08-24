package min_cut;

import utils.Graph;
import utils.Pair;

import java.util.ArrayList;

public class MixedAlgorithm implements MinCutApp {
    DeterministicAlgorithm dt;
    KargerAlgorithm kg;

    Graph G, resG;

    public MixedAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);

    }

    public void minCut(int num_vertex_left) {
        kg = new KargerAlgorithm(G);

        num_vertex_left = Math.max(Math.min(num_vertex_left, G.getV()), 2);
        kg.minCut(num_vertex_left);

        ArrayList<Pair> edge = kg.edgeList, kgmincut = kg.bestMinCut;
        int asignacion[] = kg.getSet();

        System.out.println("total edge");
        for(Pair p : edge) {
            System.out.println(p.getFirst() + " " + p.getSecond());
        }
        System.out.println("mincut edge");
        for(Pair p : kgmincut) {
            System.out.println(p.getFirst() + " " + p.getSecond());
        }


        int new_asign[] = new int[G.getV()];
        for(int i=0; i<G.getV(); i++)
            new_asign[i] = -1;

        int cont = 0;
        for(int i=0; i<G.getV(); i++) {
            int x = asignacion[i];
            if (new_asign[x] == -1) {
                new_asign[x] = cont;
                cont++;
            }

            new_asign[i] = new_asign[x];
        }

        for(int i : new_asign)
            System.out.println(i);

        Graph auxG = new Graph(num_vertex_left);
        for(Pair p : kgmincut) {
            int u = p.getFirst(), v = p.getSecond();
            auxG.addEdge(new_asign[u], new_asign[v]);

        }
        System.out.println(auxG.toString());

        dt = new DeterministicAlgorithm(auxG);
        dt.minCut();

        System.out.println("dt mincut");
        for(Pair p : dt.bestMinCut) {
            System.out.println(p.getFirst() + " " + p.getSecond());
        }
    }

    public static void main(String args[]) {
        Graph G = new Graph(7);
        G.randomConnectedGraph(0.6);

        System.out.println(G.toString());

        MixedAlgorithm mx = new MixedAlgorithm(G);
        mx.minCut(4);

    }

}
