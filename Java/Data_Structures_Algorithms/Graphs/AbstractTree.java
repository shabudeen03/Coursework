package HW5;

public abstract class AbstractTree<E> implements Tree<E> {

    @Override /** Preorder traversal from the root */
    public void preorder() {
    }
  
    @Override /** Inorder traversal from the root*/
    public void inorder() {
    }
  
    @Override /** Postorder traversal from the root */
    public void postorder() {
    }
  
    @Override /** Return true if the tree is empty */
    public boolean isEmpty() {
      return getSize() == 0;
    }  
  }
