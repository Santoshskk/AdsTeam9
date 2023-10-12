package models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static models.Car.CarType;
import static models.Car.FuelType;

public class Detection {
    private final Car car;                  // the car that was detected
    private final String city;              // the name of the city where the detector was located
    private final LocalDateTime dateTime;   // date and time of the detection event

    /* Representation Invariant:
     *      every Detection shall be associated with a valid Car
     */

    public Detection(Car car, String city, LocalDateTime dateTime) {
        this.car = car;
        this.city = city;
        this.dateTime = dateTime;
    }

    /**
     * Parses detection information from a line of text about a car that has entered an environmentally controlled zone
     * of a specified city.
     * the format of the text line is: lisensePlate, city, dateTime
     * The licensePlate shall be matched with a car from the provided list.
     * If no matching car can be found, a new Car shall be instantiated with the given lisensePlate and added to the list
     * (besides the license plate number there will be no other information available about this car)
     * @param textLine
     * @param cars     a list of known cars, ordered and searchable by licensePlate
     *                 (i.e. the indexOf method of the list shall only consider the lisensePlate when comparing cars)
     * @return a new Detection instance with the provided information
     * or null if the textLine is corrupt or incomplete
     */


    public static Detection fromLine(String textLine, List<Car> cars) {
        Detection newDetection = null;

        // Split the textLine into its components
        String[] parts = textLine.split(",");

        // Check if there are enough components (licensePlate, city, dateTime)
        if (parts.length == 3) {
            String licensePlate = parts[0].trim();
            String city = parts[1].trim();
            String dateTimeStr = parts[2].trim();

            // Define the DateTimeFormatter for the expected date and time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


            // Parse the dateTime string into a LocalDateTime using the formatter
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            // Search for a matching car in the list
            int carIndex = -1;
            for (int i = 0; i < cars.size(); i++) {
                if (cars.get(i).getLicensePlate().equals(licensePlate)) {
                    carIndex = i;
                    break;
                }
            }

            // If no matching car was found, create a new Car instance
            if (carIndex == -1) {
                Car newCar = new Car(licensePlate); // You need to implement a Car constructor
                cars.add(newCar);
                carIndex = cars.size() - 1; // Set the index to the newly added car
            }

            // Create a new Detection instance with the specific LocalDateTime
            Car matchedCar = cars.get(carIndex); // Retrieve the matched car
            newDetection = new Detection(matchedCar, city, dateTime); // You need to implement a Detection constructor
        }

        return newDetection;
    }


    /**
     * Validates a detection against the purple conditions for entering an environmentally restricted zone
     * I.e.:
     * Diesel trucks and diesel coaches with an emission category of below 6 may not enter a purple zone
     * @return a Violation instance if the detection saw an offence against the purple zone rule/
     *          null if no offence was found.
     */
    public Violation validatePurple() {
        // TODO validate that diesel trucks and diesel coaches have an emission category of 6 or above
        CarType carType = car.getCarType();
        FuelType fuelType = car.getFuelType();
        int emissionCategory = car.getEmissionCategory();
        if(carType == CarType.Truck && fuelType == FuelType.Diesel || carType == CarType.Coach && fuelType == FuelType.Diesel) {
            if(emissionCategory < 6) {
                Violation violation = new Violation(car, city);
                return violation;
            }
        }
        return null;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    @Override
    public String toString() {
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return car.getLicensePlate() + "/" + city + "/" + formattedDateTime;
    }

}
