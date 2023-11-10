package spotifycharts;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class SortingTest{

    /**
     *
     this class includes methods for generating test data sets of varying sizes and testing different sorting algorithms
     (Bubble Sort, Selection Sort, Quick Sort, and Heap Sort)
     The class measures and outputs the execution time for each sorting algorithm,
     aiding in the analysis of their efficiency.
     System.gc() is called post data generation to minimize GC (garbage collection) impact on timing measurements
     */

    public static void main(String[] args) {
        int[] datasetSizes = {100, 200, 400, 800, 1600};

        for (int size : datasetSizes) {
            List<Song> songs = generateTestData(size);
            performSortingTests(songs);
        }
    }
    private static List<Song> generateTestData(int size) {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            songs.add(SongBuilder.createSample(i)); // Adjust this to your song creation method
        }

        // call to the JVM to perform garbage collection
        System.gc();

        return songs;
    }
    private static void performSortingTests(List<Song> originalSongs) {
        SorterImpl<Song> sorter = new SorterImpl<>();
        Comparator<Song> comparator = Comparator.comparing(Song::getTitle); // Adjust based on Song attributes

        testSortingMethod("Bubble Sort", originalSongs, sorter, comparator);
        testSortingMethod("Selection Sort", originalSongs, sorter, comparator);
        testSortingMethod("Quick Sort", originalSongs, sorter, comparator);
        testSortingMethod("Heap Sort", originalSongs, sorter, comparator, 10);
    }
    private static void testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator) {
        testSortingMethod(methodName, originalSongs, sorter, comparator, -1);
    }
    private static void testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator, int numTops) {
        List<Song> songsToSort = new ArrayList<>(originalSongs);

        long startTime = System.currentTimeMillis();

        // Call the appropriate sorting method
        switch (methodName) {
            case "Bubble Sort":
                sorter.selInsBubSort(songsToSort, comparator);
                break;
            case "Selection Sort":
                sorter.selectionSort(songsToSort, comparator);
                break;
            case "Quick Sort":
                sorter.quickSort(songsToSort, comparator);
                break;
            case "Heap Sort":
                if (numTops > 0) {
                    sorter.topsHeapSort(numTops, songsToSort, comparator);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown sorting method: " + methodName);
        }

        long endTime = System.currentTimeMillis();
        System.out.println(methodName + " - Time taken for sorting: " + (endTime - startTime) + "ms");
    }
}
