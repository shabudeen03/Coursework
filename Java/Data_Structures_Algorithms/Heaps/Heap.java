package HW3;
import java.util.*;

public class Heap<E extends Comparable<E>> implements Cloneable {
    private ArrayList<E> list = new ArrayList<E>();

    public Heap() {}
    public Heap(E[] objects) {
        for(E obj:objects) {
            add(obj);
        }
    }

    public int getSize() {
        return list.size();
    }

    public void add(E obj) {
        list.add(obj);

        if(list.size() > 1) {
            boolean continueLooping = true;
            int currIdx = list.size() - 1;
            int parenIdx;

            while(continueLooping) {
                parenIdx = (currIdx - 1) / 2;

                if(list.get(currIdx).compareTo(list.get(parenIdx)) > 0) {
                    list.set(currIdx, list.set(parenIdx, list.get(currIdx)));
                } else {
                    continueLooping = false;
                }

                currIdx = parenIdx;
            }
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
        if (list.get(maxIndex).compareTo(
        list.get(rightChildIndex)) < 0)
        maxIndex = rightChildIndex;
        
        // Swap if the current node is less than the maximum
        if (list.get(currentIndex).compareTo(
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

    @Override
    public Object clone() {
        Heap<E> heap = new Heap<E>();

        ArrayList<E> clone = new ArrayList<E>();

        for(E o:list) {
            clone.add(o);
        }

        heap.list = clone;
           
        return heap;
    }

    
    @Override
    public boolean equals(Object o) {
        Heap<E> thisCloned = (Heap<E>)this.clone();
        Heap<E> otherCloned = (Heap<E>)((Heap<E>)o).clone();

        System.out.println(thisCloned.getList());
        System.out.println(otherCloned.getList());


        if(list.size() == otherCloned.getSize()) {
            for(int i=0; i<list.size(); i++) {
                E thisObj = thisCloned.remove();
                E otherObj = otherCloned.remove();

                if(thisObj.compareTo(otherObj) != 0) {
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
        Heap<Integer> heap = new Heap<Integer>(list);
        heap.add(22);
        System.out.println(heap.getList());

        // Heap<Integer> heap3 = heap.clone();
        // System.out.println(heap3.getList());

        Integer[] list2 = {11, 22, 3, 19, 1, 5};
        Heap<Integer> heap2 = new Heap<Integer>(list2);
        System.out.println(heap2.getList());

        // for(int i=0; i<5; i++) {
        //     System.out.println(heap.remove() + " " + heap2.remove());
        // }


        System.out.println(heap.equals(heap2));

        Heap<Integer> heap3 = (Heap<Integer>)heap.clone();
        System.out.println(heap.equals(heap3));
        System.out.println(heap == heap3);
    }
}
