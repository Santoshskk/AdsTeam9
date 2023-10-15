package models;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class DetectionTest2 {
    private Detection vehicle;
    Car bmw;

    @BeforeEach
    void setUp() {
        bmw = new Car("A-123-BB", 4, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        vehicle = new Detection(bmw, "Leiden", LocalDateTime.of(2022,10,1,12,11,10));  // Assuming Vehicle has a constructor that accepts a Car
    }


    //    //checks that a Violation object is returned
    //    //indicating that the method correctly identified the rule violation
    @Test
    void testValidatePurple_forDisallowedTruck() {
        bmw.setCarType(Car.CarType.Truck);
        bmw.setFuelType(Car.FuelType.Diesel);
        bmw.setEmissionCategory(5);
        Violation result = vehicle.validatePurple();
        assertNotNull(result, "Expected a Violation but got null");
    }

    //checks that null is returned.
    // indicating that the method correctly identified that there was no rule violation in this case.
    @Test
    void testValidatePurple_forAllowedTruck() {
        bmw.setCarType(Car.CarType.Truck);
        bmw.setFuelType(Car.FuelType.Diesel);
        bmw.setEmissionCategory(6);
        Violation result = vehicle.validatePurple();
        assertNull(result, "Expected null but got a Violation");
    }


    @Test
    void testValidatePurple_forDisallowedCoach() {
        bmw.setCarType(Car.CarType.Coach);
        bmw.setFuelType(Car.FuelType.Diesel);
        bmw.setEmissionCategory(5);

        Violation result = vehicle.validatePurple();

        assertNotNull(result, "Expected a Violation but got null");
    }


}
