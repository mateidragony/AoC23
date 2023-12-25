import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.*;


public class q25 {

    public static final String filename = "in.txt";

    //#region
    // Modified Karger's algorithm since we know what the minimum cut is
    // (https://web.stanford.edu/class/archive/cs/cs161/cs161.1172/CS161Lecture16.pdf)

    public static SuperNode getRandomNode(Set<SuperNode> nodes){
        int idx = (int)(Math.random() * nodes.size());
        for(SuperNode sn : nodes){
            if(idx == 0) return sn;
            idx--;
        }
        return null; // won't happen (trust)
    }

    public static void initGraph(HashMap<String, List<String>> graph, Set<SuperNode> superNodes, HashMap<SuperNode, Set<SuperNode>> edges){
        for(String node : graph.keySet()){
            SuperNode x = new SuperNode(node);
            superNodes.add(x);
            edges.put(x, new HashSet<>());
            for(String connection : graph.get(node)){
                SuperNode connNode = new SuperNode(connection);
                edges.get(x).add(connNode);
            }
        }
    }

    public static void merge(SuperNode a, SuperNode b, Set<SuperNode> superNodes, HashMap<SuperNode, Set<SuperNode>> edges){

        superNodes.remove(a);
        superNodes.remove(b);

        SuperNode x = new SuperNode();
        for(String node : a.nodes) x.nodes.add(node);
        for(String node : b.nodes) x.nodes.add(node);

        edges.put(x, new HashSet<>());

        for(SuperNode edge : edges.get(a)) edges.get(x).add(edge);
        for(SuperNode edge : edges.get(b)) edges.get(x).add(edge);

        for(Set<SuperNode> edge : edges.values()){
            if(edge.contains(a) || edge.contains(b)){
                edge.remove(a);
                edge.remove(b);
                edge.add(x);
            }
        }

        edges.remove(a);
        edges.remove(b);

        superNodes.add(x);
    }

    public static int numEdgesCut(Set<String> graph1Nodes, Set<String> graph2Nodes, HashMap<String, List<String>> graph){

        int num = 0;

        for(String node : graph1Nodes){
            List<String> edges = graph.get(node);
            for(String edge : edges) if(graph2Nodes.contains(edge)) num++;
        }

        return num;
    }

    public static int[] karger(HashMap<String, List<String>> graph){
        Set<SuperNode> superNodes = new HashSet<>();
        HashMap<SuperNode, Set<SuperNode>> edges = new HashMap<>();

        initGraph(graph, superNodes, edges);

        while(superNodes.size() > 2){
            SuperNode a = getRandomNode(edges.keySet());
            SuperNode b = getRandomNode(edges.get(a));

            merge(a, b, superNodes, edges);
        }

        // Graph is now split into 2
        int num = -1;

        Iterator<SuperNode> iterator = edges.keySet().iterator();
        Set<String> graph1Nodes = new HashSet<>(iterator.next().nodes);
        Set<String> graph2Nodes = new HashSet<>(iterator.next().nodes);

        num = numEdgesCut(graph1Nodes, graph2Nodes, graph);

        return new int[]{num, graph1Nodes.size() * graph2Nodes.size()};
    }

    public static int repeatKarger(HashMap<String, List<String>> graph){
        int num;
        int ret;
        do{
            int[] karger = karger(graph);
            num = karger[0];
            ret = karger[1];
        } while (num != 3);

        return ret;
    }



    //#endregion


    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        // --------------------
        //      Part One
        // --------------------

        HashMap<String, List<String>> components = new HashMap<>();
        for(String line : lines){
            String[] data = line.split(": ");
            String curComponent = data[0].trim();
            components.putIfAbsent(curComponent, new ArrayList<>());

            String[] connections = data[1].trim().split(" ");
            for(String connection : connections){
                connection = connection.trim();
                components.putIfAbsent(connection, new ArrayList<>());

                components.get(connection).add(curComponent);
                components.get(curComponent).add(connection);
            }
        }


        HashMap<String, List<String>> test = new HashMap<>();
        test.put("a", Stream.of("b", "c", "d").toList());
        test.put("b", Stream.of("a", "d").toList());
        test.put("c", Stream.of("a", "d", "e").toList());
        test.put("d", Stream.of("a", "b", "c", "e").toList());
        test.put("e", Stream.of( "c", "d").toList());

        int partOneSum = repeatKarger(components);



        // --------------------
        //      Part Two
        // --------------------


        // --------------------
        //       Answers
        // --------------------        

        System.out.println("Part 1: "+partOneSum);
        System.out.println("Part 2: ");
    }

    public static class SuperNode{
        List<String> nodes;
        public SuperNode(String... nodes){ 
            this.nodes = new ArrayList<>();
            for(String node : nodes) this.nodes.add(node);
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof SuperNode)) return false;
            SuperNode sn = (SuperNode)obj;
            return nodes.equals(sn.nodes);
        }
        @Override
        public String toString() {
            return "SuperNode [nodes=" + nodes + "]";
        }
        
        
    }

    // public static class SuperEdge{
    //     SuperNode start, end;

    //     public SuperEdge(SuperNode start, SuperNode end) {
    //         this.start = start;
    //         this.end = end;
    //     }

    //     @Override
    //     public int hashCode() {
    //         final int prime = 31;
    //         int result = 1;
    //         result = prime * result + ((start == null) ? 0 : start.hashCode());
    //         result = prime * result + ((end == null) ? 0 : end.hashCode());
    //         return result;
    //     }

    //     @Override
    //     public boolean equals(Object obj) {
    //         if(!(obj instanceof SuperEdge)) return false;
    //         SuperEdge se = (SuperEdge)obj;
    //         return start.equals(se.start) && end.equals(se.end);
    //     }
    // }
}
