package min_cut;

import utils.Graph;
import utils.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

// Usamos algoritmo de Ford Fulkerson
public class DeterministicAlgorithm implements MinCutApp {

    Graph G, resG;
    ArrayList<Pair> bestMinCut = new ArrayList<>();
    int bestMaxFlow = 0;
    int vertex_assign[];

    public DeterministicAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);

        vertex_assign = new int[G.getV()];
        for(int i=0; i<G.getV(); i++)
            vertex_assign[i] = i;
    }

    public void vertexAssign(int[] assign) {
        vertex_assign = assign;
    }

    private int[] dfs_aumentante(int s, int t) {
        int parent[] = new int[G.getV()];
        //int visited[] = new int[G.getV()];
        //int T = t;
        for(int i=0; i<G.getV(); i++) {
            parent[i] = -1;
            //visited[i] = 0;
        }

        Stack<Integer> st = new Stack<>(); st.push(s);
        parent[s] = s;
        //visited[s] = 0;

        while(!st.empty()) {
            int u = st.peek(); st.pop();

            /*
            if(visited[u] == 0) {
                visited[u] = 1;
                //for
            } else if(visited[u] == 1) {
                visited[u] = 2;
            } else {
                continue;
            }
            */

            for(int v : G.getNeighboorAdjL(u)) {
                if(parent[v] == -1 && G.getWeight(u, v) > 0) {
                    parent[v] = u;
                    st.push(v);
                }
            }
        }

        return parent;
    }

    /*
        Pendiente: Cambiar los grafos para que mantengan los peso, y cambiar dfs para realizar actualizaciones
                    mientras recorre el grafo.
                   Tal vez cambiar el algoritmo a Dinic.
     */
    public void maxFlow(int s, int t) {
        while(true) {
            // Determinar la existencia de un camino aumentante.
            int parent[] = dfs_aumentante(s, t);

            for(int i : parent) {
                System.out.print(i + " ");
            }
            System.out.println("");

            if(parent[t] == -1)
                break;

            int fin = t;
            int min_cap = 1000000000;

            // Puesto que todas las aristas tienen el mismo peso, si existe un camino aumentante,
            // entonces es de costo 1.

            // Obtencion de cual es el vertice de menor peso
            while(fin != parent[fin]) {
                int ini = parent[fin];

                if(min_cap > G.getWeight(ini, fin)) {
                    min_cap = G.getWeight(ini, fin);
                    //min_edge = new Pair(ini, fin);
                }

                fin = ini;

            }
            if(min_cap == 0) {
                System.out.println("mincap");
                break;
            }

            // Actualizamos el grafo.
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
    }

    public void minCut() {
        int s = vertex_assign[0];
        int best_mf = 1000000000;
        ArrayList<Pair> best_mc = new ArrayList<>();

        int reg[] = new int[G.getV()];
        for(int i=0; i<G.getV(); i++)
            reg[i] = 0;
        reg[0] = 1;

        for(int i : vertex_assign) {
            if(reg[i] == 1)
                continue;

            reg[i] = 1;
            //System.out.println("minCut: " + s + " -> " + i);
            maxFlow(s, i);

            int parent[] = dfs_aumentante(s, i);
            ArrayList<Pair> lastMinCut = new ArrayList<>();
            for(int j=0; j<G.getV(); j++) {
                for(int u : G.getNeighboorAdjL(j)) {
                    if(parent[j] != -1 && parent[u] == -1) {
                        lastMinCut.add(new Pair(j, u));
                    }
                }
            }

            int mf = lastMinCut.size();

            if(mf < best_mf) {
                best_mf = mf;
                best_mc.clear();
                best_mc.addAll(lastMinCut);
            }

            G = new Graph(resG);
        }
        bestMinCut.clear();
        bestMinCut.addAll(best_mc);
        bestMaxFlow = best_mf;
    }

    public static void main(String[] args) {

        Graph G1 = new Graph(8);
        G1.randomConnectedGraph(0.5);
        //System.out.println("grafo inicial:");
        //System.out.print(G1.toString());

        DeterministicAlgorithm dt = new DeterministicAlgorithm(G1);
        int parents[] = dt.dfs_aumentante(0, 5);

        /*
        System.out.println("parent");
        for(int i=0; i<G1.getV(); i++) {
            System.out.print(parents[i] + " ");
        }
        System.out.print("\n\n");
        */

        dt.minCut();

        //System.out.println("grafo final:");
        //System.out.print(G1.toString());

        System.out.println("mf: " + dt.bestMaxFlow);
        System.out.println("minCut: ");
        for(Pair p : dt.bestMinCut) {
            System.out.print("(" + p.getFirst() + "," + p.getSecond() + ") ");
        }
        System.out.print("\n");



        Graph G = new Graph(3);
        G.addEdge(0, 1);
        G.addEdge(0, 1);
        //G.addEdge(1, 2);
        //G.addEdge(1, 2);
        G.addEdge(1, 2);
        System.out.print(G.toString() + "\n");

        DeterministicAlgorithm dt1 = new DeterministicAlgorithm(G);

        dt1.minCut();
        System.out.println("mf: " + dt1.bestMaxFlow);
        System.out.println("minCut: ");
        for(Pair p : dt1.bestMinCut) {
            System.out.print("(" + p.getFirst() + "," + p.getSecond() + ") ");
        }
        System.out.print("\n");

    }
}
