package HW4;
import java.util.*;

public class AVLTree<E extends Comparable<E>> {
    public static class Node<E extends Comparable<E>> {
        protected E element;
        protected Node<E> left;
        protected Node<E> right;
        protected Node<E> parent;
        protected int height;
        protected int size;

        public Node(E e) {
            this(e, null);
        }

        public Node(E e, Node<E> parent) {
            element = e;
            this.parent = parent;
            size = getSize(this);
        }

        public int getSize(Node<E> a) {
			size = 1; 
			if (a.left != null)
				size += getSize((Node<E>) a.left);
			if (a.right != null)
				size += getSize((Node<E>) a.right);
			return size;
		}
    }

    /** Get the number of nodes in the tree */
    public int getSize() {
      return size;
    }

    /** Returns the root of the tree */
    public Node<E> getRoot() {
      return root;
    }

    protected Node<E> root;
    protected int size = 0;

    /** Create a default AVL tree */
    public AVLTree() {}

    /** Create an AVL tree from an array of objects */
    public AVLTree(E[] objects) {
        for (int i = 0; i < objects.length; i++)
            insert(objects[i]);
    }

    protected Node<E> createNewNode(E e) {
        return new Node<E>(e);
    }

    protected Node<E> createNewNode(E e, Node<E> parent) {
        return new Node<E>(e, parent);
    }

    public boolean search(E e) {
        Node<E> current = root; // Start from the root
        while (current != null) {
            if (e.compareTo(current.element) < 0) {
                current = current.left;
            } else if (e.compareTo(current.element) > 0) {
                current = current.right;
            } else // element matches current.element
                return true; // Element is found
        }
    
        return false;
    }
	
    /** Insert an element and rebalance if necessary */
    public boolean insert(E e) {
        boolean successful = true; //Assume inserted successfully

        if (root == null) {
            root = createNewNode(e); // Create a new root
        } else {
            // Locate the parent node
            Node<E> current = root;
            Node<E> p = null;

            while (current != null) {
                if (e.compareTo(current.element) < 0) {
                    p = current;
                    current = current.left;
                } else if (e.compareTo(current.element) > 0) {
                    p = current;
                    current = current.right;
                } else {
                    successful = false; //duplicate
                    break;
                }       
            }
    
            // Create the new node and attach it to the parent node
            if (e.compareTo(p.element) < 0 && successful)
                p.left = createNewNode(e, p);
            else if(successful)
                p.right = createNewNode(e, p);
        }

        size++;

        if (!successful)
            return false; // e is already in the tree
        else {
            balancePath(e); // Balance from e to the root if necessary
            updateSize(root);
        }
           
        return true; // e is inserted
    }

    /** Balance the nodes in the path from the specified
    * node to the root if necessary
    */
    private void balancePath(E e) {
        ArrayList<Node<E>> path = path(e);// from root to e
        for (int i = path.size() - 1; i >= 0; i--) {
            Node<E> A = path.get(i);
            updateHeight(A);

            Node<E> parentOfA = (A == root) ? null : path.get(i - 1);
      
            switch(balanceFactor(A)) {
                case -2:
                    if (balanceFactor(A.left) <= 0) {
                        balanceLL(A, parentOfA); // Perform LL rotation
                    } else {
                        balanceLR(A, parentOfA); // Perform LR rotation
                    }

                    break;
                case +2:
                    if (balanceFactor(A.right) >= 0) {
                        balanceRR(A, parentOfA); // Perform RR rotation
                    } else {
                        balanceRL(A, parentOfA); // Perform RL rotation
                    }
            }
        }
    }

    /** Update the height of a specified node */
    private void updateHeight(Node<E> node) {
        if (node.left == null && node.right == null) // node is a leaf
            node.height = 0;
         else if (node.left == null) // node has no left subtree
            node.height = 1 + node.right.height;            
        else if (node.right == null) // node has no right subtree
            node.height = 1 + node.left.height;
        else
            node.height = 1 + Math.max(node.right.height, node.left.height);
    }

    /** Return the balance factor of the node */
    private int balanceFactor(Node<E> node) {
        if (node.right == null) // node has no right subtree
            return -node.height;
        else if (node.left == null) // node has no left subtree
            return +node.height;
        else
            return node.right.height - node.left.height;
    }

    /** Balance LL */
    private void balanceLL(Node<E> A, Node<E> parentOfA) {
        Node<E> B = A.left; // A is left-heavy and B is left-heavy
        if (A == root) {
            root = B;
        } else {
            if (parentOfA.left == A) {
                parentOfA.left = B;
            } else {
                parentOfA.right = B;
            }
        }

        A.left = B.right; // Make T2 the left subtree of A
        B.right = A; // Make A the left child of B
        updateHeight(A);
        updateHeight(B);
    }

    /** Balance RR */
    private void balanceRR(Node<E> A, Node<E> parentOfA) {
        Node<E> B = A.right; // A is right-heavy and B is right-heavy
        if (A == root) {
            root = B;
        } else {
            if (parentOfA.left == A) {
                parentOfA.left = B;
            } else {
                parentOfA.right = B;
            }
        }
      
        A.right = B.left; // Make T2 the right subtree of A
        B.left = A;
        updateHeight(A);
        updateHeight(B);
    }

    /** Balance LR */
    private void balanceLR(Node<E> A, Node<E> parentOfA) {
        Node<E> B = A.left; // we know that A is left-heavy
        Node<E> C = B.right; // we know that B is right-heavy
        if (A == root) {
            root = C;
        } else {
            if (parentOfA.left == A) {
                parentOfA.left = C;
            } else {
                parentOfA.right = C;
            }
        }
      
        A.left = C.right; // Make T3 the left subtree of A
        B.right = C.left; // Make T2 the right subtree of B
        C.left = B;
        C.right = A;
        // Adjust heights
        updateHeight(A);
        updateHeight(B);
        updateHeight(C);
    }

    /** Balance RL */
    private void balanceRL(Node<E> A, Node<E> parentOfA) {
        Node<E> B = A.right; // we know that A is right-heavy
        Node<E> C = B.left; // we know that B is left-heavy
        
        if (A == root) {
            root = C;
        } else {
            if (parentOfA.left == A) {
                parentOfA.left = C;
            } else {
                parentOfA.right = C;
            }
        }

        A.right = C.left; // Make T2 the right subtree of A
        B.left = C.right; // Make T3 the left subtree of B
        C.left = A;
        C.right = B;
        // Adjust heights
        updateHeight(A);
        updateHeight(B);
        updateHeight(C);
    }

    /** Delete an element from the binary tree.
     * Return true if the element is deleted successfully
     * Return false if the element is not in the tree */
    public boolean delete(E element) {
        if (root == null)
            return false; // Element is not in the tree
      
        // Locate the node to be deleted and also locate its parent node
        Node<E> parent = null;
        Node<E> current = root;
      
        while (current != null) {
            if (element.compareTo(current.element) < 0) {
                parent = current;
                current = current.left;
            } else if (element.compareTo(current.element) > 0) {
                parent = current;
                current = current.right;
            } else
                break; // Element is in the tree pointed by current
        }
      
        if (current == null)
            return false; // Element is not in the tree
            // Case 1: current has no left children
        
        if (current.left == null) {
            // Connect the parent with the right child of the current node
            if (parent == null) {
                root = current.right;
            } else {
                if (element.compareTo(parent.element) < 0)
                    parent.left = current.right;
                else
                    parent.right = current.right;
                    
                // Balance the tree if necessary
                balancePath(parent.element);
            }
        } else {
            // Case 2: The current node has a left child
            // Locate the rightmost node in the left subtree of
            // the current node and also its parent
            Node<E> parentOfRightMost = current;
            Node<E> rightMost = current.left;
            while (rightMost.right != null) {
                parentOfRightMost = rightMost;
                rightMost = rightMost.right; // Keep going to the right
            }

            // Replace the element in current by the element in rightMost
            current.element = rightMost.element;
            
            // Eliminate rightmost node
            if (parentOfRightMost.right == rightMost)
                parentOfRightMost.right = rightMost.left;
            else
                // Special case: parentOfRightMost is current
                parentOfRightMost.left = rightMost.left; 

            // Balance the tree if necessary
            balancePath(parentOfRightMost.element);
        }
          
        updateSize(root);
        size--;
        return true; // Element deleted
    }

    public ArrayList<Node<E>> path(E e) {
        ArrayList<Node<E>> list = new ArrayList<>();
        Node<E> current = root; // Start from the root
        while (current != null) {
            list.add(current); // Add the node to the list
        
            if (e.compareTo(current.element) < 0) {
                current = current.left;
            } else if (e.compareTo(current.element) > 0) {
                current = current.right;
            } else
                break;
            }
      
        return list; // Return an array list of nodes
    }

    //Returns list of paths to leaf nodes
    public void collectBranches() {
        ArrayList<ArrayList<Node<E>>> paths = new ArrayList<ArrayList<Node<E>>>();
        
        ArrayList<Node<E>> leaves = new ArrayList<Node<E>>();
        ArrayList<Node<E>> nodes = new ArrayList<Node<E>>();
        
        if(root != null) nodes.add(root);
        while(nodes.size() > 0) {
            Node<E> current = nodes.remove(nodes.size() - 1);
    
            if(current.left == null && current.right == null) {
                leaves.add(current);
            }
    
            if(current.left != null) {
                nodes.add(current.left);
            }
    
            if(current.right != null) {
                nodes.add(current.right);
            }
        }
    
        for(int i=0; i<leaves.size(); i++) {
            ArrayList<Node<E>> path = new ArrayList<Node<E>>();
            Node<E> leaf = leaves.remove(0);
    
            while(leaf != null) {
                path.add(0, leaf);
                leaf = leaf.parent;
            }
    
            paths.add(path);
        }
    
        for(int i=0; i<paths.size(); i++) {
            for(int j=0; j<paths.get(i).size(); j++) {
                System.out.print(paths.get(i).get(j).element + " ");
            }

            System.out.println();
        }
    }

    //Find kth smallest element
    public E find(int k) {
        if(k < 1 || k > size) {
            return null;
        }

        return find(k, root);
    }

    //Helper function to return kth smallest element with specified root
    private E find(int k, Node<E> currentRoot) {
        if(currentRoot.left == null) {
            if(k == 1) {
                return currentRoot.element;
            }
            
            if(k == 2) {
                return currentRoot.right.element;
            }
        }
        
        if(k <= currentRoot.left.size) {
            return find(k, currentRoot.left);
        } 
        
        if(k == currentRoot.left.size + 1) {
            return currentRoot.element;
        } 

        return find(k - currentRoot.left.size - 1, currentRoot.right);
    }

    private int updateSize(Node<E> currentRoot) {
        if(currentRoot != null) {
            int leftUpdated = updateSize(currentRoot.left);
            int rightUpdated = updateSize(currentRoot.right);
            currentRoot.size = 1 + leftUpdated + rightUpdated;
            return currentRoot.size;
        }

        return 0;
    }

    // Inner class InorderIterator in outer class BST
    private class InorderIterator implements Iterator<E> {
    
        // Store the elements in a list
        private ArrayList<E> list = new ArrayList<>();
        private int current = 0; // Point to the current element in list
        
        public InorderIterator() {
            inorder(); // Traverse binary tree and store elements in list
        }

        /** Inorder traversal from the root*/
        private void inorder() {
            inorder(root);
        }

        /** Inorder traversal from a subtree */
        private void inorder(Node<E> root) {
            if (root == null) return;
            inorder(root.left);
            list.add(root.element);
            inorder(root.right);
        }

        @Override /** More elements for traversing? */
        public boolean hasNext() {
          if (current < list.size())
                return true;

          return false;
        }

        @Override /** Get the current element and move to the next */
        public E next() {
            return list.get(current++);
        }

        @Override /** Remove the current element */
        public void remove() {
            AVLTree.this.delete(list.get(current)); // Delete the current element
            list.clear(); // Clear the list
            inorder(); // Rebuild the list
        }
    }

    public Iterator<E> iterator() {
        return new InorderIterator();
    }

    public abstract class AbstractTree<E> implements Tree<E> {
        @Override /** Preorder traversal from the root */
        public void preorder() {}
  
        @Override /** Inorder traversal from the root*/
        public void inorder() {}
  
        @Override /** Postorder traversal from the root */
        public void postorder() {}
  
        @Override /** Return true if the tree is empty */
        public boolean isEmpty() {
            return getSize() == 0;
        }
    }

    public interface Tree<E> extends Iterable<E> {
        /** Return true if the element is in the tree */
        public boolean search(E e);
  
        /** Insert element o into the binary tree
        * Return true if the element is inserted successfully */
        public boolean insert(E e);
  
        /** Delete the specified element from the tree
        * Return true if the element is deleted successfully */
        public boolean delete(E e);
  
        /** Preorder traversal from the root */
        public void preorder();
  
        /** Inorder traversal from the root*/
        public void inorder();
  
        /** Postorder traversal from the root */
        public void postorder();
  
        /** Get the number of nodes in the tree */
        public int getSize();
  
        /** Return true if the tree is empty */
        public boolean isEmpty();
    } 
}

 
