package min_cut;

import utils.Graph;
import utils.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

// Usamos algoritmo de Ford Fulkerson
public class DeterministicAlgorithm implements MinCutApp {

    Graph G, resG;
    ArrayList<Pair> bestMinCut = new ArrayList<>();
    int bestMaxFlow = 0;

    public DeterministicAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);
    }

    private int[] dfs_aumentante(int s) {
        int parent[] = new int[G.getV()];
        for(int i=0; i<G.getV(); i++) {
            parent[i] = -1;
        }

        Stack<Integer> st = new Stack<>(); st.push(s);
        parent[s] = s;

        while(!st.empty()) {
            int u = st.peek(); st.pop();

            for(int v : G.getNeighboorAdjL(u)) {
                if(parent[v] == -1 && G.getWeight(u, v) > 0) {
                    parent[v] = u;
                    st.push(v);
                }
            }
        }

        return parent;
    }

    public int maxFlow(int s, int t) {
        int flow_count = 0;
        //int cont = 0;
        while(true) {
            //if(cont > 7) break;

            int parent[] = dfs_aumentante(s);

            if(parent[t] == -1)
                break;

            int fin = t;
            int min_cap = 1000000000;
            Pair min_edge = new Pair(-1, -1);
            while(fin != parent[fin]) {
                int ini = parent[fin];

                if(min_cap > G.getWeight(ini, fin)) {
                    min_cap = G.getWeight(ini, fin);
                    min_edge = new Pair(ini, fin);
                }

                fin = ini;

            }
            if(min_cap == 0)
                break;

            flow_count += min_cap;
            bestMinCut.add(min_edge);

            System.out.println("flow count: " + flow_count);
            //cont++;
            System.out.print(G.toString());
            for(int a : parent) System.out.print(a + " ");
            System.out.print("\n\n");

            fin = t;
            while(fin != parent[fin]) {
                int ini = parent[fin];

                int last_w1 = G.getWeight(ini, fin);
                int last_w2 = G.getWeight(fin, ini);
                G.setWeight(ini, fin, last_w1 - min_cap);
                G.setWeight(fin, ini, last_w2 + min_cap);

                fin = ini;
            }
        }
        return flow_count;
    }

    public void minCut() {
        int s = 0;
        int best_mf = 1000000000;
        ArrayList<Pair> best_mc = new ArrayList<>();
        for(int i=1; i<G.getV(); i++) {
            System.out.println("iter: " + s + " " + i);
            int mf = maxFlow(s, i);
            System.out.println("minCut: ");
            for(Pair p : bestMinCut) {
                System.out.print("(" + p.getFirst() + "," + p.getSecond() + ") ");
            }
            System.out.print("\n");

            if(mf < best_mf) {
                best_mf = mf;
                best_mc.clear();
                best_mc.addAll(bestMinCut);
            }
            G = new Graph(resG);
        }
        bestMinCut.clear();
        bestMinCut.addAll(best_mc);
        bestMaxFlow = best_mf;
    }

    public static void main(String[] args) {
        Graph G1 = new Graph(8);
        G1.addEdge(0, 4);
        //G1.addEdge(0, 6);
        G1.addEdge(1, 2);
        G1.addEdge(1, 3);
        G1.addEdge(2, 3);
        G1.addEdge(2, 4);
        G1.addEdge(3, 7);
        G1.addEdge(4, 5);
        G1.addEdge(4, 6);
        G1.addEdge(4, 7);
        G1.addEdge(5, 6);
        System.out.println("grafo:");
        System.out.print(G1.toString());

        DeterministicAlgorithm dt = new DeterministicAlgorithm(G1);
        int parents[] = dt.dfs_aumentante(0);

        for(int i=0; i<G1.getV(); i++) {
            System.out.print(parents[i] + " ");
        }
        System.out.print("\n\n");

        dt.minCut();

        System.out.println("mf: " + dt.bestMaxFlow);
        System.out.println("minCut: ");
        for(Pair p : dt.bestMinCut) {
            System.out.print("(" + p.getFirst() + "," + p.getSecond() + ") ");
        }
        System.out.print("\n");

    }
}
