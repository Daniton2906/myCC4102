package utils;

import java.util.ArrayList;
import java.util.Stack;

public class Graph {

    private int V, E;
    ArrayList<Integer>[] adjM, adjL;

    public Graph(int n) {
        V = n;
        E = 0;

        adjL = new ArrayList[n];
        adjM = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjL[i] = new ArrayList();
            adjM[i] = new ArrayList();
            for(int j=0; j<n; j++) {
                adjM[i].add(j, 0);
            }
        }
    }

    public Graph(Graph G) {
        V = G.getV();
        E = G.getE();

        adjL = new ArrayList[V];
        adjM = new ArrayList[V];
        for(int i=0; i<V; i++) {
            ArrayList<Integer> l_i = G.getNeighboorAdjL(i);

            adjL[i] = new ArrayList<>();
            adjM[i] = new ArrayList<>();

            adjL[i].addAll(l_i);
            adjM[i].addAll(G.adjM[i]);
        }
    }

    public ArrayList<Integer> getNeighboorAdjL(int u) {
        return adjL[u];
    }

    public ArrayList<Integer> getNeighboorAdjM(int u) {
        return adjM[u];
    }

    // por ahora, suponemos que partimos de un grafo vacio.
    // los pesos son unitarios.
    public void randomGraph(double prob) {
        for(int i=0; i<V; i++) {
            for(int j=i+1; j<V; j++) {
                double p = Math.random();

                if(p < prob) {
                    E++;
                    adjL[i].add(j);
                    adjL[j].add(i);

                    adjM[i].set(j, 1);
                    adjM[j].set(i, 1);

                }
            }
        }
    }

    public void randomConnectedGraph(double prob) {
        while(true) {
            randomGraph(prob);
            if(isConnected())
                break;
        }
    }

    public boolean isConnected() {
        boolean res = true;
        int parent[] = dfs(0);
        for(int a : parent) {
            if(a == -1)
                res = false;

        }

        return res;
    }

    public int[] dfs(int s) {
        int parent[] = new int[V];
        for(int i=0; i<V; i++) {
            parent[i] = -1;
        }
        parent[s] = 0;
        Stack<Integer> st = new Stack<>();
        st.push(s);
        while(!st.empty()) {
            int u = st.peek(); st.pop();

            for(int v : adjL[u]) {
                if(parent[v] == -1) {
                    parent[v] = u;
                    st.push(v);
                }
            }
        }

        return parent;
    }

    public void addEdge(int u, int v, int w) {
        E++;
        adjL[u].add(v);
        adjL[v].add(u);

        adjM[u].set(v, w);
        adjM[v].set(u, w);
    }

    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }

    public int getWeight(int u, int v) {
        return adjM[u].get(v);
    }

    public void setWeight(int u, int v, int w) {
        adjM[u].set(v, w);
    }

    public int getV() {
        return V;
    }

    public int getE() {
        return E;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + "\n");
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for(int u : adjL[v]) {
                s.append(u + "(" + adjM[v].get(u) + ") ");
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

        Graph G3 = new Graph(8);
        G3.randomGraph(0.1);
        System.out.println(G3.toString());

        int[] p3 = G3.dfs(0);
        for(int i=0; i<G3.getV(); i++) {
            System.out.print(p3[i] + " ");
        }
        System.out.println("\nis connected? : " + G3.isConnected());
        System.out.print("\n");

        G.setWeight(0, 4, 10);
        System.out.println(G.toString());

    }
}