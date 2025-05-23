package HW3;

public class MinHeapSort {
    public static void main(String[] args) {
        Integer[] list = {11, 5, 3, 19, 1, 22};
        minHeapSort(list);

        for(int i:list) System.out.print(i + " ");
        System.out.println();
    }

    public static <E extends Comparable<E>> void minHeapSort(E[] list) {
        MinHeap<E> heap = new MinHeap<E>(list);

        for(int i=0; i<list.length; i++) {
            list[i] = heap.remove();
        }
    }
}
