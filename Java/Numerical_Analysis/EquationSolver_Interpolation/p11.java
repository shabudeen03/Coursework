package HW1;
public class p11 {
    private static double root = 0.865474; //rounded well enough, kept just in case as solution
    private static double tolerance = 0.00005; //tolerance level of within 4 decimal places

    public static void main(String[] args) {
        long start = System.currentTimeMillis(); //start considering time it took to calculate root
        double root = bisectionMethod(0, 1, 0, 1); //function that calculators root
        long end = System.currentTimeMillis(); //time considered as the point of after root has been calculated

        System.out.println("Operation time took " + (end - start) + " Milliseconds."); //difference of two times is how long it took to calculate root
        System.out.println("Approximate root is " + root + "."); //Print out root
    }

    public static double bisectionMethod(double a, double b, double prevSoln, int count) {
        double x = (a + b) / 2; //midpoint x

        if(Math.abs(x - prevSoln) < tolerance && count > 1) { //count > 1 for default purposes with different internals
            System.out.println("Bisection Method took " + count + " iteration steps."); //to help guesstimate Flops
            return x; //return if midpoint is converging within 4 decimal places
        }

        if(g(a) * g(x) < 0) {
            //root in [a, x]
            b = x;
        } else {
            //root in [x, b]
            a = x;
        }

        return bisectionMethod(a, b, x, count + 1); //continue finding root with updated interval of where root is located
    }

    public static double g(double x) { //the function g(x)
        return Math.cos(x) - Math.pow(x, 3);
    }
}