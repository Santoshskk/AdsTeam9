import models.FreightWagon;
import models.PassengerWagon;
import models.Wagon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyUnitTestWagon {
    protected Wagon wagon1, wagon2, wagon3, wagon4;

    @BeforeEach
    public void wagonSetup() {
        System.out.println("Setup for MyUnitTestWagon");
        // Initialize Wagons
        wagon1 = (Wagon)(Object)new PassengerWagon(1001, 40);
        wagon2 = (Wagon)(Object)new PassengerWagon(1002, 30);
        wagon3 = (Wagon)(Object)new PassengerWagon(1003, 50);
        wagon4 = (Wagon)(Object)new FreightWagon(2001, 50000);
    }

    @Test
    public void testReversalOfSingleWagon() {
        Wagon result = wagon4.reverseSequence();
        assertEquals(wagon4, result);
    }

    @Test
    public void testMultipleReversals() {
        wagon1.attachTail(wagon2);
        wagon2.attachTail(wagon3);

        wagon1.reverseSequence();
        Wagon newStart = wagon3.reverseSequence();

        assertEquals(wagon1, newStart);
        assertEquals(wagon2, newStart.getNextWagon());
        assertEquals(wagon3, newStart.getNextWagon().getNextWagon());
    }


}

 