package models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyUnitTest {
    private Car car1;
    private Car car2;
    private TrafficTracker tracker;
    private Detection detection1;
    private Violation violation1, violation2, violation3;

    // Setup common objects that will be used in multiple tests
    @BeforeEach
    public void setup() {
        car1 = new Car("A-123-BB", 4, Car.CarType.Truck, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        car2 = new Car("B-123-CC", 4, Car.CarType.Car, Car.FuelType.Diesel, LocalDate.of(2018, 5, 21));
        tracker = new TrafficTracker();
        detection1 = new Detection(car1, "Amsterdam", LocalDateTime.of(2022, 10, 15, 12, 0));
        // Add violations to TrafficTracker
        OrderedList<Violation> violationsList = new OrderedArrayList<>();
        violationsList.add(violation1);
        violationsList.add(violation2);
        violationsList.add(violation3);
        tracker.setViolations(violationsList);

    }
    // Testing Car's constructor
    // This test verifies that a Car object is correctly initialized
    @Test
    public void testCarInitialization() {
        assertNotNull(car1);
        assertEquals("A-123-BB", car1.getLicensePlate());
        assertEquals(4, car1.getEmissionCategory());
    }
    // Testing Violation's combineOffencesCounts method
    // This test checks that the offense counts
    // are combined correctly when two similar Violations are combined
    @Test
    public void testViolationOffenceCombination() {
        Violation violation1 = new Violation(car1, "Amsterdam");
        Violation violation2 = new Violation(car1, "Amsterdam");
        Violation combined = violation1.combineOffencesCounts(violation2);
        assertEquals(2, combined.getOffencesCount());
    }
}