package HW5;
import java.util.*;

public abstract class AbstractGraph<V> implements Graph<V> {
	// Store vertices
	protected List<V> vertices = new ArrayList();
	// Adjacency lists
	protected List<List<Edge>> neighbors = new ArrayList();

	/** Edge inner class inside the AbstractGraph class */
	public static class Edge {
		public int u; // Starting vertex of the edge
		public int v; // Ending vertex of the edge

		/** Construct an edge for (u, v) */
		public Edge(int u, int v) {
			this.u = u;
			this.v = v;
		}

		public boolean equals(Object o) {
			return u == ((Edge) o).u && v == ((Edge) o).v;
		}

        public String toString() {return "(" + u + ", " + v + ")"; };
	}

	@Override /** Add a vertex to the graph */
	public boolean addVertex(V vertex) {
		if (!vertices.contains(vertex)) {
			vertices.add(vertex);
			neighbors.add(new ArrayList<Edge>());
			return true;
		} else {
			return false;
		}
	}

	/** Construct an empty graph */
	protected AbstractGraph() {}

	/** Construct a graph from vertices and edges stored in arrays */
	protected AbstractGraph(V[] vertices, int[][] edges) {
		for (int i = 0; i < vertices.length; i++)
			addVertex(vertices[i]);

		createAdjacencyLists(edges, vertices.length);
	}

	/** Construct a graph from vertices and edges stored in List */
	protected AbstractGraph(List<V> vertices, List<Edge> edges) {
		for (int i = 0; i < vertices.size(); i++)
			addVertex(vertices.get(i));

		createAdjacencyLists(edges, vertices.size());
	}

	/** Create adjacency lists for each vertex */
	private void createAdjacencyLists(int[][] edges, int numberOfVertices) {
		for (int i = 0; i < edges.length; i++) {
			addEdge(edges[i][0], edges[i][1]);
		}
	}

	@Override /** Add an edge to the graph */
	public boolean addEdge(int u, int v) {
		return addEdge(new Edge(u, v));
	}

	/** Create adjacency lists for each vertex */
	private void createAdjacencyLists(List<Edge> edges, int numberOfVertices) {
		for (Edge edge : edges) {
			addEdge(edge.u, edge.v);
		}
	}

	/** Add an edge to the graph */
	protected boolean addEdge(Edge e) {
		if (e.u < 0 || e.u > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.u);
		if (e.v < 0 || e.v > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.v);
		if (!neighbors.get(e.u).contains(e)) {
			neighbors.get(e.u).add(e);
			return true;
		} else {
			return false;
		}
	}

	/** Construct a graph for integer vertices 0, 1, 2 and edge list */
	protected AbstractGraph(List<Edge> edges, int numberOfVertices) {
		for (int i = 0; i < numberOfVertices; i++)
			addVertex((V) (new Integer(i))); // vertices is {0, 1, ...}
		createAdjacencyLists(edges, numberOfVertices);
	}

	/** Construct a graph from integer vertices 0, 1, and edge array */
	protected AbstractGraph(int[][] edges, int numberOfVertices) {
		for (int i = 0; i < numberOfVertices; i++)
			addVertex((V) (new Integer(i))); // vertices is {0, 1, ...}
		createAdjacencyLists(edges, numberOfVertices);
	}

	@Override /** Return the vertices in the graph */
	public List<V> getVertices() {
		return vertices;
	}

	@Override /** Return the object for the specified vertex */
	public V getVertex(int index) {
		return vertices.get(index);
	}

	@Override /** Return the index for the specified vertex object */
	public int getIndex(V v) {
		return vertices.indexOf(v);
	}

	@Override /** Return the number of vertices in the graph */
	public int getSize() {
		return vertices.size();
	}

	@Override /** Return the neighbors of the specified vertex */
	public List<Integer> getNeighbors(int index) {
		List<Integer> result = new ArrayList();
		for (Edge e : neighbors.get(index))
			result.add(e.v);
		return result;
	}

	@Override /** Return the (out)degree for a specified vertex */
	public int getDegree(int u) {
		return neighbors.get(u).size();
	}

	@Override /** Print the edges */
	public void printEdges() {
		for (int u = 0; u < neighbors.size(); u++) {
			System.out.print("Vertex "+ u + ": ");
			for (Edge e : neighbors.get(u)) {
				System.out.print("(" + getVertex(e.u) + ", " + getVertex(e.v) + ") ");
			}
			System.out.println();
		}
	}

	@Override /** Clear the graph */
	public void clear() {
		vertices.clear();
		neighbors.clear();
	}

	@Override /** Obtain a DFS tree starting from vertex v */
	public Tree dfs(int v) {
		int[] parent = new int[vertices.size()];
		for (int i = 0; i < parent.length; i++)
			parent[i] = -1; // Initialize parent[i] to -1
		// Mark visited vertices (default false)
		boolean[] isVisited = new boolean[vertices.size()];
		List<Integer> searchOrder = new ArrayList();
		// Recursively search
		dfs(v, parent, isVisited, searchOrder);
		// Return the search tree
		return new Tree(v, parent, searchOrder);
	}

	/** Recursive method for DFS search */
	private void dfs(int u, int[] parent, boolean[] isVisited, List<Integer> searchOrder) {
		// Store the visited vertex
		searchOrder.add(u);
		isVisited[u] = true; // Vertex v visited
		for (Edge e : neighbors.get(u))
			if (!isVisited[e.v]) {
				parent[e.v] = u; // The parent of vertex e.v is u
				dfs(e.v, parent, isVisited, searchOrder); // Recursive search
			}
	}

	// Add the inner class Tree in the AbstractGraph class
	public class Tree {
		private int root; // The root of the tree
		private int[] parent; // Store the parent of each vertex
		private List<Integer> searchOrder; // Store the search order

		/** Construct a tree with root, parent, and searchOrder */
		public Tree(int root, int[] parent, List<Integer> searchOrder) {
			this.root = root;
			this.parent = parent;
			this.searchOrder = searchOrder;
		}

		/** Return the root of the tree */
		public int getRoot() {
			return root;
		}

		/** Return the parent of vertex v */
		public int getParent(int v) {
			return parent[v];
		}

		/** Return the path of vertices from a vertex to the root */
		public List<V> getPath(int index) {
			ArrayList<V> path = new ArrayList();
			do {
				path.add(vertices.get(index));
				index = parent[index];
			} while (index != -1);
			return path;
		}

		/** Print a path from the root to vertex v */
		public void printPath(int index) {
			List<V> path = getPath(index);
			System.out.print("A path from " + vertices.get(root) + " to " + vertices.get(index) + ": ");
			for (int i = path.size() - 1; i >= 0; i--)
				System.out.print(path.get(i) + " ");
		}

		/** Return an array representing search order */
		public List<Integer> getSearchOrder() {
			return searchOrder;
		}

		/** Return number of vertices found */
		public int getNumberOfVerticesFound() {
			return searchOrder.size();
		}

		/** Print the whole tree */
		public void printTree() {
			System.out.println("Root is: " + vertices.get(root));
			System.out.print("Edges: ");
			for (int i = 0; i < parent.length; i++)
				if (parent[i] != -1) {
					// Display an edge
					System.out.print("(" + vertices.get(parent[i]) + ", " + vertices.get(i) + ") ");
				}
			System.out.println();
		}
	}

	@Override /** Starting bfs search from vertex v */
	public Tree bfs(int v) {
		List<Integer> searchOrder = new ArrayList();
		int[] parent = new int[vertices.size()];
		for (int i = 0; i < parent.length; i++)
			parent[i] = -1; // Initialize parent[i] to -1
		LinkedList<Integer> queue = new LinkedList(); // list used as a queue
		queue.offer(v); // Enqueue v
		boolean[] isVisited = new boolean[vertices.size()];
		isVisited[v] = true; // Mark it visited
		while (!queue.isEmpty()) {
			int u = queue.poll(); // Dequeue to u
			searchOrder.add(u); // u searched
			for (Edge e : neighbors.get(u))
				if (!isVisited[e.v]) {
					queue.offer(e.v); // Enqueue v
					parent[e.v] = u; // The parent of w is u
					isVisited[e.v] = true; // Mark it visited
				}
		}
		return new Tree(v, parent, searchOrder);
	}

	//Problem 2 Helper 
	public boolean isConnected() {
		Tree tree = bfs(0);
		return tree.getNumberOfVerticesFound() == vertices.size();
	}

	//Problem 3
	public List getPath(int u, int v) {
		List path = new ArrayList();
		
		Tree tree = bfs(v);
		path = tree.getPath(u);

		if(path.size() < 2) return null;

		return path;
	}

	//Problem 4
	public boolean isBipartite() {
		boolean[] membership = new boolean[vertices.size()]; //True - Left, False - Right
		boolean[] changed = new boolean[vertices.size()]; //Check if vertex already visited (is 1) or not (is 0)

		// for(int i=0; i<membership.length; i++) {
		// 	membership[i] = false;
		// 	changed[i] = false;
		// }

		for(int i=0; i<neighbors.size(); i++) {
			int u = i;

			if(! changed[u]) {
				changed[u] = true;
				membership[u] = true; //first one assigned left, its neighbors assigned right
				
				for(int j=0; j<neighbors.get(i).size(); j++) {
					int v = neighbors.get(i).get(j).v;
	
					//If neighbor is already assigned, don't bother changing then
					if(! changed[v]) {
						if(!membership[u]) { //u being true means v be false and thats already default
							membership[v] = true;
						}
					}
				}
			}
		}

		//All vertices should be labelled true/false in membership by now
		//Now check if 2 neighboring vertices conflict with same membership or not, if yes then return false

		for(List<Edge> edges:neighbors) {
			for(Edge e:edges) {
				if(membership[e.u] == membership[e.v]) {
					return false;
				}
			}
		}

		return true;
	}	

    /** Determine if there is a cycle in the graph */
	public boolean isCyclic() {
		List<Integer> cycle = new ArrayList();

		int[] parent = new int[vertices.size()];
		for(int i=0; i<parent.length; i++) parent[i] = -1;

		boolean[] isVisited = new boolean[parent.length];
		boolean[] stack = new boolean[parent.length];

		boolean cycleFound = false;
		for(int i=0; i<isVisited.length; i++) {
			if(!isVisited[i]) {
				cycleFound = cycleFound || isCyclic(i, stack, cycle, isVisited);
				
				if(cycleFound) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isCyclic(int u, boolean[] stack, List<Integer> cycle, boolean[] isVisited) {
		if(stack[u]) {
			for(int i=0; i<stack.length; i++) {
				if(stack[i]) {
					cycle.add(i);
				}
			}

			return true;
		}

		if(isVisited[u]) {
			return false;
		}

		isVisited[u] = true;
		stack[u] = true;

		for(Edge e:neighbors.get(u)) {
			if(isCyclic(e.v, stack, cycle, isVisited)) {
				return true;
			}
		}

		stack[u] = false;
		return false;
	}

	//Obtain a cycle containing v in a Graph
    public List<Integer> getACycle(int v) {
		List<Integer> cycle = new ArrayList();

		int[] parent = new int[vertices.size()];
		for(int i=0; i<parent.length; i++) parent[i] = -1;

		boolean[] isVisited = new boolean[parent.length];
		boolean[] stack = new boolean[parent.length];

		boolean cycleFound = false;
		for(int i=0; i<isVisited.length; i++) {
			if(!isVisited[i]) {
				cycleFound = cycleFound || isCyclic(i, stack, cycle, isVisited);
				
				if(cycleFound) {
					break;
				}
			}
		}

		return cycle;
    }
}