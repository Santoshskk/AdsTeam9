import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyUnitTestTrain {
    protected Train passengerTrain, trainWithoutWagons, freightTrainFullCapacity, shortTrain;
    protected Wagon passengerWagon1, passengerWagon2, passengerWagon3;
    protected Wagon freightWagon1, freightWagon2;

    @BeforeEach
    public void customSetup() {
        System.out.println("Custom Setup for MyUnitTestTrain");
        // Initialize Trains
        passengerTrain = new Train(new Locomotive(12345, 7), "Amsterdam", "Paris");
        trainWithoutWagons = new Train(new Locomotive(67890, 5), "London", "Berlin");
        // Freight train at full capacity for testing
        freightTrainFullCapacity = new Train(new Locomotive(54321, 2), "Madrid", "Rome");
        // Short train for moving wagons between trains
        shortTrain = new Train(new Locomotive(98765, 3), "Vienna", "Prague");
        // Initialize Wagons
        passengerWagon1 = (Wagon)(Object)new PassengerWagon(8001, 40);
        passengerWagon2 = (Wagon)(Object)new PassengerWagon(8002, 30);
        passengerWagon3 = (Wagon)(Object)new PassengerWagon(8003, 50);
        freightWagon1 = (Wagon)(Object)new FreightWagon(9001, 40000);
        freightWagon2 = (Wagon)(Object)new FreightWagon(9002, 50000);
        // Attach Wagons
        passengerTrain.attachToRear(passengerWagon1);
        passengerWagon1.attachTail(passengerWagon2);
        freightTrainFullCapacity.attachToRear(freightWagon1);
        freightTrainFullCapacity.attachToRear(freightWagon2);
    }
    @Test
    public void testAttachNullWagonToRear() {
        assertFalse(passengerTrain.attachToRear(null));
    }
    @Test
    public void testAttachWagonToRear() {
        Wagon newWagon = (Wagon)(Object)new PassengerWagon(8004, 45);
        assertTrue(passengerTrain.attachToRear(newWagon));   // Test if the wagon is attached successfully

        // Verifying that the new wagon is indeed the last wagon in the sequence
        Wagon lastWagon = passengerTrain.getLastWagonAttached();
        assertEquals(newWagon, lastWagon);
    }







}
