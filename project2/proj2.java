import java.util.Scanner;

/**
 * From preorder and postorder traversals of a given tree as input, constructs
 * the tree and returns results on queries, and prints level order traversal
 * 
 * To compile: javac proj2.java
 *
 * To run: java proj2 < inputfile
 *
 * @file proj2.java
 * @author Soorya Kumar
 */
public class proj2 {
    
    /** array to hold preorder traversal */
    public static String[] pretrav;
    /** array to hold post order traversal */
    public static String[] posttrav;
    /** array to hold tree built from preorder and postorder traversals */
    public static Tree familyTree;
    /** the current sub tree in the familyTree to access and manipulate */
    public static Tree.SubTree currentSubTree;
    
    /**
     * This method searches for the index of an element in the postorder traversal
     * 
     * @param s string representing an element in the tree/postorder traversal
     * @return index of element in postorder traversal
     */
    public static int findElementInPostTrav(String s) {
        for (int i = 0; i < posttrav.length; i++) {
            if (posttrav[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method creates a string for the level-order traversal of the familyTree
     * 
     * @return string representing the level-order traversal of the familyTree
     */    
    public static String levelOrderTraversal() {
        String levelOrderTraversalString = "";
        ArrayList<Tree.SubTree> traversalQueue = new ArrayList<Tree.SubTree>();
        traversalQueue.add(traversalQueue.size(), familyTree.root);
        while (!traversalQueue.isEmpty()) {
            Tree.SubTree tempSubTree = traversalQueue.remove(0);
            levelOrderTraversalString += tempSubTree.element + ", ";
            if (tempSubTree.children != null) {
                for (int i = 0; i < tempSubTree.children.size(); i++) {
                    traversalQueue.add(traversalQueue.size(), tempSubTree.children.get(i));
                }
            }
        }
        levelOrderTraversalString = levelOrderTraversalString.substring(0, levelOrderTraversalString.length() - 2);
        levelOrderTraversalString += ".";
        return levelOrderTraversalString;
    }
    
    /**
     * This method builds the familyTree from the preorder and postorder
     * 
     * @param size size of tree to build
     * @param prestart index to begin in pretrav array
     * @param poststart index to begin in posttrav array 
     */
    public static void buildTree(int size, int prestart, int poststart) {
        if (size == 1) {
            currentSubTree.insert(pretrav[prestart]);
        }
        else if (size == pretrav.length) {
            familyTree = new Tree();
            familyTree.insert(pretrav[prestart]);
            currentSubTree = familyTree.root;
            buildTree(size - 1, prestart + 1, 0);
        }
        else {
            try {
                currentSubTree.insert(pretrav[prestart]);
                currentSubTree = familyTree.lookup(pretrav[prestart]);
                if (posttrav[poststart].equals(pretrav[prestart + 1])) {
                    while (posttrav[poststart].equals(pretrav[prestart + 1])) {
                        buildTree(1, prestart + 1, poststart++);
                        prestart++;
                    }
                    if (posttrav[poststart + 1].equals(currentSubTree.parent.element)) {
                        currentSubTree = currentSubTree.parent.parent;
                        buildTree(findElementInPostTrav(pretrav[prestart + 1]) - (poststart + 1), prestart + 1, poststart + 2);
                    }
                    else {
                        currentSubTree = currentSubTree.parent;
                        buildTree(findElementInPostTrav(pretrav[prestart + 1]) - poststart, prestart + 1, poststart + 1);
                    }
                }
                else {
                    buildTree(findElementInPostTrav(pretrav[prestart + 1]) - poststart, prestart + 1, poststart);
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    /**
     * Main method for program
     * gets user input, sets up pretrav and posttrav arrays
     * gets query input, builds tree, resolves queries
     * prints out level order traversal
     * 
     * @param args command line arguments
     */    
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        String preOrderTraversal = userInput.nextLine();
        String postOrderTraversal = userInput.nextLine();
        Scanner lineScannerPreOrder = new Scanner(preOrderTraversal.replaceAll("[<>,.]", ""));
        ArrayList<String> tempArrayList = new ArrayList<String>();
        while (lineScannerPreOrder.hasNext()) {
            tempArrayList.add(tempArrayList.size(), lineScannerPreOrder.next());
        }
        pretrav = new String[tempArrayList.size()];
        for (int i = 0; i < tempArrayList.size(); i++) {
            pretrav[i] = tempArrayList.get(i);
        }
        Scanner lineScannerPostOrder = new Scanner(postOrderTraversal.replaceAll("[<>,.]", ""));
        tempArrayList = new ArrayList<String>();
        while (lineScannerPostOrder.hasNext()) {
            tempArrayList.add(tempArrayList.size(), lineScannerPostOrder.next());
        }
        posttrav = new String[tempArrayList.size()];
        for (int i = 0; i < tempArrayList.size(); i++) {
            posttrav[i] = tempArrayList.get(i);
        }
        buildTree(pretrav.length, 0, 0);
        while (userInput.hasNext()) {
            Scanner queryLineScanner = new Scanner(userInput.nextLine().replaceAll("[?<>,.]", ""));
            String a = queryLineScanner.next();
            String b = queryLineScanner.next();
            try{
                familyTree.lookup(a).markAncestors();
            }
            catch (NullPointerException e) {
                System.out.printf("Can't find element %s\n", a);
                continue;
            }
            Tree.SubTree leastCommonAncestor;
            try{
                leastCommonAncestor = familyTree.lookup(b).findLeastCommonAncestor();
            }
            catch (NullPointerException e) {
                System.out.printf("Can't find element %s\n", b);
                continue;
            }
            String relationship = familyTree.lookup(a).determineRelationship(familyTree.lookup(b), leastCommonAncestor);
            System.out.println(relationship);
            familyTree.root.clearMarks();
            queryLineScanner.close();
        }
        System.out.print(levelOrderTraversal());
        lineScannerPostOrder.close();
        lineScannerPreOrder.close();
        userInput.close();        
    }
    
        /**
     * Custom Tree implementation
     * 
     * @file Tree.java
     * @author Soorya Kumar
     */
    public static class Tree {
        /** root of tree*/
        public SubTree root;
        
        /**
         * Constructor for Tree
         */
        public Tree() {
            this.root = null; 
        }
        
        /**
         * Method to insert element into tree, delegates to Subtree.insert()
         * @param s string of element to insert into tree
         */
        public void insert(String s) {
            if (root == null)
                root = new SubTree(s, null);
            else
                root.insert(s);
        }

        /**
         * Method to find element in tree, returns Subtree of that element
         * 
         * @param s string of element to lookup in tree
         * @return Subtree of element in tree
         */    
        public SubTree lookup(String s) {
            if (root.element.equals(s)) {
                return root;
            }
            else 
                return root.lookup(s);
        }
        
        /**
         * get children of a subtree
         * @return array list of subtrees (children)
         */    
        public ArrayList<SubTree> getChildren() {
            return root.children;
        }
        
        /**
         * Inner class to represent the nodes or building blocks
         * of the Tree object, each node of the Tree is a subtree
         * containing an element, children, parent, and mark
         * 
         * @author Soorya Kumar
         */
        public class SubTree {
            /** element of SubTree */
            public String element;
            /** array list of children of element */
            public ArrayList<SubTree> children;  
            /** parent of element */
            public SubTree parent;
            /** mark of element (used to find least common ancestor) */
            public Boolean mark;
        
            /**
             * Constructor for SubTree
             * @param element element
             * @param parent parent
             */
            public SubTree(String element, SubTree parent) {
                this.element = element;
                this.parent = parent;
                this.children = null;
                this.mark = false;
            }
            
            /**
             * insert element into SubTree
             * @param s element of child SubTree
             */
            public void insert(String s) {
                if (children == null) {
                    this.children = new ArrayList<SubTree>();
                }
                children.add(new SubTree(s, this));
            }
            
            /**
             * lookup element in SubTree
             * @param s element to find in SubTree
             * @return SubTree of found element
             */
            public SubTree lookup(String s) {
                if (children == null) {
                    return null;
                }
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i).element.equals(s)) {
                        return children.get(i);
                    }
                }
                Tree.SubTree st = null;
                for (int i = 0; i < children.size(); i++) {
                    st = children.get(i).lookup(s);
                    if (st != null) {
                        break;
                    }
                }
                return st;
            }
            
            /**
             * Mark ancestors of a SubTree
             */        
            public void markAncestors() {
                this.mark = true;
                if (this.parent != null) {
                    this.parent.markAncestors();
                }
            }

            /**
             * trace back through ancestors to find least common ancestor
             * after having run markAncestors on a different node 
             * @return SubTree of least common ancestor
             */        
            public SubTree findLeastCommonAncestor() {
                if (this.mark) {
                    return this;
                }
                return this.parent.findLeastCommonAncestor();
            }
            
            /**
             * Calculate path to least common ancestor given a SubTree
             * @param st starting SubTree
             * @param leastCommonAncestor previously determined least Common Ancestor
             * @return int path length to least common ancestor
             */        
            public int distanceToLeastCommonAncestor(SubTree st, SubTree leastCommonAncestor) {
                if (st == null || st.element.equals(leastCommonAncestor.element)) {
                    return 0;
                }
                return 1 + distanceToLeastCommonAncestor(st.parent, leastCommonAncestor);
            }

            /**
             * clear all marks in SubTree
             */        
            public void clearMarks() {
                this.mark = false;
                if (this.children != null) {
                    for (int i = 0; i < this.children.size(); i++) {
                        children.get(i).clearMarks();
                    }
                }
            }

            /**
             * determine the relationship between Subtree calling this function and b
             * @param b SubTree to determine relationship with calling SubTree
             * @param leastCommonAncestor least common ancestor between calling SubTree and b
             * @return string representing relationship of calling SubTree and b
             */        
            public String determineRelationship(SubTree b, SubTree leastCommonAncestor) {
                int aPathLength = distanceToLeastCommonAncestor(this, leastCommonAncestor);
                int bPathLength = distanceToLeastCommonAncestor(b, leastCommonAncestor);
                String relationship = null;
                if (aPathLength == 0) {
                    if (bPathLength == 0)
                        return this.element + " is " + b.element + ".";
                    else if (bPathLength == 1)
                        return this.element + " is " + b.element + "'s parent.";
                    else if (bPathLength == 2)
                        return this.element + " is " + b.element + "'s grandparent.";
                    else if (bPathLength == 3)
                        return this.element + " is " + b.element + "'s great-grandparent.";
                    else if (bPathLength > 3) {
                        relationship = this.element + " is " + b.element + "'s ";
                        for (int i = 0; i < bPathLength - 2; i++) {
                            relationship += "great-";
                        }
                        relationship += "grandparent.";
                        return relationship;
                    }
                }
                else if (aPathLength == 1) {
                    if (bPathLength == 0)
                        return this.element + " is " + b.element + "'s child.";
                    else if (bPathLength == 1)
                        return this.element + " is " + b.element + "'s sibling.";
                    else if (bPathLength == 2)
                        return this.element + " is " + b.element + "'s aunt/unlce.";
                    else if (bPathLength >= 2) {
                        relationship = this.element + " is " + b.element + "'s ";
                        for (int i = 0; i < bPathLength - 2; i++) {
                            relationship += "great-";
                        }
                        relationship += "aunt/uncle.";
                        return relationship;
                    }
                }
                else if (aPathLength == 2) {
                    if (bPathLength == 0)
                        return this.element + " is " + b.element + "'s grandchild.";
                    else if (bPathLength == 1)
                        return this.element + " is " + b.element + "'s niece/nephew.";
                }
                if (aPathLength >= 2) {
                    if (aPathLength >= 3 && bPathLength == 0) {
                        relationship = this.element + " is " + b.element + "'s ";
                        for (int i = 0; i < aPathLength - 2; i++) {
                            relationship += "great-";
                        }
                        relationship += "grandchild.";
                        return relationship;    
                    }
                    else if (bPathLength == 1) {
                        relationship = this.element + " is " + b.element + "'s ";
                        for (int i = 0; i < aPathLength - 2; i++) {
                            relationship += "great-";
                        }
                        relationship += "niece/nephew.";
                        return relationship;    
                    }
                    else if (bPathLength >= 2) {
                        relationship = this.element + " is " + b.element + "'s ";
                        relationship += "" + (Math.min(aPathLength, bPathLength) - 1) + "th cousin "; 
                        relationship += "" + Math.abs(aPathLength - bPathLength) + " times removed.";
                        return relationship;    
                    }
                }
                return null;
            }
        }
    }
        /**
     * Custom ArrayList
     * @file ArrayList
     * @author Soorya Kumar
     *
     * @param <E> generic parameter
     */
    public static class ArrayList<E> extends java.util.AbstractList<E> {

        /** initial size of array list */
        private static final int INIT_SIZE = 10;
        /** array managing the elements in the array list */
        private E[] list;
        /** current accessible size of array list */
        private int size;
        
        /**
         * Constructor for ArrayList 
         */
        @SuppressWarnings("unchecked")
        public ArrayList () {
            list = (E[]) new Object[INIT_SIZE];
            size = 0;
        }
        
        /** 
         * add element 
         * 
         * @param index index of element to add
         * @param e element to add
         */
        @SuppressWarnings("unchecked")
        @Override
        public void add(int index, E e) {
            if (e == null) {
                throw new NullPointerException();
            }
            for (int i = 0; i < size; i++) {
                if (e.equals(list[i])) {
                    throw new IllegalArgumentException();
                }
            }
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException();
            }
            
            if (size < list.length) {
                for (int i = size; i > index; i--) {
                    list[i] = list[i - 1];
                }
                list[index] = e;
                size++;
            }
            else if (size == list.length) {
                E[] temp = (E[]) new Object[list.length];
                for (int i = 0; i < list.length; i++) {
                    temp[i] = list[i];
                }
                list = (E[]) new Object[INIT_SIZE * 2];
                for (int i = 0; i < temp.length; i++) {
                    list[i] = temp[i];
                }
                for (int i = size; i > index; i--) {
                    list[i] = list[i - 1];
                }
                list[index] = e;
                size++;
            }
            
        }

        /** 
         * remove element 
         * 
         * @param index index of element to remove
         * @return element that was removed
         */
        @Override
        public E remove(int index) {
            if (list[index] == null) {
                throw new IndexOutOfBoundsException();
            }
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException();
            }
            E ret = list[index];
            if (size > index) {
                for (int i = index; i < size - 1; i++) {
                    list[i] = list[i + 1];
                }
                size--;
            }
            return ret;
            
        }
        
        /** 
         * set element 
         * 
         * @param index index of element to set
         * @param e element to set
         * @return element that was replaced
         */
        @Override
        public E set(int index, E e) {
            if (e == null) {
                throw new NullPointerException();
            }
            for (int i = 0; i < list.length; i++) {
                if (e.equals(list[i])) {
                    throw new IllegalArgumentException();
                }
            }
            if (index < 0 || index > size - 1) {
                throw new IndexOutOfBoundsException();
            }
            E ret = list[index];
            list[index] = e;
            return ret;
        }
        
        /** 
         * get element
         * @param index index of element to get
         * @return element at the given index
         */
        @Override
        public E get(int index) {
            if (index < 0 || index > size - 1) {
                throw new IndexOutOfBoundsException();
            }
            return list[index];
        }
        
        /**
         * returns size of array list
         * 
         * @return current accessible size 
         */
        @Override
        public int size() {
            return size;
        }
    }
}