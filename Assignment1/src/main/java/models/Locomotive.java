package models;

public class Locomotive {
    public int locNumber;
    public int maxWagons;


    public Locomotive(int locNumber, int maxWagons) {
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    public int getMaxWagons() {
        return maxWagons;
    }

    public int getLocNumber() {
        return locNumber;
    }
}
