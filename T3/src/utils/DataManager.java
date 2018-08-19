package utils;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.util.Random;

public class DataManager {

    static public EdgeWeightedGraph getRandomGraph(int V, double p) {
        EdgeWeightedGraph graph = new EdgeWeightedGraph(V);
        Random random = new Random(V);
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if(i != j && random.nextFloat() < p){
                    graph.addEdge(new Edge(i, j, 1));
                }
            }
        }
        return null;
    }
}
