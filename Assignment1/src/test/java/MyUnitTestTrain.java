import models.*;
import org.junit.jupiter.api.BeforeEach;

public class MyUnitTestTrain {

    Train passengerTrain, trainWithoutWagons, freightTrain;
    Wagon passengerWagon1, passengerWagon2, passengerWagon3, passengerWagon4;
    Wagon passengerWagon8001, passengerWagon8002;
    Wagon freightWagon1, freightWagon2;
    Wagon freightWagon9001, freightWagon9002;

    @BeforeEach
    public void Setup() {
        Locomotive rembrandt = new Locomotive(24531, 8);
        passengerTrain = new Train(rembrandt, "Amsterdam", "Paris");
        passengerWagon8001 = (Wagon)(Object)new PassengerWagon(8001,32);
        passengerTrain.setFirstWagon(passengerWagon8001);
        passengerWagon8002 = (Wagon)(Object)new PassengerWagon(8002,32);
        passengerWagon8001.attachTail(passengerWagon8002);
        Wagon wagon = passengerWagon8002.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8003,18));
        wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8004,44));
        wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8005,44));
        wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8006,44));
        wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8007,40));
        Locomotive vanGogh = new Locomotive(29123, 7);
        trainWithoutWagons = new Train(vanGogh, "Amsterdam", "London");
        Locomotive clusius = new Locomotive(63427, 50);
        freightTrain = new Train(clusius, "Amsterdam", "Berlin");
        freightWagon9001 = (Wagon)(Object)new FreightWagon(9001,50000);
        freightTrain.setFirstWagon(freightWagon9001);
        freightWagon9002 = (Wagon)(Object)new FreightWagon(9002,40000);
        freightWagon9001.attachTail(freightWagon9002);
        freightWagon9002.attachTail((Wagon)(Object)new FreightWagon(9003,30000));
        passengerWagon1 = (Wagon)(Object)new PassengerWagon(8011,50);
        passengerWagon2 = (Wagon)(Object)new PassengerWagon(8012,50);
        passengerWagon3 = (Wagon)(Object)new PassengerWagon(8013,50);
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        freightWagon1 = (Wagon)(Object)new FreightWagon(9011,60000);
        freightWagon2 = (Wagon)(Object)new FreightWagon(9012,60000);
        freightWagon1.attachTail(freightWagon2);
        passengerWagon1 = (Wagon)(Object)new PassengerWagon(8001, 36);
        passengerWagon2 = (Wagon)(Object)new PassengerWagon(8002, 18);
        passengerWagon3 = (Wagon)(Object)new PassengerWagon(8003, 48);
        passengerWagon4 = (Wagon)(Object)new PassengerWagon(8004, 44);
        freightWagon1 = (Wagon)(Object)new FreightWagon(9001, 50000);
        freightWagon2 = (Wagon)(Object)new FreightWagon(9002, 60000);
    }
}
