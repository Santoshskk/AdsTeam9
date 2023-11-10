package spotifycharts;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion or bubble sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * @param items
     * @param comparator
     * @return the items sorted in place
     */

    //implemented sorting algoritmes. first made bubble sort. also made selection sort for fun
    public List<E> selInsBubSort(List<E> items, Comparator<E> comparator) {
       return bubbleSort(items, comparator);
    }


    /**
     * Bubble sort
     * Worst-case complexity: O(n²)
     * best case complexity: O(n) this is when the list is already sorted
     * this algoritme compares each pair of items and swaps when in the wrong order.
     *
     * @param items
     * @param comparator
     * @return
     */
    private List<E> bubbleSort(List<E> items, Comparator<E> comparator) {
        boolean doItAgain = false;
        int limit = items.size();
        for (int i = 0; i < limit - 1; i++) {
            E thisValue = items.get(i);
            E nextValue = items.get(i + 1);
            if (comparator.compare(thisValue, nextValue) > 0) {
                // Swap elements
                items.set(i, nextValue);
                items.set(i + 1, thisValue);
                doItAgain = true; // A swap occurred so check again
            }
        }
        //recursively call the function to continue sorting
        if (doItAgain) {
            selInsBubSort(items, comparator);
        }
        return items;
    }


    /**
     * Worst case complexity: O(n²)
     * Best case complexity: O(n²)
     *
     * this Algoritme looks for the smallest item in the list and places it at the start.
     * This process is repeated for the remainder of the list
     *
     * so it is O(N x N) which is OO(n²)
     * @param items
     * @param comparator
     * @return
     */
    public List<E> selectionSort(List<E> items, Comparator<E> comparator) {

        for (int i = 0; i < items.size(); i++) {
            int currentMinIndex = i;
            for(int x = currentMinIndex + 1; x < items.size(); x++) {
                if (comparator.compare(items.get(x), items.get(currentMinIndex)) < 0) {
                    currentMinIndex = x;
                }
            }
            if (currentMinIndex != i) {
                swap(items, i, currentMinIndex);
            }
        }
        return items;
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     *
     * Worst-Case Complexity: O(n²)
     * Best-Case complexity:  O(n log n)
     * The Big O complexity of Quick Sort varies based on the choice of the pivot.
     * There is no fixed level of complexity for QuickSort
     *
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    //divide and Conquer algoritme
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        if(items.size() < 2) {
            return items;
        }
        return quickSortRecursive(items, 0, items.size() - 1, comparator);
    }
    private List<E> quickSortRecursive(List<E> items, int start, int end, Comparator<E> comparator) {
        if (start < end) {
            int pivotIndex = partition(items, start, end, comparator);
            quickSortRecursive(items, start, pivotIndex - 1, comparator);
            quickSortRecursive(items, pivotIndex + 1, end, comparator);
        }
        return items;
    }
    private int partition(List<E> items, int start, int end, Comparator<E> comparator) {
        E pivotValue = items.get(end);
        int i = start - 1;
        for (int j = start; j < end; j++) {
            if (comparator.compare(items.get(j), pivotValue) <= 0) {
                i++;
                swap(items, i, j);
            }
        }
        swap(items, i + 1, end);
        return i + 1;
    }
    private void swap(List<E> items, int index1, int index2) {
            E startElement = items.get(index1);
            E pivotElement = items.get(index2);
            items.set(index1, pivotElement);
            items.set(index2, startElement);
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     *
     *
     *
     *  Complexity: O(n log m)
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {

        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }

        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        for (int i = numTops-1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection

            //swap item[0] and item[i];
            //this moves item[0] to its designated position
                swap(items, 0, i );

            //the new root may have violated the heap condition
            // Repair the heap condition on the remaining heap of size i
            heapSink(items, i, reverseComparator);
        }

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        int i = heapSize - 1;  // Start with the last item in the heap
        E currentItem = items.get(i);

        while (i > 0) {
            int parentIndex = (i - 1) / 2;

            // Check if the current item is greater than or equal to its parent
            if (comparator.compare(currentItem, items.get(parentIndex)) >= 0) {
                break;
            }

            // Swap the current item with its parent
            items.set(i, items.get(parentIndex));
            i = parentIndex;  // Move to the parent position
        }

        // Place the currentItem in its correct position in the heap
        items.set(i, currentItem);
    }

    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
        /*


    Index Calculations for 0-based index heap:
    - Left child index  = 2 * parent_index + 1
    - Right child index = 2 * parent_index + 2
    - Parent index = (child_index - 1) / 2
    */
    /*
    In a max heap, the value of each node is greater than or equal to the values of its children.
    In a min heap, the value of each node is less than or equal to the values of its children.
     */

    /*
      Binary Min-Heap Zero-Based Indexes: value in ( ) are the indexes and the number before that the
    value of the node

                  1(0)
                 /    \
              2(1)    3(2)
              /  \    /  \
           5(3) 6(4) 7(5) 8(6)

    Array: [1, 2, 3, 5, 6, 7, 8]
     */
    protected void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        //sink items[0] down the heap until
        //2*i+1>=heapSize || (items[i] <= items[2*i+1] && items[i] <= items[2*i+2])

        int i = 0;
        //2*i+1>=heapSize -- this condition
        // checks if the left child of the current node exists within the bounds of the heap.
        while (2 * i + 1 < heapSize) {
            int leftChildIndex = 2 * i + 1;
            int rightChildIndex = 2 * i + 2;
            int smallerChildIndex = leftChildIndex;

            // Compare the left child with the right child if it exists and is smaller
            if (rightChildIndex < heapSize && comparator.compare(items.get(leftChildIndex), items.get(rightChildIndex)) > 0) {
                smallerChildIndex = rightChildIndex;
            }

            // If the current item is smaller or equal to the smaller child, stop traverse the list
            if (comparator.compare(items.get(i), items.get(smallerChildIndex)) <= 0) {
                break;
            }

            // Swap the current item with the smaller child to create a new root
            E temp = items.get(i);
            items.set(i, items.get(smallerChildIndex));
            items.set(smallerChildIndex, temp);

            i = smallerChildIndex;
        }
    }
}
