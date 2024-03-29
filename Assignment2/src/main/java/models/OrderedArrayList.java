package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> sortOrder;   // the comparator that has been used with the latest sort
    protected int nSorted;                       // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given sortOrder comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section



    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> sortOrder) {
        super();
        this.sortOrder = sortOrder;
        this.nSorted = 0;
    }

    public Comparator<? super E> getSortOrder() {
        return this.sortOrder;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.sortOrder = c;
        this.nSorted = this.size();
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        // If an element is added to the sorted section, the sorted section is no longer fully sorted
        if (index < nSorted) {
            nSorted = index; // Update nSorted to the index where the new element was added
        }
    }
    @Override
    public E remove(int index) {
        E removedElement = super.remove(index);
        // If an element is removed from the sorted section, the sorted section remains sorted
        // But if an element is removed from the unsorted section, nSorted should not change
        if (index < nSorted) {
            nSorted--; // Decrease nSorted by 1 as one element is removed from the sorted section
        }
        return removedElement;
    }
    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.sortOrder);
        }
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an sortOrder for the list
        if (this.getSortOrder() != null) {
            return indexOfByIterativeBinarySearch((E)item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.sortOrder
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {


        int index = IterativeBinarySearch(0, nSorted - 1, searchItem);
        // If item is found in the sorted section, return its index
        if (index != -1) {
            return index;
        }
        // Linear search in the unsorted section
        for (int i = nSorted; i < size(); i++) {
            if (this.sortOrder.compare(get(i), searchItem) == 0) {
                return i;
            }
        }
        // Item not found in either section
        return -1;
    }


    private int IterativeBinarySearch(int start, int end, E searchItem) {
        while (start <= end) {
            int mid = start + (end - start) / 2;
            int comparison = this.sortOrder.compare(get(mid), searchItem);
            // Item found
            if (comparison == 0) {
                return mid;
            }
            // Item is on the left side of array
            if (comparison > 0) {
                end = mid - 1;
            }
            // Item is on the right side of array
            else {
                start = mid + 1;
            }
        }
        // Item not found in the sorted section
        return -1;
    }



    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.sortOrder
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */


    //O(log n)
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        // Recursive binary search in the sorted section
        int index = recursiveBinarySearch(0, nSorted - 1, searchItem);
        // If item is found in the sorted section, return its index
        if (index != -1) {
            return index;
        }
        // Linear search in the unsorted section
        for (int i = nSorted; i < size(); i++) {
            if (this.sortOrder.compare(get(i), searchItem) == 0) {
                return i;
            }
        }
        // Item not found in either section
        return -1;
    }


    private int recursiveBinarySearch(int start, int end, E searchItem) {
        if (start <= end) {
            int mid = start + (end - start) / 2;
            //A negative int if the new mid-variable is "less than" the searchItem.
            //Zero if they are equal
            //A positive integer if the new mid-variable is "greater than" the searchItem.
            int comparison = this.sortOrder.compare(get(mid), searchItem);
            // Item found
            if (comparison == 0) {
                return mid;
            }
            // Item is on the left side of array
            if (comparison > 0) {
                return recursiveBinarySearch(start, mid - 1, searchItem);
            }
            // Item is on the right side of array
            return recursiveBinarySearch(mid + 1, end, searchItem);
        }
        // Item not found in the sorted section
        return -1;
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     * @param newItem
     * @param merger    a function that takes two items and returns an item that contains the merged content of
     *                  the two items according to some merging rule.
     *                  e.g. a merger could add the value of attribute X of the second item
     *                  to attribute X of the first item and then return the first item
     * @return  whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {


            // Retrieve the matched item
            E matchedItem = this.get(matchedItemIndex);

            // Merge the matched item and the new item
            E mergedItem = merger.apply(matchedItem, newItem);

            // Replace the matched item in the list with the merged item
            this.set(matchedItemIndex, mergedItem);
            return false;
        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     * @param mapper    a function that calculates the contribution of a single item
     * @return          the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E, Double> mapper) {
        double sum = 0.0;

        for (E item : this) {
            double contribution = mapper.apply(item);
            sum += contribution;
        }

        return sum;
    }

}
