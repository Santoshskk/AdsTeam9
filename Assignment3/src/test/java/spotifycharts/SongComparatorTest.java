package spotifycharts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SongComparatorTest {

    /**
     * The  SongComparatorTest class contains JUnit tests for the comparison logic in the Song class.
     * It ensures that the comparison method in the  Song class behaves as expected under various scenarios.
     *
     * This test class specifically focuses on two key aspects of the comparison logic:
     * 1. A song is compared with itself, expecting a result that signifies equality.
     * 2. The symmetry of comparison between two different songs, ensuring that the comparison is consistent in both directions.
     *
     * These tests are crucial for validating the correctness and consistency of the sorting and comparison logic implemented in the {@code Song} class.
     */
    @Test
    public void testCompareWithItself() {
        Song song = new Song("Artist", "Title", Song.Language.EN);
        song.setStreamsCountOfCountry(Song.Country.UK, 100);
        assertEquals(0, song.compareByHighestStreamsCountTotal(song), "A song should compare equal to itself");
    }

    @Test
    public void testCompareSymmetry() {
        Song song1 = new Song("Artist1", "Title1", Song.Language.EN);
        Song song2 = new Song("Artist2", "Title2", Song.Language.EN);
        song1.setStreamsCountOfCountry(Song.Country.UK, 100);
        song2.setStreamsCountOfCountry(Song.Country.UK, 200);
        assertTrue(song1.compareByHighestStreamsCountTotal(song2) == -song2.compareByHighestStreamsCountTotal(song1),
                "Comparison should be symmetric");
    }
}
