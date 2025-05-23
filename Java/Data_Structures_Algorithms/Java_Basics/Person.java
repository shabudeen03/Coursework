package assmt9;

public class Person {
    private String firstName;
    private String lastName;
    private double power;

    public Person(String firstName, String lastName, double power) {
        this.firstName = firstName;
        this.lastName = lastName;

        if(power < 1) this.power = 1;
        else if(power > 10) this.power = 10;
        else this.power = power;
    }

    public double throwDisc() {
        return getPower() * 2;
    }

    public double getPower() {
        return power;
    }

    public String toString() {
        return lastName + ", " + firstName + "\n   Power: " + power;
    }
}
