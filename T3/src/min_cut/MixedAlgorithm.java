package min_cut;

import utils.Graph;

public class MixedAlgorithm implements MinCutApp {
    DeterministicAlgorithm dt;
    KargerAlgorithm kg;

    Graph G, resG;

    public MixedAlgorithm(Graph _G) {
        G = new Graph(_G);
        resG = new Graph(_G);

    }



}
