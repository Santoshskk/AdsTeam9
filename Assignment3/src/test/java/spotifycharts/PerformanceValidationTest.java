package spotifycharts;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


/**
 * This class contains JUnit tests for evaluating the performance of different sorting algorithms
 * implemented in the SortingTest class. It tests the execution times against predefined
 * expected times to ensure that the sorting algorithms perform as expected.
 *
 * The class defines various input sizes and expected execution times for each sorting algorithm.
 * These expected times are used as a benchmark to validate the actual execution times obtained
 * from the SortingTest class.
 *
 * Each test is run for a specific input size, and the execution times of the sorting algorithms
 * are then compared to the expected times, allowing a margin of error to account for environmental
 * variability and other factors affecting performance.
 *
 * @author Tygo
 */

public class PerformanceValidationTest {
    private static final int NUMBER_OF_RUNS = 1;
    private static final int[] INPUT_SIZES = {100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600};
    private static final Map<Integer, Map<String, Long>> EXPECTED_EXECUTION_TIMES = Map.of(
            100, Map.of(
                    "Bubble Sort", 7554400L,
                    "Selection Sort", 429100L,
                    "Quick Sort", 158600L,
                    "Heap Sort", 759000L
            ),
            200, Map.of(
                    "Bubble Sort", 9578000L,
                    "Selection Sort", 1815300L,
                    "Quick Sort", 173300L,
                    "Heap Sort", 66900L
            ),
            400, Map.of(
                    "Bubble Sort", 12126200L,
                    "Selection Sort", 4773700L,
                    "Quick Sort", 415300L,
                    "Heap Sort", 80700L
            ),
            800, Map.of(
                    "Bubble Sort", 47754300L,
                    "Selection Sort", 8375100L,
                    "Quick Sort", 565100L,
                    "Heap Sort", 133900L
            ),
            1600, Map.of(
                    "Bubble Sort", 69590800L,
                    "Selection Sort", 13457300L,
                    "Quick Sort", 1305200L,
                    "Heap Sort", 286200L
            ),
            3200, Map.of(
                    "Bubble Sort", 366482400L,
                    "Selection Sort", 54640100L,
                    "Quick Sort", 2299200L,
                    "Heap Sort", 799300L
            ),
            6400, Map.of(
                    "Bubble Sort", 1702774100L,
                    "Selection Sort", 229300500L,
                    "Quick Sort", 6185600L,
                    "Heap Sort", 3652600L
            ),
            12800, Map.of(
                    "Bubble Sort", 7046343200L,
                    "Selection Sort", 1297488100L,
                    "Quick Sort", 9989600L,
                    "Heap Sort", 1299800L
            ),
            25600, Map.of(
                    "Bubble Sort", 40123723600L,
                    "Selection Sort", 5832166000L,
                    "Quick Sort", 31482600L,
                    "Heap Sort", 3300200L
            )
    );

    @Test
    public void validateSortingPerformance() {
        SortingTest sortingTest = new SortingTest();
        sortingTest.setNumberOfRuns(NUMBER_OF_RUNS);
        sortingTest.runTests(); // This will execute sorting for all dataset sizes
        for (int size : INPUT_SIZES) {

            Map<String, Long> actualTimesForSize = sortingTest.getExecutionTimes().get(size); // Retrieve the execution times for the current size
            Map<String, Long> expectedTimesForSize = EXPECTED_EXECUTION_TIMES.get(size);

            for (String sortingAlgorithm : actualTimesForSize.keySet()) {
                long actualTime = actualTimesForSize.get(sortingAlgorithm);
                long expectedTime = expectedTimesForSize.get(sortingAlgorithm);

                // Use a margin of error for comparison, e.g., 50% of the expected value
                assertTrue(Math.abs(actualTime - expectedTime) < 0.5 * expectedTime,
                        "Execution time for " + sortingAlgorithm + " with input size " + size + " is out of expected range");
            }
        }
    }
}
