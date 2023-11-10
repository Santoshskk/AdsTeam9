package spotifycharts;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

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
        //if I make the input > 12800. 25600 for example. it takes around 28 secondds to sort.
        // so that is why I limited it till 12800
        int[] datasetSizes = {100, 200, 400, 800, 1600, 3200, 6400, 12800 };


        for (int size : datasetSizes) {
            List<Song> songs = generateTestData(size);
            performSortingTests(songs, size);
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
    private static void performSortingTests(List<Song> originalSongs, int size) {
        SorterImpl<Song> sorter = new SorterImpl<>();
        Comparator<Song> comparator = Comparator.comparing(Song::getTitle); // Adjust based on Song attributes

        testSortingMethod("Bubble Sort", originalSongs, sorter, comparator, size);
        testSortingMethod("Selection Sort", originalSongs, sorter, comparator, size);
        testSortingMethod("Quick Sort", originalSongs, sorter, comparator, size);
        testSortingMethod("Heap Sort", originalSongs, sorter, comparator, 10, size);
    }
    private static void testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator, int size) {
        testSortingMethod(methodName, originalSongs, sorter, comparator, -1, size);
    }
    private static void testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator, int numTops, int size) {
        List<Song> songsToSort = new ArrayList<>(originalSongs);

        long startTime = System.nanoTime();

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

        long endTime = System.nanoTime();
        long durationNanoSeconds = endTime - startTime;
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanoSeconds);
        long durationSeconds = durationNanoSeconds / 1_000_000_000;

        System.out.println(methodName + "\n - Time taken for sorting:" +
                " \n Nano Seconds: " + durationNanoSeconds + " nano seconds" +
                " \n Mili seconds: " + durationMillis + " milli seconds" +
                " \nSeconds: " + durationSeconds + " secondds" +
                "\ninput size: " + size + "\n");
    }
}
