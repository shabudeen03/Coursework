package assmt9;

public class Coach extends Person {
    private String role;

    public Coach(String firstName, String lastName, double power, String role) {
        super(firstName, lastName, power);
        this.role = role;
    }

    public String toString() {
        String[] parts = super.toString().split(" ");
        return parts[0] + " " + parts[1] + "\n   Role: " + role;
    }
}
