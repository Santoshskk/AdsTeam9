package models;
public class PassengerWagon extends Wagon {

    public int numberOfSeats;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSeats=numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    //De normale equals methode deed het niet dus heb ik een nieuwe moeten maken
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // zelfde objectreferentie
        }else if  (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PassengerWagon obj2 = (PassengerWagon) obj;
        return this.getId() == obj2.getId() && this.getNumberOfSeats() == obj2.getNumberOfSeats();
    }

    @Override
    public String toString() {
        return "[Wagon-"+ super.id +"]";
    }
}
