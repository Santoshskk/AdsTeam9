package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class TrafficTracker2 {
    private TrafficTracker tracker;
    private Car mustang;

    @BeforeEach
    public void setup() {
        mustang = new Car("A-123-BB", 4, Car.CarType.Truck, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        tracker = new TrafficTracker();
        Violation violation1 = new Violation(mustang, "Amsterdam");  // Assuming 25 * 1 = 25 for Truck

        // Assuming you have a concrete subclass of OrderedList called ConcreteOrderedList
        OrderedList<Violation> violationsList = new OrderedArrayList<>();
        violationsList.add(violation1);
        tracker.setViolations(violationsList);
    }

    @Test
    public void testCalculateTotalFines() {
        double result = tracker.calculateTotalFines();
        // Assuming the fine for the Truck with 1 offence is 25
        assertEquals(25.0, result);
    }
}
