package HW5;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Connected {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = scan.nextLine();

        File file = new File(fileName);
        scan = new Scanner(file);
        
        String line;
        int numVertices = 0;
        UnweightedGraph<Integer> graph;
        List<AbstractGraph.Edge> list = new ArrayList<AbstractGraph.Edge>();

        while(scan.hasNextLine()) {
            line = scan.nextLine();

            if(line.length() == 1) {
                numVertices = Integer.parseInt(line);
            } else {
                int v = Integer.parseInt(line.substring(0, 1));
                line = line.substring(1).trim();

                for(int i=0; i<line.length(); i++) {
                    if(line.charAt(i) != ' ') {
                        list.add(new AbstractGraph.Edge(v, Integer.parseInt(line.substring(i, i+1))));
                    }
                }
            }
        }

        graph = new UnweightedGraph<Integer>(list, numVertices);
        System.out.println("The number of vertices is " + graph.vertices.size());
        graph.printEdges();

        boolean isConnected = graph.isConnected();
        System.out.print("The graph is ");
        if(isConnected) {
            System.out.println("connected.");
        } else {
            System.out.println("not connected.");
        }

        System.out.println(graph.isBipartite());
    }
}
