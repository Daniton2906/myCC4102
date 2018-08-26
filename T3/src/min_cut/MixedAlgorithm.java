package min_cut;

import utils.Graph;
import utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MixedAlgorithm {

    class PairSort implements Comparator<Pair>
    {
        int reg[];

        public PairSort(int n) {
            reg = new int[n];
            for(int i=0; i<n; i++)
                reg[i] = i;

        }

        public PairSort(int[] asignacion) {
            reg = asignacion;
        }
        // Used for sorting in ascending order of
        // roll number
        public int compare(Pair a, Pair b) {
            int u1 = a.getFirst(), v1 = a.getSecond();
            int u2 = b.getFirst(), v2 = b.getSecond();

            int ru1 = Math.min(reg[u1], reg[v1]), rv1 = Math.max(reg[u1], reg[v1]);
            int ru2 = Math.min(reg[u2], reg[v2]), rv2 = Math.max(reg[u2], reg[v2]);

            int r = ru1 - ru2;
            if(r == 0)
                r = rv1 - rv2;

            return r;
        }
    }

    DeterministicAlgorithm dt;
    KargerAlgorithm kg;
    PairSort comp;
    Graph G, resG;
    public ArrayList<Pair> bestMinCut = new ArrayList<>();

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

        comp = new PairSort(new_asign);
        Collections.sort(kgmincut, comp);

        /*
        System.out.println("mincut edge");
        for(Pair p : kgmincut) {
            System.out.println(p.getFirst() + " " + p.getSecond() + " . " + new_asign[p.getFirst()] + " " + new_asign[p.getSecond()]);
        }
        */

        Graph auxG = new Graph(num_vertex_left);
        for(Pair p : kgmincut) {
            int u = p.getFirst(), v = p.getSecond();
            auxG.addEdge(new_asign[u], new_asign[v]);

        }
        // System.out.println("\nDescripcion grafo comprimido");
        // System.out.println(auxG.toString());

        dt = new DeterministicAlgorithm(auxG);
        dt.minCut();
        ArrayList<Pair> dtmincut = dt.bestMinCut;

        comp = new PairSort(cont);
        Collections.sort(dtmincut, comp);

        int j = 0;
        for(int i=0; i<kgmincut.size(); i++) {
            if(j >= dtmincut.size())
                break;

            int u1 = kgmincut.get(i).getFirst(), v1 = kgmincut.get(i).getSecond();
            int u2 = dtmincut.get(j).getFirst(), v2 = dtmincut.get(j).getSecond();

            int ru1 = Math.min(new_asign[u1], new_asign[v1]), rv1 = Math.max(new_asign[u1], new_asign[v1]);
            int ru2 = Math.min(u2, v2), rv2 = Math.max(u2, v2);

            if(ru1 == ru2 && rv1 == rv2) {
                bestMinCut.add(kgmincut.get(i));
                j++;
            }
        }
    }

    public void kMinCut(int k, int t) {
        int s = -1;
        ArrayList<Pair> lastBest = new ArrayList<>();

        for(int i=0; i<k; i++) {
            System.out.println("\nITERACION " + (i+1) + "\n###############");
            minCut(t);

            if(bestMinCut.size() < s || s == -1) {
                s = bestMinCut.size();
                lastBest = new ArrayList<>(bestMinCut);

            }
            bestMinCut.clear();

        }
        bestMinCut = new ArrayList<>(lastBest);
    }

    public static void main(String args[]) {
        Graph G = new Graph(11);
        G.randomConnectedGraph(0.5);

        System.out.println("Descripcion del grafo");
        System.out.println(G.toString());

        MixedAlgorithm mx = new MixedAlgorithm(G);
        mx.kMinCut(6, 5);

        System.out.println("mx best min-cut");
        for(Pair p : mx.bestMinCut) {
            System.out.println(p.getFirst() + " " + p.getSecond());
        }


        G = new Graph(8);
        G.addEdge(0, 1);
        G.addEdge(0, 5);
        G.addEdge(0, 6);

        G.addEdge(1, 2);
        G.addEdge(1, 5);
        G.addEdge(1, 6);

        G.addEdge(2, 3);
        G.addEdge(2, 4);
        G.addEdge(2, 7);

        G.addEdge(3, 4);
        G.addEdge(3, 7);

        G.addEdge(4, 7);

        G.addEdge(5, 6);

        G.addEdge(6, 7);

        System.out.println("Descripcion del grafo");
        System.out.println(G.toString());

        mx = new MixedAlgorithm(G);
        mx.kMinCut(6, 5);

        System.out.println("mx best min-cut");
        for(Pair p : mx.bestMinCut) {
            System.out.println(p.getFirst() + " " + p.getSecond());
        }

    }

}
