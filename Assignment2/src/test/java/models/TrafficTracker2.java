package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class TrafficTracker2 {
    private TrafficTracker tracker;
    private Car mustang;
    private Car sedan;

    private Violation violation1, violation2, violation3;

    // Set up the testing environment before each test
    @BeforeEach
    public void setup() {
        // Initialize cars
        mustang = new Car("A-123-BB", 4, Car.CarType.Truck, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        sedan = new Car("B-123-CC", 4, Car.CarType.Car, Car.FuelType.Diesel, LocalDate.of(2018, 5, 21));

        // Initialize TrafficTracker
        tracker = new TrafficTracker();

        // Initialize violations
        violation1 = new Violation(mustang, "Amsterdam");
        violation2 = new Violation(sedan, "Rotterdam");
        violation3 = new Violation(mustang, "Rotterdam");

        // Add violations to TrafficTracker
        OrderedList<Violation> violationsList = new OrderedArrayList<>();
        violationsList.add(violation1);
        violationsList.add(violation2);
        violationsList.add(violation3);
        tracker.setViolations(violationsList);
    }

    // Test the calculateTotalFines method of TrafficTracker
    @Test
    public void testCalculateTotalFines() {
        // What is being tested: Whether the total fine calculated is as expected
        double result = tracker.calculateTotalFines();
        assertEquals(50, result);  // Expected total fine should be 50
    }

    // Test the topViolationsByCar method of TrafficTracker
    @Test
    public void testTopViolationsByCar() {
        // What is being tested: Whether the method returns the top 2 violations sorted by Car
        List<Violation> topViolationsByCar = tracker.topViolationsByCar(2);
        assertEquals(2, topViolationsByCar.size());  // We expect 2 top violations by car
        assertTrue(topViolationsByCar.contains(violation1) || topViolationsByCar.contains(violation3));  // One of the mustang violations should be there
        assertTrue(topViolationsByCar.contains(violation2));  // The sedan violation should be there
    }

    // Test the topViolationsByCity method of TrafficTracker
    @Test
    public void testTopViolationsByCity() {
        // What is being tested: Whether the method returns the top 2 violations sorted by City
        List<Violation> topViolationsByCity = tracker.topViolationsByCity(2);
        assertEquals(2, topViolationsByCity.size());  // We expect 2 top violations by city
        assertTrue(topViolationsByCity.stream().anyMatch(v -> "Amsterdam".equals(v.getCity())));
        assertTrue(topViolationsByCity.stream().anyMatch(v -> "Rotterdam".equals(v.getCity())));
    }
}
