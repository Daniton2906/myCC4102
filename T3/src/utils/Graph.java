package utils;

import com.sun.xml.internal.bind.v2.TODO;

import java.util.ArrayList;

public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V, E;

    private class Pair {
        private int a, b;

        public Pair(int _a, int _b) {
            a = _a;
            b = _b;
        }

        public int getFirst() {
            return a;
        }

        public int getSecond() {
            return b;
        }

    }

    ArrayList<Pair>[] adj;
    public Graph(int n) {
        V = n;

        adj = new ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new ArrayList();
    }


    public Graph(Graph G) {
        V = G.getV();
        E = G.getE();

        adj = new ArrayList[V];
        for(int i=0; i<V; i++) {
            ArrayList<Pair> l_i = G.getNeighboor(i);

            adj[i] = new ArrayList<>();
            adj[i].addAll(l_i);
        }
    }

    public void randomGraph(double probability) {
        for(int i=0; i<V; i++) {
            adj[i].clear();
        }

        for(int i=0; i<V; i++) {
            for(int j=i+1; j<V; j++) {
                double p = Math.random();
                if(p < probability) {
                    adj[i].add(new Pair(1, j));
                }
            }
        }
    }

    public ArrayList<Pair> getNeighboor(int v) {
        return adj[v];
    }

    public int getV() {
        return V;
    }

    public int getE() {
        return E;
    }

    public void addEdge(int u, int v, int w) {
        E++;
        adj[u].add(new Pair(w, v));
        adj[v].add(new Pair(w, u));
    }

    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }

    public int degree(int v) {
        return adj[v].size();
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("nodes " + V + ", edges " + E + "\n");
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (Pair p : adj[v]) {
                s.append(p.getSecond() + "(" + p.getFirst() + ") ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        Graph G = new Graph(8);

        G.addEdge(0, 4);
        G.addEdge(1, 2);
        G.addEdge(1, 3);
        G.addEdge(2, 3);
        G.addEdge(2, 4);
        G.addEdge(3, 7);
        G.addEdge(4, 5);
        G.addEdge(4, 6);
        G.addEdge(5, 6);

        System.out.println(G.toString());
        Graph G2 = new Graph(G);

        System.out.println(G2.toString());

        Graph G3 = new Graph(5);
        G3.randomGraph(0.5);
    }
}