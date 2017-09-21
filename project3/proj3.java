import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Project 3
 *
 * Heap, UpTree, and Edge Classes provided in this file itself, to make compilation easier
 * 
 * Input is edges of a graph specified by their vertex numbers and a weight for each edge
 * ex.  0   1    5.0
 
 *      1   2    5.0
 
 *      2   3    5.0
 
 *      3   4    5.0

 * 
 * Input is used to make Edge objects for every line of input
 * Edge Objects are then entered into a heap which essentially orders the edges by weight
 * The heap is printed. Then the heap is used as an input to used Kruskal's algorithm to 
 * find a minimum spanning tree. The edges are popped off and Uptrees are used as part of
 * Kruskal's algorithm to determine if a vertex has yet been considered to be in the
 * minimum spanning tree. The minimum spanning tree is printed once it has been created
 * Finally an Adjacency List is made from a heap of edges. Edges are again popped from the
 * heap to created the Adjacency List. The Adjacency List is printed.
 *
 * To compile: javac proj3.java
 *
 * To run: java proj3 < inputfile > outputfile
 * 
 * @author Soorya Kumar
 */
public class proj3 {
    
    /**
     * Kruskal's algorithm for Minimum Spanning Trees
     * @param h heap of edges to create Minimum Spanning Trees from
     * @param numberOfVertices total number of vertices in graph
     * @return Minimum Spanning Trees made from Kruskal's algorithm
     */
    public static LinkedList<Edge> KruskalMST(Heap h, int numberOfVertices) {
        LinkedList<Edge> MST = new LinkedList<Edge>();
        UpTree components = new UpTree(numberOfVertices + 1);
        while (components.sets() > 1) {
            Edge minEdge = h.deleteMin();
            int set1 = components.find(minEdge.getVertex1());
            int set2 = components.find(minEdge.getVertex2());
            if (set1 != set2) {
                components.union(set1, set2);
                MST.add(minEdge);
            }
        }
        return MST;
    }
    
    /**
     * Creates an Adjacency List from a heap of edges
     * the index serves as the key, and each index represents that vertex (index 0 represents vertex 0)
     * the element at each index is the first edge (from lowest neighboring vertex to highest)
     * connected to the vertex represented by that index. All other edges are linked together in order 
     * using the next field for each edge 
     * @param h heap of edges to create Adjacency List from
     * @param numberOfVertices total number of vertices in graph
     * @return Adjacency List
     */
    public static Edge[] AdjacencyList(Heap h, int numberOfVertices) {
        Edge[] vertices = new Edge[numberOfVertices + 1];
        Edge temp;
        while (h.size() > 0) {
            Edge minEdge = h.deleteMin();
            if (vertices[minEdge.getVertex1()] == null) {
                vertices[minEdge.getVertex1()] = new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), minEdge.next());
            }
            else {
                if (vertices[minEdge.getVertex1()].getVertex2() > minEdge.getVertex2()) {
                    temp = vertices[minEdge.getVertex1()];
                    vertices[minEdge.getVertex1()] = new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), temp);
                    
                }
                else if (vertices[minEdge.getVertex1()].getVertex2() < minEdge.getVertex2()) {
                    Edge traverser = vertices[minEdge.getVertex1()];
                    while (traverser.next() != null && traverser.next().getVertex2() < minEdge.getVertex2() ) {
                        traverser = traverser.next();
                    }
                    if (traverser.next() == null || traverser.next().getVertex2() != minEdge.getVertex2()) {
                        temp = traverser.next();
                        traverser.setNext(new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), temp));
                    }
                }
            }
            if (vertices[minEdge.getVertex2()] == null) {
                vertices[minEdge.getVertex2()] = new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), minEdge.next());
            }
            else {
                if (vertices[minEdge.getVertex2()].getVertex1() > minEdge.getVertex1()) {
                    temp = vertices[minEdge.getVertex2()];
                    vertices[minEdge.getVertex2()] = new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), temp);
                }
                else if (vertices[minEdge.getVertex2()].getVertex1() < minEdge.getVertex1()) {
                    Edge traverser = vertices[minEdge.getVertex2()];
                    while (traverser.next() != null && traverser.next().getVertex1() < minEdge.getVertex1() ) {
                        traverser = traverser.next();
                    }
                    if (traverser.next() == null || traverser.next().getVertex1() != minEdge.getVertex1()) {
                        temp = traverser.next();
                        traverser.setNext(new Edge(minEdge.getVertex1(), minEdge.getVertex2(), minEdge.getWeight(), temp));
                    }
                }
            }
        }
        return vertices;
    }
    
    /**
     * prints the Adjacency List
     * @param aL Adjacency List to print
     */
    public static void printAdjacencyList(Edge[] aL) {
        for (int i = 0; i < aL.length; i++) {
            Edge e = aL[i];
            while(e != null) {
                if (i < e.getVertex2()) {
                    System.out.printf("%4d ", e.getVertex2());
                }
                else {
                    System.out.printf("%4d ", e.getVertex1());
                }
                e = e.next();
            }
            System.out.printf("\n");
        }
    }
    
    /**
     * Program uses standard input and output, redirection should be used on the
     * the command line with the input and output files
     * 
     * Scans input, calls method to print heap, calls method for Kruskal's algorithm,
     * prints minimum spanning tree, calls method to create adjacency list, calls
     * method to print adjacency list
     * @param args
     */
    public static void main(String[] args) {
        Scanner inputLineScanner = new Scanner(System.in);
        int maxVert = 0;
        int vertex1 = 0;
        int vertex2 = 0;
        float weight = 0;
        Edge currentEdge;
        Heap edgeHeap = new Heap();
        while(true) {
            String line = inputLineScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            vertex1 = lineScanner.nextInt();
            if (vertex1 == -1) {
                break;
            }
            vertex2 = lineScanner.nextInt();
            weight = lineScanner.nextFloat();
            maxVert = Math.max(vertex1, maxVert);
            maxVert = Math.max(vertex2, maxVert);
            if (vertex1 > vertex2) {
                int temp = vertex1;
                vertex1 = vertex2;
                vertex2 = temp;
            }
            currentEdge = new Edge(vertex1, vertex2, weight, null);
            edgeHeap.insert(currentEdge);
            lineScanner.close();
        }
        edgeHeap.printHeapInOrder();
        Heap copyOfEdgeHeap = new Heap();
        Heap copyOfEdgeHeap2 = new Heap();
        Heap copyOfEdgeHeap3 = new Heap();
        int originalSize = edgeHeap.size();
        for (int i = 0; i < originalSize; i++) {
            Edge e = edgeHeap.deleteMin();
            copyOfEdgeHeap.insert(e);
            copyOfEdgeHeap2.insert(e);
            copyOfEdgeHeap3.insert(e);
        }
        LinkedList<Edge> kMST = KruskalMST(copyOfEdgeHeap, maxVert);
        Collections.sort(kMST);
        Edge[] aL = AdjacencyList(copyOfEdgeHeap2, maxVert);        
        for (int i = 0; i < kMST.size(); i++) {
            System.out.printf("%4d %4d\n", ((Edge) kMST.get(i)).getVertex1(), ((Edge) kMST.get(i)).getVertex2());
        }
        printAdjacencyList(aL);
        inputLineScanner.close();
    }
    
    /**
     * Custom UpTree Implementaion
     * 
     * @author Soorya Kumar
     */
    public static class UpTree {

        /** array for all elements in UpTree */
        private int[] list;
        
        /**
         * Constructor for UpTree
         * @param n number of elements to create UpTree with
         */
        public UpTree(int n) {
            list = new int[n];
            Arrays.fill(list, -1);
        }
        
        /**
         * creates a union between two sets
         * @param a root of one of the sets
         * @param b root of the other set
         * @return root of the union of the two sets
         */
        public int union(int a, int b) {
            if(Math.abs(list[a]) > Math.abs((list[b]))) {
                list[a] = -(Math.abs(list[a]) + Math.abs((list[b])));
                list[b] = a;
                return a;
            }
            else {
                list[b] = -(Math.abs(list[a]) + Math.abs((list[b])));
                list[a] = b;
                return b;
            }
        }
        
        /**
         * finds the root of the set the element is in
         * @param a element to find which set it is in
         * @return root of the set that parameter a is in
         */
        public int find(int a) {
            while (list[a] > 0) {
                a = list[a];
            }
            return a;
        }
        
        /**
         * returns the number of different sets in the UpTree
         * @return number of different sets in the UpTree
         */
        public int sets() {
            int numberOfSets = 0;
            for (int i = 0; i < list.length; i++) {
                if (list[i] < 0) {
                    numberOfSets++;
                }
            }
            return numberOfSets;
        }
    }
    
    /**
     * Custom Heap implementation
     * 
     * @file Tree.java
     * @author Soorya Kumar
     */
    public static class Heap {

        /** initial size of array list */
        private static final int INIT_SIZE = 5000;
        /** array managing the elements in the array list */
        private Edge[] list;
        /** current accessible size of array list */
        private int size;

        /**
         * Constructer for Heap
         */
        public Heap() {
            list = new Edge[INIT_SIZE];
            size = 0;
        }
        
        /**
         * inserts edge into heap
         * @param e edge to insert
         */
        public void insert(Edge e) {
            list[size] = e;
            size++;
            upHeap(size);
        }
        
        /**
         * adjusts heap to continue to adhere to heap structure requirements
         * @param i index to inspect
         */
        private void upHeap(int i) {
            if (i > 1) {
                if (list[(i / 2) - 1].getWeight() > list[i - 1].getWeight()) {
                    swap((i / 2) - 1, i - 1);
                    upHeap((i / 2));
                }
            }
        }
        
        /**
         * swap positions of two edges in heap
         * @param e1 index of one edge to swap
         * @param e2 index of the other edge to swap
         */
        private void swap(int e1, int e2) {
            Edge tempEdge = list[e1];
            list[e1] = list[e2];
            list[e2] = tempEdge;
        }
        
        /**
         * delete minimum edge from heap, which is the root
         * @return minimum edge (root) of heap
         */
        public Edge deleteMin() {
            Edge root = list[0];
            swap(0, size - 1);
            list[size - 1] = null;
            size--;
            if (size > 0) {
                downHeap(0);
            }
            return root;
        }
        
        /**
         * adjusts heap after removal of minimum edge to 
         * continue to adhere to heap structure requirements
         * @param m index to inspect
         */
        public void downHeap(int m) {
            int i = 0;
            if ((2 * m + 2) < size) {
                if (list[2 * m + 2].getWeight() <= list[2 * m + 1].getWeight()) {
                    i = 2 * m + 2;
                }
                else {
                    i = 2 * m + 1;
                }
            }
            else if ((2 * m + 1) < size) {
                    i = 2 * m + 1;
            }
            if (i > 0 && (list[m].getWeight() > list[i].getWeight() )) {
                swap(m, i);
                downHeap(i);
            }
        }
        
        /**
         * returns size of heap
         * @return size of heap
         */
        public int size() {
            return size;
        }
        
        /**
         * prints the heap in the order of each node
         */
        public void printHeapInOrder() {
            for (int i = 0; i < size; i++) {
                System.out.printf("%4d %4d\n", list[i].getVertex1(), list[i].getVertex2());
            }
        }
    }
    
    /**
     * Edge class
     * 
     * @author Soorya Kumar
     */
    public static class Edge implements Comparable<Edge> {

        /** vertex1 */
        private int vertex1;
        /** vertex2 */
        private int vertex2;
        /** weight of edge */
        private float weight;
        /** next edge */
        private Edge next;
        
        /**
         * constructor for Edge
         * @param vertex1 vertex 1
         * @param vertex2 vertex 1
         * @param weight weight of edge
         * @param next next edge
         */
        public Edge(int vertex1, int vertex2, float weight, Edge next) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.weight = weight;
            this.next = next;
        }
        
        /**
         * returns vertex1
         * @return vertex1
         */
        public int getVertex1() {
            return vertex1;
        }
        
        /**
         * returns vertex2
         * @return vertex2
         */
        public int getVertex2() {
            return vertex2;
        }
        
        /**
         * returns weight of edge
         * @return weight of edge
         */
        public float getWeight() {
            return weight;
        }
        
        /**
         * returns next edge
         * @return next edge
         */
        public Edge next() {
            return next;
        }
        
        /**
         * set next edge
         * @param e edge to set as next edge for this edge
         */
        public void setNext(Edge e) {
            this.next = e;
        }
        
        /**
         * compare to another edge
         * @param e edge to compare with
         * @return -1 if this edge comes before, 0 if equal, 1 if this edge comes after e
         */
        @Override
        public int compareTo(Edge e) {
            if (this.vertex1 < e.vertex1)
                return -1;
            else if(this.vertex1 > e.vertex1)
                return 1;
            else {
                if (this.vertex2 < e.vertex2)
                    return -1;
                else if(this.vertex2 < e.vertex2)
                    return 1;
                else
                    return 0;
            }
        }
    }
}