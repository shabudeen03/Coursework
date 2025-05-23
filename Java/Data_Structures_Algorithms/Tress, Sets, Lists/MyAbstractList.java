package HW4;
import java.util.*;

public abstract class MyAbstractList<E> implements MyList<E> {
    protected int size = 0; // The size of the list

    /** Create a default list */
    protected MyAbstractList() {}

    /** Create a list from an array of objects */
    protected MyAbstractList(E[] objects) {
        for (int i = 0; i < objects.length; i++)
        add(objects[i]);
    }

    /** Add a new element at the end of this list */
    public void add(E e) {
        add(size, e); // add an element at index size
    }

    /** Return true if this list contains no elements */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Return the number of elements in this list */
    public int size() {
        return size;
    }

    /** Remove the first occurrence of the element from this list.
    * Shift any subsequent elements to the left.
    * Return true if the element is removed. */
    public boolean remove(E e) {
        int i = indexOf(e);

        if (i >= 0) {
            remove(i);
            return true;
        } else {
            return false;
        }
    }

    public boolean addAll(MyList<E> otherList) {
        Iterator<E> iterator = otherList.iterator();
        boolean changed = false;

        while(iterator.hasNext()) {
            E obj = iterator.next();
            if(! this.contains(obj)) {
                this.add(obj);
                changed = true;
            }
        }

        return changed;
    }

    public boolean removeAll(MyList<E> otherList) {
        int initialSize = size;
        Iterator<E> iterator = otherList.iterator();

        //remove all shared values
        while(iterator.hasNext()) {
            E obj = iterator.next();
            if(this.contains(obj)) {
                this.remove(obj);
            }         
        }

        //return whether size has changed or not
        return initialSize != size;
    }

    public boolean retainAll(MyList<E> otherList) {
        E[] retained = (E[]) (new Object[this.size]);

        int initialSize = size;
        int i=0;
        Iterator<E> iterator = this.iterator();

        //keep track of shared values b/w this and other list
        while(iterator.hasNext()) {
            E obj = iterator.next();

            if(otherList.contains(obj)) {
                retained[i] = obj;
                i++;
            }

            this.remove(obj);
        }

        for(int j=0; j<i; j++) {
            this.add(retained[j]);
        }

        //return whether size has changed or not
        return size != initialSize;
    }
}
