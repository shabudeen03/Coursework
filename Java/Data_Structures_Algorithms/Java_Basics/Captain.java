package assmt9;

public class Captain extends UltimatePlayer {
    private boolean type;

    public Captain(String firstName, String lastName, double power, String position, boolean type) {
        super(firstName, lastName, power, position);
        this.type = type;
    }

    public double throwDisc() {
        return getPower() * 5;
    }

    public String toString() {
        return super.toString() + "\n   Captain: " + (type ? "offense":"defense");
    }
}
