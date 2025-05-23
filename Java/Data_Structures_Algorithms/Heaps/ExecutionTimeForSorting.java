package HW3;
import java.util.*;

public class ExecutionTimeForSorting {
    public static void main(String[] args) {
        System.out.println("Array Size\tSelectionSort\tBubbleSort\tMergeSort\tQuickSort\tHeapSort\tRadixSort");
        System.out.println("50,000\t\t" + getSelection(50000) + "\t\t" + getBubble(50000) + "\t\t" + getMerge(50000) + "\t\t" + getQuick(50000) + "\t\t" + getHeap(50000) + "\t\t" + getRadix(50000));
        System.out.println("100,000\t\t" + getSelection(100000) + "\t\t" + getBubble(100000) + "\t\t" + getMerge(100000) + "\t\t" + getQuick(100000) + "\t\t" + getHeap(100000) + "\t\t" + getRadix(100000));
        System.out.println("250,000\t\t" + getSelection(250000) + "\t\t" + getBubble(250000) + "\t\t" + getMerge(250000) + "\t\t" + getQuick(250000) + "\t\t" + getHeap(250000) + "\t\t" + getRadix(250000));
        System.out.println("500,000\t\t" + getSelection(500000) + "\t\t" + getBubble(500000) + "\t\t" + getMerge(500000) + "\t\t" + getQuick(500000) + "\t\t" + getHeap(500000) + "\t\t" + getRadix(500000));
        System.out.println("1,000,000\t\t" + getSelection(1000000) + "\t\t" + getBubble(1000000) + "\t\t" + getMerge(1000000) + "\t\t" + getQuick(1000000) + "\t\t" + getHeap(1000000) + "\t\t" + getRadix(1000000));

        // System.out.println("50,000\t\t" + getMerge(50000) + "\t\t" + getQuick(50000) + "\t\t" + getHeap(50000) + "\t\t" + getRadix(50000));
        // System.out.println("100,000\t\t" + getMerge(100000) + "\t\t" + getQuick(100000) + "\t\t" + getHeap(100000) + "\t\t" + getRadix(100000));
        // System.out.println("250,000\t\t" + getMerge(250000) + "\t\t" + getQuick(250000) + "\t\t" + getHeap(250000) + "\t\t" + getRadix(250000));
        // System.out.println("500,000\t\t" + getMerge(500000) + "\t\t" + getQuick(500000) + "\t\t" + getHeap(500000) + "\t\t" + getRadix(500000));
        // System.out.println("1,000,000\t" + getMerge(1000000) + "\t\t" + getQuick(1000000) + "\t\t" + getHeap(1000000) + "\t\t" + getRadix(1000000));
    }

    public static Integer[] generateList(int num) {
        Integer[] list = new Integer[num];

        for(int i=0; i<num; i++) {
            list[i] = (Integer) (int) (Math.random() * num);
        }

        return list;
    }

    public static int getSelection(int num) {
        Integer[] list = generateList(num);
        
        return selectionSort(list);
    }

    public static int getBubble(int num) {
        Integer[] list = generateList(num);
        
        return bubbleSort(list);
    }

    public static int getMerge(int num) {
        Integer[] list = generateList(num);
        
        return mergeSort(list);
    }

    public static int getQuick(int num) {
        Integer[] list = generateList(num);
       
        return quicksort(list);
    }

    public static int getHeap(int num) {
        Integer[] list = generateList(num);
        
        return heapSort(list);
    }

    public static int getRadix(int num) {
        Integer[] list = generateList(num);
        long startTime = System.currentTimeMillis();

        for(int i=0; i<10; i++) {
            radix = i;
            bucketSort(list);
        }

        long endTime = System.currentTimeMillis();

        return (int) (endTime - startTime);
    }

    public static int selectionSort(Integer[] list) {
        long startTime = System.currentTimeMillis();

        for(int i=0; i<list.length - 1; i++) {
            int minIdx = i;

            for(int j=i+1; j<list.length; j++) {
                if(list[j] < list[i]) {
                    minIdx = j;
                }
            }

            if(minIdx != i) {
                int temp = list[i];
                list[i] = list[minIdx];
                list[minIdx] = temp;
            }
        }

        long endTime = System.currentTimeMillis();
        return (int) (endTime - startTime);
    }

    public static int bubbleSort(Integer[] list) { 
        long startTime = System.currentTimeMillis();
        //Best Case: Already Sorted: Only need to iterate once to see, which is n-1 operations so O(n)
        //Worst Case: Iterate every single time: (n-1) + (n-2) + ... + 2 + 1 = n(n-1) / 2 --> O(n^2)
        boolean nextPassNeeded = true;

        for(int i=1; i<list.length && nextPassNeeded; i++) {
            nextPassNeeded = false; //Array may be sorted

            for(int j=0; j<list.length - i; j++) {
                if(list[j] > list[j + 1]) { //If this never happens at all, list is already sorted
                    nextPassNeeded = true;

                    int temp = list[j];
                    list[j] = list[j+1];
                    list[j+1] = temp;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        return (int) (endTime - startTime);
    }

    public static int mergeSort(Integer[] list) {
        long startTime = System.currentTimeMillis();
        //mergeTime --> Takes n-1 at most comparisons of 2 sublists, n moves to move elements to temp array
        //T(n) = T(n/2) + T(n/2) + mergeTime = 2T(n/2) + 2n - 1 --> O(nlogn)


        if(list.length > 1) {
            Integer[] firstHalf = new Integer[list.length / 2];
            System.arraycopy(list, 0, firstHalf, 0, list.length / 2);
            mergeSort(firstHalf);

            Integer[] secondHalf = new Integer[list.length - (list.length / 2)];
            System.arraycopy(list, list.length / 2, secondHalf, 0,secondHalf.length);
            mergeSort(secondHalf);

            merge(firstHalf, secondHalf, list);
        }

        long endTime = System.currentTimeMillis();
        return (int) (endTime - startTime);
    }

    public static void merge(Integer[] list1, Integer[] list2, Integer[] temp) {
        int c1 = 0, c2 = 0, c3 = 0;

        while(c1 < list1.length && c2 < list2.length) {
            if(list1[c1] < list2[c2]) {
                temp[c3++] = list1[c1++];
            } else {
                temp[c3++] = list2[c2++];
            }
        }

        while(c1 < list1.length) {
            temp[c3++] = list1[c1++];
        }

        while(c2 < list2.length) {
            temp[c3++] = list2[c2++];
        }
    }

    public static int quicksort(Integer[] list) {
        long startTime = System.currentTimeMillis();
        //Worst Case: array divided into subarray of one less element --> n-1 + n-2 + ... + 2 + 1 = O(n^2)
        //Best Case: Array divided into 2 subarrays of equal size --> T(n) = 2T(n/2) + n --> O(nlogn)
        //Average Case: The sizes of the partitioned parts is about the same size so it's also O(nlogn)
        //More Space Efficient than merge sort, worse best case but same average case runtime
        quicksort(list, 0, list.length - 1);
        long endTime = System.currentTimeMillis();
        return (int) (endTime - startTime);
    }

    public static void quicksort(Integer[] list, int first, int last) {
        if(last > first) {
            int pivotIndex = partition(list, first, last);
            quicksort(list, first, pivotIndex - 1);
            quicksort(list, pivotIndex + 1, last);
        }
    }

    public static int partition(Integer[] list, int first, int last) {
        int pivot = list[first];
        int low = first + 1;
        int high = last;

        while(high > low) {
            while(low <= high && list[low] <= pivot) {
                low++;
            }

            while(low <= high && list[high] > pivot) {
                high --;
            }

            if(high > low) {
                int temp = list[high];
                list[high] = list[low];
                list[low] = temp;
            }
        }

        while(high > first && list[high] >= pivot)
            high--;

        if(pivot > list[high]) {
            list[first] = list[high];
            list[high] = pivot;
            return high;
        } else 
            return first;
    }

    public static int heapSort(Integer[] list) {
        long startTime = System.currentTimeMillis();
        Heap<Integer> heap = new Heap<Integer>((Integer[])list);

        for(int i=list.length - 1; i>=0; i--) {
            list[i] = heap.remove();
        }

        long endTime = System.currentTimeMillis();
        return (int) (endTime - startTime);
    }

    static int t = 10;
    public static void bucketSort(Integer[] list) {
    ArrayList<Integer>[] bucket =new ArrayList[t+1];
    // Distribute the elements from list to buckets
        for (int i = 0; i < list.length; i++) {
        // Assume element has the getKey() method
            int key = getKey(list[i]);
            if (bucket[key] == null)
                bucket[key] = new ArrayList<Integer>();
            bucket[key].add(list[i]);
        }
        
        // Now move the elements from the buckets back to list
        int k = 0; // k is an index for list
        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] != null) {
                for (int j = 0; j < bucket[i].size(); j++)
                    list[k++] = (int)(bucket[i].get(j));
            }
        }
    }
    
    static int radix = 1;
    public static int getKey(int n) {
        for(int i=0; i<radix; i++)
            n = n / 10;
        
            return n % 10;
    }
}
