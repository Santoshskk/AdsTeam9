package models;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class TrafficTracker {
    private final String TRAFFIC_FILE_EXTENSION = ".txt";
    private final String TRAFFIC_FILE_PATTERN = ".+\\" + TRAFFIC_FILE_EXTENSION;

    private OrderedList<Car> cars;                  // the reference list of all known Cars registered by the RDW
    private OrderedList<Violation> violations;      // the accumulation of all offences by car and by city

    public TrafficTracker() {
        // Initialize cars with an empty ordered list which sorts items by licensePlate.
        this.cars = new OrderedArrayList<>(Comparator.comparing(Car::getLicensePlate));

        // Initialize violations with an empty ordered list which sorts items by car and city.
        this.violations = new OrderedArrayList<>(Comparator
                .comparing(Violation::getCar)
                .thenComparing(Violation::getCity));
    }


    /**
     * imports all registered cars from a resource file that has been provided by the RDW
     * @param resourceName
     */
    public void importCarsFromVault(String resourceName) {
        this.cars.clear();

        // load all cars from the text file
        int numberOfLines = importItemsFromFile(this.cars,
                createFileFromURL(TrafficTracker.class.getResource(resourceName)),
                Car::fromLine);

        // sort the cars for efficient later retrieval
        this.cars.sort();

        System.out.printf("Imported %d cars from %d lines in %s.\n", this.cars.size(), numberOfLines, resourceName);
    }

    /**
     * imports and merges all raw detection data of all entry gates of all cities from the hierarchical file structure of the vault
     * accumulates any offences against purple rules into this.violations
     * @param resourceName
     */
    public void importDetectionsFromVault(String resourceName) {
        this.violations.clear();

        int totalNumberOfOffences =
            this.mergeDetectionsFromVaultRecursively(
                    createFileFromURL(TrafficTracker.class.getResource(resourceName)));

        System.out.printf("Found %d offences among detections imported from files in %s.\n",
                totalNumberOfOffences, resourceName);
    }

    /**
     * traverses the detections vault recursively and processes every data file that it finds
     * @param file
     */
    private int mergeDetectionsFromVaultRecursively(File file) {
        int totalNumberOfOffences = 0;

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            for (File subFile : filesInDirectory) {
                totalNumberOfOffences += mergeDetectionsFromVaultRecursively(subFile);
            }
        } else if (file.getName().matches(TRAFFIC_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw detection files
            // process the content of this file and merge the offences found into this.violations
            totalNumberOfOffences += this.mergeDetectionsFromFile(file);
        }

        return totalNumberOfOffences;
    }

    /**
     * imports another batch detection data from the filePath text file
     * and merges the offences into the earlier imported and accumulated violations
     * @param file
     */
    private int mergeDetectionsFromFile(File file) {
        this.violations.sort();
        List<Detection> newDetections = new ArrayList<>();
        importItemsFromFile(newDetections, file, line -> Detection.fromLine(line, cars));
        System.out.printf("Imported %d detections from %s.\n", newDetections.size(), file.getPath());
        int totalNumberOfOffences = 0;

        for (Detection detection : newDetections) {
            Violation newViolation = detection.validatePurple();
            if (newViolation != null) {
                boolean violationExists = false;
                for (Violation existingViolation : this.violations) {
                    if (existingViolation.getCar().equals(newViolation.getCar()) &&
                            existingViolation.getCity().equals(newViolation.getCity())) {
                        existingViolation.setOffencesCount(existingViolation.getOffencesCount() + 1);
                        violationExists = true;
                        break;
                    }
                }
                if (!violationExists) {
                    this.violations.add(newViolation);
                }
                totalNumberOfOffences++;
            }
        }
        return totalNumberOfOffences;
    }







    /**
     * calculates the total revenue of fines from all violations,
     * Trucks pay €25 per offence, Coaches €35 per offence
     * @return      the total amount of money recovered from all violations
     */
    public double calculateTotalFines() {
        return this.violations.aggregate(violation -> {
            Car.CarType carType = violation.getCar().getCarType(); // Assuming Violation has a getCar() method
            int offencesCount = violation.getOffencesCount();
            if (carType == Car.CarType.Truck) {
                return (double) (25 * offencesCount);
            } else if (carType == Car.CarType.Coach) {
                return (double) (35 * offencesCount);
            }
            return 0.0; // return 0 if the car type is neither Truck nor Coach
        });
    }


    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by car across all cities.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCar(int topNumber) {
        OrderedList<Violation> mergedViolationsByCar = new OrderedArrayList<>(Comparator.comparing(Violation::getCar));
        for (Violation v : this.violations) {
            mergedViolationsByCar.merge(v, (v1, v2) -> {
                v1.setOffencesCount(v1.getOffencesCount() + v2.getOffencesCount());
                return v1;
            });
        }

        //sort the new list by decreasing offencesCount.
        mergedViolationsByCar.sort((v1, v2) -> Integer.compare(v2.getOffencesCount(), v1.getOffencesCount()));

        //use .subList to return only the topNumber of violations from the sorted list
        return mergedViolationsByCar.subList(0, Math.min(topNumber, mergedViolationsByCar.size()));
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by city across all cars.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCity(int topNumber) {
        //merge all violations from this.violations into a new OrderedArrayList
        //which orders and aggregates violations by city
        OrderedList<Violation> mergedViolationsByCity = new OrderedArrayList<>(Comparator.comparing(Violation::getCity));
        for (Violation v : this.violations) {
            mergedViolationsByCity.merge(v, (v1, v2) -> {
                v1.setOffencesCount(v1.getOffencesCount() + v2.getOffencesCount());
                return v1;
            });
        }

        //sort the new list by decreasing offencesCount.
        mergedViolationsByCity.sort((v1, v2) -> Integer.compare(v2.getOffencesCount(), v1.getOffencesCount()));

        // use .subList to return only the topNumber of violations from the sorted list
        return mergedViolationsByCity.subList(0, Math.min(topNumber, mergedViolationsByCity.size()));
    }

    public static <E> int importItemsFromFile(List<E> items, File file, Function<String, E> converter) {
        int numberOfLines = 0;

        Scanner scanner = createFileScanner(file);

        // Read all source lines from the scanner,
        // convert each line to an item of type E
        // and add each successfully converted item into the list
        while (scanner.hasNext()) {
            // Input another line with information
            String line = scanner.nextLine();
            numberOfLines++;

            // Convert the line to an instance of E using the provided converter function
            E item = converter.apply(line);

            // Add a successfully converted item to the list of items
            if (item != null) {
                items.add(item);
            }
        }

        return numberOfLines;
    }


    /**
     * helper method to create a scanner on a file and handle the exception
     * @param file
     * @return
     */
    private static Scanner createFileScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + file.getPath());
        }
    }
    private static File createFileFromURL(URL url) {
        try {
            return new File(url.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " + url.getPath());
        }
    }

    public OrderedList<Car> getCars() {
        return this.cars;
    }

    public OrderedList<Violation> getViolations() {
        return this.violations;
    }
}
