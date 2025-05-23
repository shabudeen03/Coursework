package HW3;
import java.util.*;

public class MinHeap<E extends Comparable<E>> {
    private ArrayList<E> list = new ArrayList<E>();

    public MinHeap() {}
    public MinHeap(E[] objects) {
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
            int currIdx = list.size() - 1;
            int parenIdx;
            boolean continueLooping = true;

            while(continueLooping) {
                parenIdx = (currIdx - 1) / 2;

                if(list.get(parenIdx).compareTo(list.get(currIdx)) > 0) {
                    list.set(parenIdx, list.set(currIdx, list.get(parenIdx))); 
                } else {
                    continueLooping = false;
                }

                currIdx = parenIdx;
            }
        }
    }

    public E remove() {
        if(list.size() == 0) {
            return null;
        }

        E removedObj = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.remove(list.size() - 1);

        int currentIdx = 0;
        while(currentIdx < list.size()) {
            int leftChildIndex = 2 * currentIdx + 1;
            int rightChildIndex = 2 * currentIdx + 2;

            if(leftChildIndex >= list.size()) {
                break;
            }

            int minIndex = leftChildIndex;

            if(rightChildIndex < list.size()) {
                if(list.get(minIndex).compareTo(list.get(rightChildIndex)) > 0) {
                    minIndex = rightChildIndex;
                }
            }

            if(list.get(currentIdx).compareTo(list.get(minIndex)) > 0) {
                E temp = list.get(minIndex);
                list.set(minIndex, list.get(currentIdx));
                list.set(currentIdx, temp);
            } else {
                break;
            }
        }

        return removedObj;
    }

    public ArrayList<E> getList() {
        return list;
    }

    public static void main(String[] args) {
        Integer[] list = {3, 5, 1, 19, 11};
        MinHeap<Integer> heap = new MinHeap<Integer>(list);
        heap.add(22);
        System.out.println(heap.getList());

        System.out.println(heap.remove());
        System.out.println(heap.getList());
    }
}
