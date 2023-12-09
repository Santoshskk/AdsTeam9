package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import nl.hva.ict.ads.utils.xml.XMLParser;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Holds all election data per consituency
 * Provides calculation methods for overall election results
 */
public class Election {

    private String name;

    // all (unique) parties in this election, organised by Id
    // will be build from the XML
    protected Map<Integer, Party> parties;

    // all (unique) constituencies in this election, identified by Id
    protected Set<Constituency> constituencies;

    public Election(String name) {
        this.name = name;

        this.parties = new HashMap<>();
        this.constituencies = new HashSet<>();
    }

    /**
     * finds all (unique) parties registered for this election
     * @return all parties participating in at least one constituency, without duplicates
     */
    public Collection<Party> getParties() {
        return parties.values();
    }

    /**
     * finds the party with a given id
     * @param id
     * @return  the party with given id, or null if no such party exists.
     */
    public Party getParty(int id) {
        return parties.get(id);
    }

    public Set<? extends Constituency> getConstituencies() {
        return this.constituencies;
    }

        /**
         * finds all unique candidates across all parties across all constituencies
         * organised by increasing party-id
         * @return alle unique candidates organised by increasing party-id
         */
        public List<Candidate> getAllCandidates() {
            return constituencies.stream()
                    .flatMap(constituency -> constituency.getAllCandidates().stream())
                    .sorted(Comparator.comparing(candidate -> candidate.getParty().getId()))
                        .distinct()
                    .collect(Collectors.toList());
        }

    /**
     * Retrieve for the given party the number of Candidates that have been registered per Constituency
     * @param party
     * @return
     */
    public Map<Constituency, Integer> numberOfRegistrationsByConstituency(Party party) {
        return constituencies.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        constituency -> (int) constituency.getCandidates(party).size()
                ));
    }


    /**
     * Finds all Candidates that have a duplicate name against another candidate in the election
     * (can be in the same party or in another party)
     * @return
     */

    /**
     * HINTS:
     * getCandidatesWithDuplicateNames:
     *  Approach-1: first build a Map that counts the number of candidates per given name
     *              then build the collection from all candidates, excluding those whose name occurs only once.
     *  Approach-2: build a stream that is sorted by name
     *              apply a mapMulti that drops unique names but keeps the duplicates
     *              this approach probably requires complex lambda expressions that are difficult to justify
     */
    //first
    public Set<Candidate> getCandidatesWithDuplicateNames() {
        Map<String, Long> nameCounts = getAllCandidates().stream()
                .map(candidate -> candidate.getFullName().toLowerCase()) // Convert names to lowercase for case-insensitive comparison
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return getAllCandidates().stream()
                .filter(candidate -> nameCounts.getOrDefault(candidate.getFullName().toLowerCase(), 0L) > 1)
                .collect(Collectors.toSet());
    }


    /**
     * Retrieve from all constituencies the combined sub set of all polling stations that are located within the area of the specified zip codes
     * i.e. firstZipCode <= pollingStation.zipCode <= lastZipCode
     * All valid zip codes adhere to the pattern 'nnnnXX' with 1000 <= nnnn <= 9999 and 'AA' <= XX <= 'ZZ'
     * @param firstZipCode
     * @param lastZipCode
     * @return      the sub set of polling stations within the specified zipCode range
     */
    public Collection<PollingStation> getPollingStationsByZipCodeRange(String firstZipCode, String lastZipCode) {
        return constituencies.stream()
                .flatMap(con -> con.getPollingStationsByZipCodeRange(firstZipCode, lastZipCode).stream())
                .peek(pollingStation -> System.out.println("Polling Station Zip Code: " + pollingStation.getZipCode()))
                .collect(Collectors.toList());
    }



    /**
     * Retrieves per party the total number of votes across all candidates, constituencies and polling stations
     * @return
     */
    public Map<Party, Integer> getVotesByParty() {
        Map<Party, Integer> votesByParty = new HashMap<>();

        for (Constituency constituency : constituencies) {
            for (PollingStation pollingStation : constituency.getPollingStations()) {
                for (Map.Entry<Candidate, Integer> entry : pollingStation.getVotesByCandidate().entrySet()) {
                    Party party = entry.getKey().getParty();
                    int votes = entry.getValue();
                    votesByParty.put(party, votesByParty.getOrDefault(party, 0) + votes);
                }
            }
        }

        return votesByParty;
    }


    /**
     * Retrieves per party the total number of votes across all candidates,
     * that were cast in one out of the given collection of polling stations.
     * This method is useful to prepare an election result for any sub-area of a Constituency.
     * Or to obtain statistics of special types of voting, e.g. by mail.
     * @param pollingStations the polling stations that cover the sub-area of interest
     * @return
     */
    public Map<Party, Integer> getVotesByPartyAcrossPollingStations(Collection<PollingStation> pollingStations) {
        Map<Party, Integer> votesByParty = new HashMap<>();

        for (PollingStation pollingStation : pollingStations) {
            for (Map.Entry<Candidate, Integer> entry : pollingStation.getVotesByCandidate().entrySet()) {
                Party party = entry.getKey().getParty();
                int votes = entry.getValue();
                votesByParty.put(party, votesByParty.getOrDefault(party, 0) + votes);
            }
        }
        return votesByParty;
    }



    /**
     * Transforms and sorts decreasingly vote counts by party into votes percentages by party
     * The party with the highest vote count shall be ranked upfront
     * The votes percentage by party is calculated from  100.0 * partyVotes / totalVotes;
     * @return  the sorted list of (party,votesPercentage) pairs with the highest percentage upfront
     */
    public static List<Map.Entry<Party, Double>> sortedElectionResultsByPartyPercentage(int tops, Map<Party, Integer> votesCounts) {
        // Calculate the total number of votes
        double totalVotes = votesCounts.values().stream().mapToInt(Integer::intValue).sum();

        // Calculate the percentage of votes for each party and store it in a map
        Map<Party, Double> partyPercentageMap = new HashMap<>();
        for (Map.Entry<Party, Integer> entry : votesCounts.entrySet()) {
            Party party = entry.getKey();
            int votes = entry.getValue();
            double percentage = (votes / totalVotes) * 100.0;
            partyPercentageMap.put(party, percentage);
        }

        // Sort the map entries by percentage in descending order
        List<Map.Entry<Party, Double>> sortedEntries = partyPercentageMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Party, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Return the top 'tops' entries from the sorted list
        return sortedEntries.subList(0, Math.min(tops, sortedEntries.size()));
    }


    /**
     * Find the most representative Polling Station, which has got its votes distribution across all parties
     * the most alike the distribution of overall total votes.
     * A perfect match is found, if for each party the percentage of votes won at the polling station
     * is identical to the percentage of votes won by the party overall in the election.
     * The most representative Polling Station has the smallest deviation from that perfect match.
     *
     * There are different metrics possible to calculate a relative deviation between distributions.
     * You may use the helper method {@link #euclidianVotesDistributionDeviation(Map, Map)}
     * which calculates a relative least-squares deviation between two distributions.
     *
     * @return the most representative polling station.
     */
    public PollingStation findMostRepresentativePollingStation() {
        // Calculate the overall total votes count distribution by Party
        Map<Party, Integer> overallVotesDistribution = getVotesByParty();

        // Create a comparator based on the deviation between polling station's distribution and overall distribution
        Comparator<PollingStation> deviationComparator = Comparator.comparingDouble(pollingStation -> {
            Map<Party, Integer> pollingStationVotes = pollingStation.getVotesByParty();

            // Calculate the deviation using euclidianVotesDistributionDeviation method
            return euclidianVotesDistributionDeviation(overallVotesDistribution, pollingStationVotes);
        });

        // Find the polling station with the lowest deviation
        return constituencies.stream()
                .flatMap(con -> con.getPollingStations().stream())
                .min(deviationComparator)
                .orElse(null);
    }


    /**
     * Calculates the Euclidian distance between the relative distribution across parties of two voteCounts.
     * If the two relative distributions across parties are identical, then the distance will be zero
     * If some parties have relatively more votes in one distribution than the other, the outcome will be positive.
     * The lower the outcome, the more alike are the relative distributions of the voteCounts.
     * ratign of votesCounts1 relative to votesCounts2.
     * see https://towardsdatascience.com/9-distance-measures-in-data-science-918109d069fa
     *
     * @param votesCounts1 one distribution of votes across parties.
     * @param votesCounts2 another distribution of votes across parties.
     * @return de relative distance between the two distributions.
     */
    private double euclidianVotesDistributionDeviation(Map<Party, Integer> votesCounts1, Map<Party, Integer> votesCounts2) {
        // calculate total number of votes in both distributions
        int totalNumberOfVotes1 = integersSum(votesCounts1.values());
        int totalNumberOfVotes2 = integersSum(votesCounts2.values());

        // we calculate the distance as the sum of squares of relative voteCount distribution differences per party
        // if we compare two voteCounts that have the same relative distribution across parties, the outcome will be zero

        return votesCounts1.entrySet().stream()
                .mapToDouble(e -> Math.pow(e.getValue()/(double)totalNumberOfVotes1 -
                        votesCounts2.getOrDefault(e.getKey(),0)/(double)totalNumberOfVotes2, 2))
                .sum();
    }

    /**
     * auxiliary method to calculate the total sum of a collection of integers
     * @param integers
     * @return
     */
    public static int integersSum(Collection<Integer> integers) {
        return integers.stream().reduce(Integer::sum).orElse(0);
    }


    /**
     * Prepare a summary for a given party.
     *
     * @param partyId the ID of the party for which to generate the summary.
     * @return a summary of the specified party.
     */
    public String prepareSummary(int partyId) {
        Party party = this.getParty(partyId);
        StringBuilder summary = new StringBuilder()
                .append("\nSummary of ").append(party.getName()).append(":\n");

        // Report total number of candidates in the given party
        int totalCandidatesInParty = getAllCandidates().stream()
                .filter(candidate -> candidate.getParty().getId() == partyId).toList()
                .size();
        summary.append("Total number of candidates in the party: ").append(totalCandidatesInParty).append("\n");

        // Report the list with all candidates in the given party
        summary.append("List of candidates in the party:\n");
        getAllCandidates().stream()
                .filter(candidate -> candidate.getParty().getId() == partyId)
                .forEach(candidate -> summary.append("- ").append(candidate.getFullName()).append("\n"));

        // Report total number of registrations for the given party
        Map<Constituency, Integer> registrationsByConstituency = numberOfRegistrationsByConstituency(party);
        int totalRegistrations = registrationsByConstituency.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        summary.append("Total number of registrations for the party: ").append(totalRegistrations).append("\n");

        // Report the map of number of registrations by constituency for the given party
        summary.append("Number of registrations by constituency:\n");
        registrationsByConstituency.forEach((constituency, registrations) ->
                summary.append("- ").append(constituency.getName()).append(": ").append(registrations).append(" registrations\n"));

        return summary.toString();
    }


    public String prepareSummary() {
        StringBuilder summary = new StringBuilder()
                .append("\nElection summary of ").append(this.name).append(":\n");

        //report the total number of parties in the election
        int totalParties = getParties().size();
        summary.append("Total number of parties in the election: ").append(totalParties).append("\n");

        //report the list of all parties ordered by increasing party-Id
        List<Party> sortedParties = getParties().stream()
                .sorted(Comparator.comparing(Party::getId))
                .collect(Collectors.toList());
        summary.append("List of all parties ordered by party ID:\n");
        sortedParties.forEach(party -> summary.append("- ").append(party.getName()).append(" (ID: ").append(party.getId()).append(")\n"));

        //report the total number of constituencies in the election
        int totalConstituencies = getConstituencies().size();
        summary.append("Total number of constituencies in the election: ").append(totalConstituencies).append("\n");

        //report the total number of polling stations in the election
        long totalPollingStations = getConstituencies().stream()
                .flatMap(con -> con.getPollingStations().stream())
                .count();
        summary.append("Total number of polling stations in the election: ").append(totalPollingStations).append("\n");

        //report the total number of (different) candidates in the election
        List<Candidate> allCandidates = getAllCandidates();
        int totalCandidates = allCandidates.size();
        summary.append("Total number of different candidates in the election: ").append(totalCandidates).append("\n");

        //report the list with all candidates which have a counterpart with a duplicate name in a different party
        Set<Candidate> candidatesWithDuplicateNames = getCandidatesWithDuplicateNames();
        summary.append("Candidates with duplicate names in different parties:\n");
        candidatesWithDuplicateNames.forEach(candidate ->
                summary.append("- ").append(candidate.getFullName()).append(" (Party: ").append(candidate.getParty().getName()).append(")\n"));

        //report the sorted list of overall election results ordered by decreasing party percentage
        Map<Party, Integer> votesByParty = getVotesByParty();
        List<Map.Entry<Party, Double>> sortedElectionResults = sortedElectionResultsByPartyPercentage(votesByParty.size(), votesByParty);
        summary.append("Overall election results ordered by decreasing party percentage:\n");
        sortedElectionResults.forEach(entry ->
                summary.append("- ").append(entry.getKey().getName()).append(" (Percentage: ").append(entry.getValue()).append("%)\n"));

        //report the polling stations within the Amsterdam Wibautstraat area with zipcodes between 1091AA and 1091ZZ
        String firstZipCode = "1091AA";
        String lastZipCode = "1091ZZ";
        Collection<PollingStation> pollingStationsInArea = getPollingStationsByZipCodeRange(firstZipCode, lastZipCode);
        summary.append("Polling stations within Amsterdam Wibautstraat area (Zip Codes: ").append(firstZipCode).append(" to ").append(lastZipCode).append("):\n");
        pollingStationsInArea.forEach(pollingStation ->
                summary.append("- ").append(pollingStation.getName()).append(" (Zip Code: ").append(pollingStation.getZipCode()).append(")\n"));

        //report the top 10 sorted election results within the Amsterdam Wibautstraat area
        // with zipcodes between 1091AA and 1091ZZ ordered by decreasing party percentage
        Map<Party, Integer> votesByPartyInArea = getVotesByPartyAcrossPollingStations(pollingStationsInArea);
        List<Map.Entry<Party, Double>> top10ElectionResultsInArea = sortedElectionResultsByPartyPercentage(10, votesByPartyInArea);
        summary.append("Top 10 election results within Amsterdam Wibautstraat area (Zip Codes: ").append(firstZipCode).append(" to ").append(lastZipCode).append("):\n");
        top10ElectionResultsInArea.forEach(entry ->
                summary.append("- ").append(entry.getKey().getName()).append(" (Percentage: ").append(entry.getValue()).append("%)\n"));

        //report the most representative polling station across the election
        PollingStation mostRepresentativeStation = findMostRepresentativePollingStation();
        summary.append("Most representative polling station across the election:\n");
        summary.append("- ").append(mostRepresentativeStation.getName()).append("\n");

        //report the sorted election results by decreasing party percentage of the most representative polling station
        Map<Party, Integer> votesByPartyInMostRepresentativeStation = mostRepresentativeStation.getVotesByParty();
        List<Map.Entry<Party, Double>> sortedElectionResultsInMostRepresentativeStation = sortedElectionResultsByPartyPercentage(votesByPartyInMostRepresentativeStation.size(), votesByPartyInMostRepresentativeStation);
        summary.append("Election results in the most representative polling station ordered by decreasing party percentage:\n");
        sortedElectionResultsInMostRepresentativeStation.forEach(entry ->
                summary.append("- ").append(entry.getKey().getName()).append(" (Percentage: ").append(entry.getValue()).append("%)\n"));

        return summary.toString();
    }


    /**
     * Reads all data of Parties, Candidates, Contingencies and PollingStations from available files in the given folder and its subfolders
     * This method can cope with any structure of sub folders, but does assume the file names to comply with the conventions
     * as found from downloading the files from https://data.overheid.nl/dataset/verkiezingsuitslag-tweede-kamer-2021
     * So, you can merge folders after unpacking the zip distributions of the data, but do not change file names.
     * @param folderName    the root folder with the data files of the election results
     * @return een Election met alle daarbij behorende gegevens.
     * @throws XMLStreamException bij fouten in een van de XML bestanden.
     * @throws IOException als er iets mis gaat bij het lezen van een van de bestanden.
     */
    public static Election importFromDataFolder(String folderName) throws XMLStreamException, IOException {
        System.out.println("Loading election data from " + folderName);
        Election election = new Election(folderName);
        int progress = 0;
        Map<Integer, Constituency> kieskringen = new HashMap<>();
        for (Path constituencyCandidatesFile : PathUtils.findFilesToScan(folderName, "Kandidatenlijsten_TK2021_")) {
            XMLParser parser = new XMLParser(new FileInputStream(constituencyCandidatesFile.toString()));
            Constituency constituency = Constituency.importFromXML(parser, election.parties);
            //election.constituenciesM.put(constituency.getId(), constituency);
            election.constituencies.add(constituency);
            showProgress(++progress);
        }
        System.out.println();
        progress = 0;
        for (Path votesPerPollingStationFile : PathUtils.findFilesToScan(folderName, "Telling_TK2021_gemeente")) {
            XMLParser parser = new XMLParser(new FileInputStream(votesPerPollingStationFile.toString()));
            election.importVotesFromXml(parser);
            showProgress(++progress);
        }
        System.out.println();
        return election;
    }

    protected static void showProgress(final int progress) {
        System.out.print('.');
        if (progress % 50 == 0) System.out.println();
    }

    /**
     * Auxiliary method for parsing the data from the EML files
     * This methode can be used as-is and does not require your investigation or extension.
     */
    public void importVotesFromXml(XMLParser parser) throws XMLStreamException {
        if (parser.findBeginTag(Constituency.CONSTITUENCY)) {

            int constituencyId = 0;
            if (parser.findBeginTag(Constituency.CONSTITUENCY_IDENTIFIER)) {
                constituencyId = parser.getIntegerAttributeValue(null, Constituency.ID, 0);
                parser.findAndAcceptEndTag(Constituency.CONSTITUENCY_IDENTIFIER);
            }

            //Constituency constituency = this.constituenciesM.get(constituencyId);
            final int finalConstituencyId = constituencyId;
            Constituency constituency = this.constituencies.stream()
                    .filter(c -> c.getId() == finalConstituencyId)
                    .findFirst()
                    .orElse(null);

            //parser.findBeginTag(PollingStation.POLLING_STATION_VOTES);
            while (parser.findBeginTag(PollingStation.POLLING_STATION_VOTES)) {
                PollingStation pollingStation = PollingStation.importFromXml(parser, constituency, this.parties);
                if (pollingStation != null) constituency.add(pollingStation);
            }

            parser.findAndAcceptEndTag(Constituency.CONSTITUENCY);
        }
    }



}
