package spotifycharts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SongComparatorTest {

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
