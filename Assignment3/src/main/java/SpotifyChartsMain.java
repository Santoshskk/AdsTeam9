import spotifycharts.ChartsCalculator;
import spotifycharts.SortingTest;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");

        ChartsCalculator chartsCalculator = new ChartsCalculator(20060423L);
        chartsCalculator.registerStreamedSongs(263);
        chartsCalculator.showResults();

        //uncomment this to run the sortingTest. to test performance of all the sorting

//        SortingTest sortingTest1 = new SortingTest();
//        sortingTest1.setNumberOfRuns(1);
//        sortingTest1.runTests();

    }
}
