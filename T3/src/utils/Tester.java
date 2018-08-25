package utils;

import edu.princeton.cs.algs4.In;
import min_cut.DeterministicAlgorithm;
import min_cut.KargerAlgorithm;
import min_cut.MixedAlgorithm;

import java.io.*;
import java.util.ArrayList;

public class Tester {

    private static final int MIN_EXP = 10;
    private static final int MAX_EXP = 15;

    static private File getFile(String abs_path, String filename) {
        File dir = new File(abs_path);
        if(!dir.exists() && !dir.mkdir()) {
            System.out.println("Error al crear " + abs_path);
            System.exit(0);
        }

        File fd = new File(dir, filename);
        // the rest of your code
        try {
            if(fd.exists()) {
                System.out.println("loading file");
            } else if (fd.createNewFile()) {
                System.out.println("created new file");
            } else {
                System.out.println("could not create a new file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fd;
    }

    static public File saveGraph(Graph graph, String name) throws IOException {
        String cp_filename = name + ".txt",
                cp_absolute_path = new File("").getAbsolutePath() + "/T3/graphs/";
        File fd = getFile(cp_absolute_path, cp_filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        int V = graph.getV(), E = graph.getE();
        StringBuilder sb = new StringBuilder();
        sb.append(V).append("\t").append(E).append("\n");
        for (int u = 0; u < V; u++) {
            for (int v: graph.getNeighboorAdjL(u)) {
                sb.append(v).append("\t");
            }
            sb.deleteCharAt(sb.length() - 1).append("\n");
        }
        writer.write(sb.toString());
        writer.close();
        return fd;
    }

    static public Graph loadGraph(String name) throws IOException {
        String cp_filename = name + ".txt",
                cp_absolute_path = new File("").getAbsolutePath() + "/T3/graphs/";
        File fd = getFile(cp_absolute_path, cp_filename);

        BufferedReader reader = new BufferedReader(new FileReader(fd));

        String line = reader.readLine();
        if(line == null)
            return null;

        String[] header = line.split("\t");

        int V = Integer.parseInt(header[0]), E = Integer.parseInt(header[1]);
        Graph graph = new Graph(V);
        for (int u = 0; u < V; u++) {
            for (String sv: reader.readLine().split("\t")) {
                int v = Integer.parseInt(sv);
                graph.addEdge(u, v, 1);
            }
        }
        assert E == graph.getE();
        return graph;

    }

    static public void test0() throws IOException {
        Graph graph = new Graph(5);
        double p = 1.0/5;
        double[] p_list = {p, p + 0.05, p + 0.1, p + 0.15, p + 0.2};
        graph.randomConnectedGraph(p);
        System.out.println(graph);
        saveGraph(graph, "graph_test");
        Graph new_graph = loadGraph("graph_test");
        System.out.println(graph);
    }

    static private void writeMinCut(ArrayList<Pair> bestMinCut, String name) throws IOException {
        String cp_filename = name + ".txt",
                cp_absolute_path = new File("").getAbsolutePath() + "/T3/results/";
        File fd = getFile(cp_absolute_path, cp_filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));
        writer.write(bestMinCut.size() + System.lineSeparator());
        for(Pair p : bestMinCut) {
            writer.write(p.getFirst() + "\t" + p.getSecond() + System.lineSeparator());
        }
        writer.close();
    }

    static private String test(Graph graph, int k, String name) throws IOException {
        /*Graph graph1 = new Graph(graph),
                graph2 = new Graph(graph),
                graph3;*/

        StringBuilder sb = new StringBuilder();

        long start, end;

        //Deterministic
        System.out.println("Probando determinista...");
        start = System.currentTimeMillis();
        DeterministicAlgorithm deterministicMinCut = new DeterministicAlgorithm(graph);
        end = System.currentTimeMillis();
        System.out.printf("tiempo creacion=%d...\n", end - start);
        sb.append(end - start).append("\t");

        start = System.currentTimeMillis();
        deterministicMinCut.minCut();
        end = System.currentTimeMillis();
        System.out.printf("max flow = %f / tiempo corte =%d...\n", deterministicMinCut.time_max_flow, end - start);
        sb.append(deterministicMinCut.time_max_flow).append("\t").append(end - start).append(System.lineSeparator());
        writeMinCut(deterministicMinCut.bestMinCut, name + "-deterministic");

        // Karger
        System.out.println("Probando karger...");
        start = System.currentTimeMillis();
        KargerAlgorithm kargerMinCut = new KargerAlgorithm(graph);
        end = System.currentTimeMillis();
        System.out.printf("tiempo creacion=%d...\n", end - start);
        sb.append(end - start).append("\t");

        start = System.currentTimeMillis();
        kargerMinCut.kMinCut(k);
        end = System.currentTimeMillis();
        System.out.printf("tiempo corte =%d...\n", end - start);
        sb.append(end - start).append(System.lineSeparator());
        writeMinCut(deterministicMinCut.bestMinCut, name + "-karger");

        // Mixed
        int delta_t = graph.getV()/10, t = 3;
        int[] t_list = {t, t + 2*delta_t, t + 3*delta_t, t + 4*delta_t, t + 5*delta_t};
        for(int myt: t_list) {
            // graph3 = new Graph(graph);
            System.out.printf("Probando mezcla con t=%d...\n", myt);
            start = System.currentTimeMillis();
            MixedAlgorithm mixedMinCut = new MixedAlgorithm(graph);
            end = System.currentTimeMillis();
            System.out.printf("tiempo creacion=%d...\n", end - start);
            sb.append(end - start).append("\t");

            start = System.currentTimeMillis();
            mixedMinCut.kMinCut(k, t);
            end = System.currentTimeMillis();
            System.out.printf("tiempo corte =%d con t=%d...\n", end - start, myt);
            sb.append(end - start).append("\t").append(myt).append(System.lineSeparator());
            writeMinCut(deterministicMinCut.bestMinCut, name + "-mixed-" + Integer.toString(myt));
        }

        return sb.toString();
    }

    static private void testP(int n, int k)  throws IOException {
        String cp_filename = "minCut-" + n + "-" + System.currentTimeMillis(),
                cp_absolute_path = new File("").getAbsolutePath() + "/T3/results/";

        File fd = getFile(cp_absolute_path, cp_filename + ".tsv");

        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        double p = 1.0/5;
        double[] p_list = {p, p + 0.05, p + 0.1, p + 0.15, p + 0.2};
        for(double myP: p_list){
            String filename = "graph-" + n + "-" + myP;
            Graph graph = loadGraph(filename);
            if(graph == null) {
                graph = new Graph(n);
                graph.randomConnectedGraph(myP);
                saveGraph(graph, filename);
                // System.out.println(graph);
            }
            System.out.printf("V=%d p=%f E=%d...\n", graph.getV(), myP, graph.getE());
            writer.write(Double.toString(myP) + System.lineSeparator());
            writer.write(test(graph, k, cp_filename + "-" + Double.toString(myP)));
        }
    }

    static public void testMinCut() throws IOException {
        for(int j = MIN_EXP; j <= MAX_EXP; j++)
        {
            int n = (int) Math.pow(2, j);
            testP(n, 10);
        }
    }


    /*
    static public void test0(PriorityQueue c, DataManager dm, boolean debug) {
        System.out.println("Original array: " + dm.toString());
        Vector<Integer> vec;
        for(int i = 0; i < 3; i++) {
            vec = dm.getSuffleData();
            System.out.println("Shuffled array: " + vec.toString());
            for(int num: vec){
                c.insertar(num, num);
            }
            System.out.print("Final array: [ ");
            while(!c.isEmpty()){
                System.out.print(c.extraer_siguiente().getValue() + " ");
            }
            System.out.println("]");
        }
    }

    static public void sort_test(PriorityQueue cp, boolean debug, String name) throws IOException {
        String cp_filename = name + "-sort-" + System.currentTimeMillis() + ".tsv",
            cp_absolute_path = new File("").getAbsolutePath() + "/T2/results/sort/";

        File fd = getFile(cp_absolute_path, cp_filename);

        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        String header = "i\t2^i\theapify_time\textract_time" + System.lineSeparator();

        StringBuilder sb = new StringBuilder(header);
        for(int j = MIN_EXP_SORT; j <= MAX_EXP_SORT; j++) {
            sb.append(j); sb.append("\t");
            int n = (int) Math.pow(2, j);
            sb.append(n); sb.append("\t");
            System.out.println("Creando arreglo tamaño " + n + "...");
            DataManager dm = new DataManager(n, 0);

            System.out.println("Revolviendo arreglo...");
            List<Integer> list = dm.getSuffleData(), sorted_list;
            long start, end;
            Sort sort = new Sort(list, cp);

            System.out.println("Heapifiando arreglo...");
            start = System.currentTimeMillis();
            sort.heapify();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append("\t");

            System.out.println("Extrayendo elementos en orden...");
            start = System.currentTimeMillis();
            sorted_list = sort.sort();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append(System.lineSeparator());
        }

        writer.write(sb.toString());
        System.out.println("Escribiendo resultados en " +  cp_absolute_path + cp_filename + "...");
        System.out.println();
        System.out.println("############################## REPORT (" + name + "-sort) ###########################################################");
        System.out.println(sb.toString());
        writer.close();
    }

    static public void insert_and_melding_test(PriorityQueue cp, boolean debug, String name) throws IOException {
        String cp_absolute_path = new File("").getAbsolutePath() + "/T2/results/ins_meld/",
                cp_filename = name + "-ins_meld-" + System.currentTimeMillis() + ".tsv";

        File fd = getFile(cp_absolute_path, cp_filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));

        String header = "i\tn=2^" + MAX_EXP + "\tk=2^i\tinsert_time\tmelding_time" + System.lineSeparator();
        StringBuilder sb = new StringBuilder(header);

        int n = (int) Math.pow(2, MAX_EXP);
        System.out.println("Creando arreglo de tamaño " + n + "...");
        DataManager dm = new DataManager(n, 0);

        System.out.println("Revolviendo arreglo...");
        List<Integer> list = dm.getSuffleData(), sorted_list;

        for(int i = MAX_EXP_INS_MEL; i >= MIN_EXP_INS_MEL; i--) {
            sb.append(i); sb.append("\t");
            sb.append(n); sb.append("\t");
            int k = (int) Math.pow(2, i);
            sb.append(k); sb.append("\t");

            long start, end;
            InsertAndMelding insMel = new InsertAndMelding(i, list);

            System.out.println("Creando " + k + " subarreglos de tamaño " + n/k + " en subcolas...");
            start = System.currentTimeMillis();
            insMel.insercion(cp);
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append("\t");

            System.out.println("Aplicando melding en colas...");
            start = System.currentTimeMillis();
            PriorityQueue cola_final = insMel.melding();
            end = System.currentTimeMillis();
            sb.append(end - start); sb.append(System.lineSeparator());
        }

        writer.write(sb.toString());
        System.out.println("Escribiendo resultados en " +  cp_absolute_path + "...");
        System.out.println("");
        System.out.println("############################## REPORT (" + name + "-ins_meld) ###########################################################");
        System.out.println(sb.toString());
        writer.close();
    }
    */
}
