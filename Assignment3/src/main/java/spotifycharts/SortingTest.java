/**
 * The `SortingTest` class is responsible for evaluating the efficiency of various sorting algorithms
 * by measuring their execution times on datasets of songs of different sizes.
 *
 * It generates test data sets, applies sorting algorithms (Bubble Sort, Selection Sort, Quick Sort, and Heap Sort)
 * to these datasets, measures the execution times, and provides an analysis of their efficiency.
 *
 * To ensure accurate assessments, the class provides flexibility through the `numberOfRuns` variable:
 * - When `numberOfRuns` is set to 1, each sorting algorithm is executed once for the given input size.
 *   Execution times are reported in nanoseconds, milliseconds, and seconds for a single run.
 * - When `numberOfRuns` is > then 1 for example set to 10, each sorting algorithm is executed multiple times (e.g., ten times)
 *   for the same input size. The program calculates and reports the average execution time over these runs at the end in nanoseconds.
 *
 * Example: (run this in main) - SpotifyChartsMain
 * To run each sorting algorithm ten times for the same input size and calculate the average execution time:
 * ```java
 * SortingTest sortingTest = new SortingTest();
 * sortingTest.setNumberOfRuns(10); // Set the number of runs to 10
 * sortingTest.runTests(); // Perform sorting algorithm tests and report averages
 * ```
 *
 *
 * Note: The input size is capped at 25600 due to constraints specified in the assignment (20 seconds max sorting time).
 *
 * @author Tygo
 * @version 1.0
 */

package spotifycharts;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SortingTest {
    private int numberOfRuns = 1;
    private Map<String, Long> averageTimes;
    private static Map<Integer, Map<String, Long>> executionTimes = new LinkedHashMap<>(); // Store execution times for each size and method


    /**
     * Executes the sorting tests for all predefined dataset sizes.
     * This method iterates over a set of input sizes and performs sorting tests for each size.
     */
    public void runTests() {
        int[] datasetSizes = {100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600};

        Map<Integer, Map<String, Long>> allAverageTimes = new LinkedHashMap<>();

        for (int size : datasetSizes) {
            List<Song> songs = generateTestData(size, 20060423L);
            averageTimes = performSortingTests(songs, size, this.getNumberOfRuns());
            allAverageTimes.put(size, averageTimes);
        }

        if (numberOfRuns > 1) {
            System.out.println("Sorting done! all Averages below --- \n ");
            // Output the average durations at the end, organized by input size
            for (Map.Entry<Integer, Map<String, Long>> sizeEntry : allAverageTimes.entrySet()) {
                System.out.println("input size: " + sizeEntry.getKey() + " items");
                for (Map.Entry<String, Long> methodEntry : sizeEntry.getValue().entrySet()) {
                    long averageTimeNano = methodEntry.getValue();
                    long averageTimeMillis = TimeUnit.NANOSECONDS.toMillis(averageTimeNano);
                    double averageTimeSeconds = averageTimeNano / 1_000_000_000.0;

                    System.out.println(methodEntry.getKey() + " - Average Time taken over 10 runs:\n" +
                            "nano seconds: " + averageTimeNano + "\n" +
                            "milli seconds: " + averageTimeMillis + "\n" +
                            "seconds: " + averageTimeSeconds + "\n");

                }
                System.out.println();
            }
        }
    }

    /**
     * Generates test data for sorting.
     *
     * @param size The size of the dataset to generate.
     * @param seed The seed for random data generation.
     * @return A list of {@code Song} objects for testing.
     */
    private static List<Song> generateTestData(int size, long seed) {
        ChartsCalculator chartsCalculator = new ChartsCalculator(seed);
        List<Song> songs = chartsCalculator.registerStreamedSongs(size);
        System.gc(); // Call to the JVM to perform garbage collection
        return songs;
    }

    /**
     * Performs sorting tests on a list of songs and returns the execution times.
     *
     * @param originalSongs The list of songs to be sorted.
     * @param size The size of the dataset.
     * @param numberOfRuns The number of runs for each sorting algorithm.
     * @return A map of sorting algorithm names to their execution times.
     */
    private static Map<String, Long> performSortingTests(List<Song> originalSongs, int size, int numberOfRuns) {
        SorterImpl<Song> sorter = new SorterImpl<>();
        Comparator<Song> comparator = Comparator.comparing(Song::getTitle);

        Map<String, Long> averageTimes = new LinkedHashMap<>();

        if (numberOfRuns > 1) {
            averageTimes.put("Bubble Sort", testSortingMethodRepeatedly("Bubble Sort", sorter, comparator, size, numberOfRuns));
            averageTimes.put("Selection Sort", testSortingMethodRepeatedly("Selection Sort", sorter, comparator, size, numberOfRuns));
            averageTimes.put("Quick Sort", testSortingMethodRepeatedly("Quick Sort", sorter, comparator, size, numberOfRuns));
            averageTimes.put("Heap Sort", testSortingMethodRepeatedly("Heap Sort", sorter, comparator, size, 10));
        } else {
            testSortingMethod("Bubble Sort", originalSongs, sorter, comparator, size);
            testSortingMethod("Selection Sort", originalSongs, sorter, comparator, size);
            testSortingMethod("Quick Sort", originalSongs, sorter, comparator, size);
            testSortingMethod("Heap Sort", originalSongs, sorter, comparator, 10, size);
        }

        return averageTimes;
    }

    /**
     * Tests a single sorting method for a given list of songs.
     *
     * @param methodName The name of the sorting method to test.
     * @param originalSongs The list of songs to sort.
     * @param sorter The sorting implementation.
     * @param comparator The comparator used for sorting.
     * @param size The size of the dataset.
     * @return The execution time in nanoseconds for the sorting method.
     */
    private static long testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator, int size) {
        return testSortingMethod(methodName, originalSongs, sorter, comparator, -1, size);
    }

    private static long testSortingMethod(String methodName, List<Song> originalSongs, SorterImpl<Song> sorter, Comparator<Song> comparator, int numTops, int size) {
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
        executionTimes.computeIfAbsent(size, k -> new LinkedHashMap<>()).put(methodName, durationNanoSeconds);

        // Output the duration for this single run
        System.out.println(methodName + "\nTime taken for sorting:" +
                " \nNano Seconds: " + durationNanoSeconds + " nano seconds" +
                " \nMili seconds: " + TimeUnit.NANOSECONDS.toMillis(durationNanoSeconds) + " milli seconds" +
                " \nSeconds: " + durationNanoSeconds / 1_000_000_000 + " seconds" +
                "\ninput size: " + size + " items\n");



        return durationNanoSeconds;
    }

    /**
     * Tests a single sorting method repeatedly for a given list of songs.
     *
     * @param methodName The name of the sorting method to test.
     * @param sorter The sorting implementation.
     * @param comparator The comparator used for sorting.
     * @param size The size of the dataset.
     * @param numberOfRuns The number of times to repeat the test.
     * @return The average execution time in nanoseconds for the sorting method.
     */
    private static long testSortingMethodRepeatedly(String methodName, SorterImpl<Song> sorter, Comparator<Song> comparator, int size, int numberOfRuns) {
        return testSortingMethodRepeatedly(methodName, sorter, comparator, size, -1, numberOfRuns);
    }

    private static long testSortingMethodRepeatedly(String methodName, SorterImpl<Song> sorter, Comparator<Song> comparator, int size, int numTops, int numberOfRuns) {
        long totalDurationNanoSeconds = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            long seed = new Random().nextLong(); // Use long for seed
            List<Song> songs = generateTestData(size, seed);
            totalDurationNanoSeconds += testSortingMethod(methodName, songs, sorter, comparator, numTops, size);
        }
        return totalDurationNanoSeconds / numberOfRuns;
    }


    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public Map<String, Long> getAverageTimes() {
        return averageTimes;
    }

    public Map<Integer, Map<String, Long>> getExecutionTimes() {
        return executionTimes;
    }
}
