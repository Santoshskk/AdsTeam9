package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Indicates whether the train has at least one connected Wagon
     * @return
     */
    public boolean hasWagons() {
        return firstWagon != null;
    }


    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int count = 0;
        Wagon currentWagon = firstWagon;

        //loop keeps going till currentwagon is null
        while (currentWagon != null) {
            count++;
            currentWagon = currentWagon.getNextWagon();
        }

        return count;
    }


    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        // TODO
        Wagon currentWagon = firstWagon;
        if(currentWagon == null) {
            return null;
        }
        Wagon lastWagon = currentWagon.getLastWagonAttached();
        return lastWagon;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        Wagon currentWagon = firstWagon;
        int numberOfSeats = 0;
            while (currentWagon != null) {
                if (currentWagon instanceof PassengerWagon) {
                    numberOfSeats += ((PassengerWagon) currentWagon).getNumberOfSeats();
                }
                else {
                    numberOfSeats = 0;
                }
                currentWagon = currentWagon.getNextWagon();
            }
        return numberOfSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        // TODO
        Wagon currentWagon = firstWagon;

        int maxWeight = 0;
        while (currentWagon != null) {
            if (currentWagon instanceof FreightWagon) {
                maxWeight += ((FreightWagon) currentWagon).getMaxWeight();
            }
            else {
                maxWeight = 0;
            }
            currentWagon = currentWagon.getNextWagon();
        }
        return maxWeight;

    }

     /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {

        Wagon currentWagon = firstWagon;

        if(currentWagon == null) {
            return null;
        }

        //the length of the sequence of wagons towards the end of its tail
        int WagonLength =  currentWagon.getSequenceLength();

        for (int i = 0; i < WagonLength; i++) {
           if(i == position) {
               return currentWagon;
           }
            currentWagon = currentWagon.getNextWagon();
        }
        if(position > WagonLength || position < 0 ) {
            return null;
        }
            return null;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon currentWagon = firstWagon;

        if(currentWagon == null) {
            return null;
        }

        //the length of the sequence of wagons towards the end of its tail
        int WagonLength =  currentWagon.getSequenceLength();

        for (int i = 0; i <= WagonLength; i++) {
            if(currentWagon == null) {
                return null;
            }
            if (currentWagon.getId() == wagonId) {
                return currentWagon;
            }
            currentWagon = currentWagon.getNextWagon();
        }

        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {

        int wagonCount = getNumberOfWagons();
        int engineCapacity = engine.getMaxWagons();

        if((wagon instanceof PassengerWagon && isPassengerTrain()) ||
                (wagon instanceof FreightWagon && isFreightTrain())) {
            if(wagonCount <= engineCapacity && findWagonById(wagon.id) == null) {
                return true;
            }

        }
       return false;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 0 <= position <= numWagons
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
     }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  0 <= position < numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    // TODO string representation of a train


    @Override
    public String toString() {
        return "Train{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", engine=" + engine +
                ", firstWagon=" + firstWagon +
                '}';
    }
}
