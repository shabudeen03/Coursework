package assmt9;

public class UltimatePlayer extends Person {
    private static int jerseyNumber = 0;
    private String position;

    public UltimatePlayer(String firstName, String lastName, double power, String position) {
        super(firstName, lastName, power);
        this.position = position;
        jerseyNumber++;
    }

    public String getPosition() {
        return position;
    }

    public double throwDisc() {
        return getPower() * 4;
    }   

    public String toString() {
        return super.toString() + "\n   Jersey #: " + jerseyNumber + "\n   Position: " + position;
    }
}
