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
     *
     * @return
     */
    public boolean hasWagons() {
        return firstWagon != null;
    }


    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     *
     * @return
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     *
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
     *
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        firstWagon = wagon;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int count = 0;
        Wagon currentWagon = firstWagon;

        //loop keeps going till currentwagon is null
        while (currentWagon != null) {
            currentWagon = currentWagon.getNextWagon();
            count++;
        }

        return count;
    }


    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = firstWagon;
        if (currentWagon == null) {
            return null;
        }
        Wagon lastWagon = currentWagon.getLastWagonAttached();
        return lastWagon;
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        Wagon currentWagon = firstWagon;
        int numberOfSeats = 0;
        while (currentWagon != null) {
            if (currentWagon instanceof PassengerWagon) {
                numberOfSeats += ((PassengerWagon) currentWagon).getNumberOfSeats();
            } else {
                numberOfSeats = 0;
            }
            currentWagon = currentWagon.getNextWagon();
        }
        return numberOfSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        // TODO
        Wagon currentWagon = firstWagon;

        int maxWeight = 0;
        while (currentWagon != null) {
            if (currentWagon instanceof FreightWagon) {
                maxWeight += ((FreightWagon) currentWagon).getMaxWeight();
            } else {
                maxWeight = 0;
            }
            currentWagon = currentWagon.getNextWagon();
        }
        return maxWeight;

    }

    /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     *
     * @param position
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon currentWagon = firstWagon;

        if (currentWagon == null) {
            return null;
        }

        int WagonLength = getNumberOfWagons();

        if (position >= WagonLength || position < 0) {
            return null;
        }

        // Loop through wagons
        for (int i = 0; i < WagonLength; i++) {
            if (i == position) {
                return currentWagon;
            }
            currentWagon = currentWagon.getNextWagon();
        }
        return null;
    }


    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon currentWagon = firstWagon;

        if (currentWagon == null) {
            return null;
        }

        //the length of the sequence of wagons towards the end of its tail
        int WagonLength = currentWagon.getSequenceLength();

        for (int i = 0; i <= WagonLength; i++) {
            if (currentWagon == null) {
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
     *
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        int wagonSequenceCount = wagon.getSequenceLength();
        int wagonCount = getNumberOfWagons();
        int engineCapacity = engine.getMaxWagons();
        boolean correctWagon = false;

        if (wagonCount == 0) {
            correctWagon = true;
        }

        if ((wagon instanceof PassengerWagon && isPassengerTrain()) ||
                (wagon instanceof FreightWagon && isFreightTrain())) {
            correctWagon = true;
        }

        if (correctWagon) {
            boolean isWagonPartOfTrain = findWagonById(wagon.id) != null;
            if (wagonCount + wagonSequenceCount <= engineCapacity && !isWagonPartOfTrain) {
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
     *
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        if (wagon == null) {
            return false;
        }

        // Check if the wagon is already part of this train
        if (findWagonById(wagon.id) != null) {
            return false;
        }

        // Detach the wagon
        if (wagon.hasPreviousWagon()) {
            wagon.getPreviousWagon().setNextWagon(null);
            wagon.setPreviousWagon(null);
        }

        // Check if there's capacity to attach the wagon sequence
        int totalWagons = getNumberOfWagons();
        int wagonsToAttach = wagon.getSequenceLength();
        if (totalWagons + wagonsToAttach > this.engine.getMaxWagons()) {
            return false; // Not enough capacity to attach the wagons
        }

        // Attach the wagon (or sequence) to the rear of the train
        if (this.firstWagon == null) {
            this.firstWagon = wagon;
        } else {
            Wagon lastWagon = this.firstWagon.getLastWagonAttached();
            lastWagon.setNextWagon(wagon);
            wagon.setPreviousWagon(lastWagon);
        }

        return true;
    }



    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     *
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if (!canAttach(wagon)) {
            return false;
        }
        //Detachment
        if (wagon.hasPreviousWagon()) {
            wagon.removeFromSequence();
        }

        //Insert wagon at the Front with Sequence Attachment to the tail
        if (hasWagons()) {
            Wagon tailOfInsertingSequence = findTailOfSequence(wagon);
            tailOfInsertingSequence.attachTail(firstWagon);
        }

        //this wagon = first wagon
        firstWagon = wagon;
        return true;
    }
    private Wagon findTailOfSequence(Wagon wagon) {
        Wagon Tail = wagon;
        while (Tail.hasNextWagon()) {
            Tail = Tail.getNextWagon();
        }
        return Tail;
    }


    public boolean insertAtPosition(int position, Wagon wagon) {
        boolean isWagonPartOfTrain = findWagonById(wagon.id) != null;

        // Check if the wagon is null or already part of this train
        if (isWagonPartOfTrain) {
            return false;
        }

        // Calculate the total number of wagons after the insert
        int totalWagonsAfterInsertion = getNumberOfWagons() + wagon.getSequenceLength();

        // Check if the position is valid and if the train can accommodate the new wagons
        if (position < 0 || position > getNumberOfWagons() || totalWagonsAfterInsertion > this.engine.getMaxWagons()) {
            return false;
        }

        // Detach the wagon
        if (wagon.hasPreviousWagon()) {
            wagon.getPreviousWagon().setNextWagon(null);
            wagon.setPreviousWagon(null);
        }

        // If the train is empty or the position is at the end, just attach to the rear
        if (this.firstWagon == null || position == getNumberOfWagons()) {
            attachToRear(wagon);
            return true;
        }

        if (position == 0) {
            insertAtFront(wagon);
            return true;
        }

        // Navigate to the specified position
        Wagon currentWagon = this.firstWagon;
        for (int i = 0; i < position - 1; i++) {
            currentWagon = currentWagon.getNextWagon();
        }

        // Insert the sequence at the specified position and reattach the rest
        Wagon nextWagon = currentWagon.getNextWagon();
        currentWagon.setNextWagon(wagon);
        wagon.setPreviousWagon(currentWagon);

        Wagon lastInsertedWagon = wagon.getLastWagonAttached();
        lastInsertedWagon.setNextWagon(nextWagon);
        if (nextWagon != null) {
            nextWagon.setPreviousWagon(lastInsertedWagon);
        }

        return true;
    }



    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // Check if toTrain is not the same as the current train
        if (this == toTrain) {
            return false;
        }

        // FindwagonId in the current train
        Wagon wagonToMove = findWagonById(wagonId);
        if (wagonToMove == null) {
            return false;
        }

        // Check compatibility
        if (!toTrain.canAttach(wagonToMove)) {
            return false;
        }

        // Remove the wagon from the current train
        if (wagonToMove.hasPreviousWagon()) {
            wagonToMove.getPreviousWagon().setNextWagon(wagonToMove.getNextWagon());
        } else {
            // If the wagon to move is the first wagon, update the firstWagon reference
            this.firstWagon = wagonToMove.getNextWagon();
        }
        if (wagonToMove.hasNextWagon()) {
            wagonToMove.getNextWagon().setPreviousWagon(wagonToMove.getPreviousWagon());
        }

        // Detach the wagon from its previous and next wagons
        wagonToMove.setPreviousWagon(null);
        wagonToMove.setNextWagon(null);

        // Attach the wagon to the rear of toTrain
        toTrain.attachToRear(wagonToMove);

        return true;
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
        // Check if toTrain is the same as the current train or if position is invalid
        if (this == toTrain || position < 0 || position >= getNumberOfWagons()) {
            return false;
        }

        // Navigate to the wagon at the given position
        Wagon currentWagon = firstWagon;
        for (int i = 0; i < position; i++) {
            currentWagon = currentWagon.getNextWagon();
        }

        // Check compatibility of trains and capacity of toTrain's engine
        if (!toTrain.canAttach(currentWagon)) {
            return false;
        }

        // Detach the sequence starting from currentWagon from this train
        if (currentWagon.hasPreviousWagon()) {
            currentWagon.getPreviousWagon().setNextWagon(null);
            currentWagon.setPreviousWagon(null);
        } else {
            // If the currentWagon is the first wagon, update the firstWagon reference of this train
            this.firstWagon = null;
        }

        // Attach the sequence to the rear of toTrain
        toTrain.attachToRear(currentWagon);

        return true;
    }


    public void reverse() {
        if (firstWagon == null || !firstWagon.hasNextWagon()) {
            // No wagons - so no need to reverse
            return;
        }
        Wagon currentWagon = firstWagon;
        Wagon prevWagon = null;

        //reverse  direction
        while (currentWagon != null) {
            Wagon nextWagon = currentWagon.getNextWagon();
            currentWagon.setNextWagon(prevWagon);
            currentWagon.setPreviousWagon(nextWagon);
            prevWagon = currentWagon;
            currentWagon = nextWagon;
        }

        // Update the first wagon to be the original last wagon
        firstWagon = prevWagon;
    }


    // TODO string representation of a train


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Add the locomotive
        sb.append("[").append("Loco").append(engine.getLocNumber()).append("]"); // Assuming engine has a meaningful toString()

        // Add each wagon
        Wagon currentWagon = firstWagon;
        while (currentWagon != null) {
            sb.append("[").append(currentWagon).append("]"); // Assuming Wagon has a meaningful toString()
            currentWagon = currentWagon.getNextWagon();
        }

        // Add train details
        sb.append(" with ").append(getNumberOfWagons())
                .append(" wagons from ").append(origin)
                .append(" to ").append(destination);

        return sb.toString();
    }

}
