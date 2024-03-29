package models;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {

        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon lastWagon = this;
        //loops till the end of the sequence
        while (lastWagon.hasNextWagon()) {
            lastWagon = lastWagon.getNextWagon();
        }
        return lastWagon;
    }

    /**
     * @return the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        int length = 0;

        Wagon mainWagon = this;
        //loops till there is no wagon next
        while (mainWagon != null) {
            length++;
            mainWagon = mainWagon.getNextWagon();
        }
        return length;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *                               The exception should include a message that reports the conflicting connection,
     *                               e.g.: "%s is already pulling %s"
     *                               or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {

        if (tail == null) {
            return;
        }

        if (this.hasNextWagon()) {
            throw new IllegalStateException(
                    String.format("%s is already pulling %s", this, this.getNextWagon())
            );
        } else if (tail.hasPreviousWagon()) {
            throw new IllegalStateException(
                    String.format("%s has already been attached to %s", tail, tail.getPreviousWagon())
            );
        }
        // attaches the tail wagon to "this"
        this.nextWagon = tail;
        tail.previousWagon = this;
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {

        if (!this.hasNextWagon()) {
            return null;
        }

        Wagon wagonToDetachTail = this.getNextWagon();

        //Detach between this and wagonToDetach
        this.nextWagon = null;
        wagonToDetachTail.previousWagon = null;

        //logical name
        Wagon firstWagonOfTail = wagonToDetachTail;

        return firstWagonOfTail;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {


        if (!this.hasPreviousWagon()) {
            return null;
        }

        Wagon wagonToDetachFront = this.getPreviousWagon();

        //Detach between this and wagonToDetach
        this.previousWagon = null;
        wagonToDetachFront.nextWagon = null;

        //logical name
        Wagon formerpreviousWagon = wagonToDetachFront;

        return formerpreviousWagon;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        //deletes former connections
        if (this.hasPreviousWagon()) {
            this.detachFront();
        }

        if (front.hasNextWagon()) {
            front.detachTail();
        }
        //links "this" behind the front
        this.nextWagon = front.getNextWagon();
        front.attachTail(this);

        //the previous wagon of the current wagon is connected to the front
        if (this.hasNextWagon()) {
            this.getNextWagon().previousWagon = front;
        }
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        if (this.previousWagon != null) {
            this.previousWagon.nextWagon = this.nextWagon;// this skips "this" and points to the next one
        }
        if (this.nextWagon != null) {
            this.nextWagon.previousWagon = this.previousWagon;// this skips the "this" and goes to the next one
        }
        //connections of the current are detached
        this.previousWagon = null;
        this.nextWagon = null;
    }

    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        Wagon lastWagon = this.getLastWagonAttached();
        Wagon newOrder = lastWagon; //new variable

        // loops till there are no more previousWagons
        while (lastWagon.hasPreviousWagon()) {
            Wagon beforeLastWagon = lastWagon.previousWagon;
            beforeLastWagon.removeFromSequence();
            newOrder.attachTail(beforeLastWagon);


            newOrder = beforeLastWagon;
            // stop if the beforeLastWagon and this the same
            if (beforeLastWagon == this) {
                break;
            }
        }

        return lastWagon;
    }




}
