package spotifycharts;

import java.util.EnumMap;
import java.util.Map;

public class Song {

    public enum Language {
        EN, // English
        NL, // Dutch
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        UK, // United Kingdom
        NL, // Netherlands
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist; //instance variable
    private final String title;
    private final Language language;

    /*
    I chose a Map because you can store key value pairs. the country enum can be used as the key and
    an int will be the count of streams
    */
    private Map<Country, Integer> streamsPerCountry;


    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;
        this.streamsPerCountry = new EnumMap<>(Country.class);
    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        // TODO register the streams count for the given country.
        streamsPerCountry.put(country, streamsCount);
    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        return streamsPerCountry.get(country);
    }
    /**
     *
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {

        int totalNumberOfStreams = 0;
        for (int streamsCount : streamsPerCountry.values()) {
            totalNumberOfStreams += streamsCount;
        }
        return totalNumberOfStreams;
    }

    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {

        int totalCurrentSong = this.getStreamsCountTotal();
        int totalOtherSong = other.getStreamsCountTotal();
        return Integer.compare(totalOtherSong, totalCurrentSong);

    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        if (this.language == Language.NL && other.language != Language.NL) {
            // This song is Dutch and should come before the other song
            return -1;
        } else if (this.language != Language.NL && other.language == Language.NL) {
            // The other song is Dutch and should come before this song
            return 1;
        }
        return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return artist + "/" + title + "{" + language + "}" + "(" + getStreamsCountTotal() + ")";
    }
}
