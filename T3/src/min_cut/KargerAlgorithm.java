package min_cut;

import utils.Graph;
import utils.Pair;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class KargerAlgorithm {

    private class UnionFind {
        int r[], p[];

        public UnionFind(int n) {
            r = new int[n+1];
            p = new int[n+1];
            for(int i=0; i<n+1; i++) {
                r[i] = i;
                p[i] = i;
            }
        }

        int findSet(int x) {
            return (p[x] == x) ? x : (p[x] = findSet(p[x]));
        }

        boolean sameSet(int x, int y) {
            return (findSet(x) == findSet(y));
        }

        void unionSet(int x, int y) {
            if(sameSet(x, y)) return;

            x = findSet(x); y = findSet(y);
            if(r[x] > r[y]) {
                p[x] = y;
                r[y]++;
            } else {
                p[y] = x;
                r[x]++;
            }
        }

        public int[] getSet() {
            return p;
        }
    }

    Graph G, resG;
    UnionFind uf;
    int asignacion[];
    ArrayList<Pair> bestMinCut = new ArrayList<>(), edgeList = new ArrayList<>();
    int cant_edge;

    public KargerAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);
        uf = new UnionFind(G.getV());
        asignacion = new int[G.getV()];

        for(int i=0; i<G.getV(); i++) {
            for(int x : G.getNeighboorAdjL(i)) {
                if(i > x)
                    continue;

                edgeList.add(new Pair(i, x));
            }
        }
        cant_edge = edgeList.size();
    }



    private void collapseEdge() {
        while(true) {
            int r = ThreadLocalRandom.current().nextInt(0, cant_edge);
            Pair p = edgeList.get(r);

            int u = p.getFirst(), v = p.getSecond();
            //System.out.println("par: " + u + " " + v);

            //System.out.println("asignacion u: " + uf.findSet(u));
            //System.out.println("asignacion v: " + uf.findSet(v));

            if(uf.findSet(u) == uf.findSet(v)) {
                //System.out.println("misma asignacion, repitiendo...");

                Pair aux = edgeList.get(r);
                edgeList.set(r, edgeList.get(cant_edge - 1));
                edgeList.set(cant_edge - 1, aux);

                cant_edge--;
                continue;
            }
            Pair aux = edgeList.get(r);
            edgeList.set(r, edgeList.get(cant_edge - 1));
            edgeList.set(cant_edge - 1, aux);

            cant_edge--;
            //System.out.println("distinta asignacion, colapsando vertice...");

            uf.unionSet(u, v);
            break;

        }
    }

    public void minCut(int num_vertex) {
        int n = G.getV();
        num_vertex = Math.max(Math.min(num_vertex, n), 2);

        while(n > num_vertex) {
            //System.out.println("vertices actuales: " + n);
            collapseEdge();
            n--;
        }

        /*
        System.out.println("asignacion final:");
        for(int i=0; i<G.getV(); i++) {
            asignacion[i] = uf.findSet(i);
            System.out.println(i + ": " + uf.findSet(i));
        }
        */

        for(Pair p : edgeList) {
            int u = p.getFirst(), v = p.getSecond();
            if(uf.findSet(u) != uf.findSet(v)) {
                Pair P = new Pair(Math.min(u, v), Math.max(u, v));

                /*
                int ver = 0;
                for(Pair q : bestMinCut) {
                    if(q.getFirst() == P.getFirst() && q.getSecond() == P.getSecond())
                        ver = 1;

                }

                if(ver == 1)
                    continue;
                */

                bestMinCut.add(P);
            }
        }
    }

    int[] getSet() {
        return uf.getSet();
    }

    void reset() {
        G = new Graph(resG);
        uf = new UnionFind(G.getV());
        asignacion = new int[G.getV()];
        cant_edge = edgeList.size();
        bestMinCut.clear();
    }

    public void kMinCut(int k) {
        int s = -1;
        ArrayList<Pair> lastBest = new ArrayList<>();
        for(int i=0; i<k; i++) {
            System.out.println("\nITERACION " + (i+1) + "\n###############");
            minCut(2);
            if(bestMinCut.size() < s || s == -1) {
                s = bestMinCut.size();
                lastBest = new ArrayList<>(bestMinCut);
            }
            reset();
        }
        bestMinCut = new ArrayList<>(lastBest);
    }

    public static void main(String[] args) {
        Graph G1 = new Graph(12);
        G1.randomConnectedGraph(1.0/12.0);
        System.out.println("grafo inicial:");
        System.out.print(G1.toString());

        KargerAlgorithm kg = new KargerAlgorithm(G1);
        kg.kMinCut(5);

        //System.out.println("grafo final:");
        //System.out.print(G1.toString());

        System.out.println("minCut: ");
        for(Pair p : kg.bestMinCut) {
            System.out.print("(" + p.getFirst() + "," + p.getSecond() + ") ");
        }
        System.out.print("\n");
    }
}
