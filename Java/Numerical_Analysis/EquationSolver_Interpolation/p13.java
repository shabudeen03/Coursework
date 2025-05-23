package HW1;
public class p13 {
    private static double root = 0.865474; //rounded well enough, kept just in case as solution
    private static double tolerance = 0.00005; //tolerance level of within 4 decimal places

    public static void main(String[] args) {
        long start = System.currentTimeMillis(); //start considering time it took to calculate root
        double root = secantMethod(1, 0, 1); //function that calculators root
        long end = System.currentTimeMillis(); //time considered as the point of after root has been calculated

        System.out.println("Operation time took " + (end - start) + " Milliseconds."); //difference of two times is how long it took to calculate root
        System.out.println("Approximate root is " + root + "."); //Print out root
    }

    public static double secantMethod(double prev1, double prev2, int count) {
        if(Math.abs(prev1 - prev2) < tolerance && count > 1) { //count > 1 for default purposes with different internals
            System.out.println("Secant Method took " + count + " iteration steps."); //to help guesstimate Flops
            return prev1; //return if converging within 4 decimal places
        }

        return secantMethod(prev1 - ((g(prev1) * (prev1 - prev2)) / (g(prev1) - g(prev2))), prev1, count + 1); //determine new potential root from current 2 prior values, store current x as recent previous root and keep finding
    }

    public static double g(double x) { //the function g(x)
        return Math.cos(x) - Math.pow(x, 3);
    }
}
