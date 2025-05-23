package HW3;
import java.util.*;

public class GenericHeapUsingComparator<E> implements Cloneable {
    static class IntegerComparator implements Comparator<Integer> {
        public int compare(Integer e1, Integer e2) {
            return e1 - e2;
        }
    }

    private ArrayList<E> list = new ArrayList<E>();
    private Comparator<? super E> comparator;

    public GenericHeapUsingComparator() {}

    public GenericHeapUsingComparator(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public GenericHeapUsingComparator(E[] list2, Comparator<? super E> comparator) {
        this.comparator = comparator;

        for(int i=0; i<list2.length; i++) {
            this.add(list2[i]);
        }
    }

    public void add(E newObject) {
        list.add(newObject);
        int currentIdx = list.size() - 1;

        while(currentIdx > 0) {
            int parentIdx = (currentIdx - 1) / 2;

            E temp = list.get(parentIdx);
            E obj = list.get(currentIdx);
            
            if(comparator.compare(temp, obj) < 0) {
                list.set(currentIdx, temp);
                list.set(parentIdx, obj);
            } else {
                break;
            }

            currentIdx = parentIdx;
        }
    }

    public E remove() {
        if (list.size() == 0) return null;
        E removedObject = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.remove(list.size() - 1);
        int currentIndex = 0;
        while (currentIndex < list.size()) {
        int leftChildIndex = 2 * currentIndex + 1;
        int rightChildIndex = 2 * currentIndex + 2;
        // Find the maximum between two children
        if (leftChildIndex >= list.size())
        break; // The tree is a heap
        int maxIndex = leftChildIndex;
        if (rightChildIndex < list.size())
        if (comparator.compare(list.get(maxIndex), 
        list.get(rightChildIndex)) < 0)
        maxIndex = rightChildIndex;
        
        // Swap if the current node is less than the maximum
        if (comparator.compare(list.get(currentIndex), 
        list.get(maxIndex)) < 0) {
        E temp = list.get(maxIndex);
        list.set(maxIndex, list.get(currentIndex));
        list.set(currentIndex, temp);
        currentIndex = maxIndex;
        }
        else
        break; // The tree is a heap
        }
        return removedObject;
    }

    public int getSize() {
        return list.size();
    }

    public Object clone() {
        GenericHeapUsingComparator<E> heap = new GenericHeapUsingComparator<>(comparator);
        
        ArrayList<E> clone = new ArrayList<E>();

        for(E o:list) {
            clone.add(o);
        }

        heap.list = clone;
        return heap;
    }

    public boolean equals(Object other) {
        GenericHeapUsingComparator<? super E> thisCloned = (GenericHeapUsingComparator<? super E>) this.clone();
        GenericHeapUsingComparator<? super E> otherCloned = (GenericHeapUsingComparator<? super E>)((GenericHeapUsingComparator<? super E>) other).clone();

        if(list.size() == otherCloned.getSize()) {
            for(int i=0; i<list.size(); i++) {
                E thisObj = (E) thisCloned.remove();
                E otherObj = (E) otherCloned.remove();

                if(! thisObj.equals(otherObj)) {
                    return false;
                }
            }

            return true;
        } 

        return false;
    }

    public ArrayList<E> getList() {
        return list;
    }

    public static void main(String[] args) {
        Integer[] list = {3, 5, 1, 19, 11};
        GenericHeapUsingComparator<Integer> heap = new GenericHeapUsingComparator<Integer>(list, new IntegerComparator());
        heap.add(22);
        System.out.println(heap.getList());

        Integer[] list2 = {11, 22, 3, 19, 1, 5};
        GenericHeapUsingComparator<Integer> heap2 = new GenericHeapUsingComparator<Integer>(list2, new IntegerComparator());
        System.out.println(heap2.getList());

        System.out.println(heap.equals(heap2));

        GenericHeapUsingComparator<Integer> heap3 = (GenericHeapUsingComparator<Integer>)heap.clone();
        System.out.println(heap.equals(heap3));
        System.out.println(heap == heap3);
    }
}
