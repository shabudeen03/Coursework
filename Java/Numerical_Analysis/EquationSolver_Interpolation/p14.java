package HW1;
public class p14 {
    private static double fp = 0.60352; //solution floating point, not used, kept just in case
    private static double tolerance = 0.00005; //tolerance level of within 4 decimal places

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis(); //start considering time it took to calculate fp
        double fp = fixedPointIterations(0, 0, 1); //function that calculators fp
        long end = System.currentTimeMillis(); //time considered as the point of after fp has been calculated

        System.out.println("Operation time took " + (end - start) + " Milliseconds."); //difference of two times is how long it took to calculate fp
        System.out.println("Approximate fixed point is " + fp + "."); //Print out fp
    }

    public static double fixedPointIterations(double x, double prevSoln, int count) throws Exception {
        //initial value x = 0
        if(Math.abs(x - prevSoln) < tolerance && count > 1) { //count > 1 for default purposes with different internals
            System.out.println("There were " + count + " iteration steps."); //to help guesstimate Flops
            return x; //return fixed point if within tolerance
        }

        return fixedPointIterations(g(x), x, count + 1); //continue finding
    }


    //Equal alternative to original function to help find floating point as original diverged
    public static double g(double x) {
        return (Math.cos(x) + 2 * Math.pow(x, 3)) / (1 + (3 * x * x));
    }


    //Original function below commented out, diverged so above function used
    // public static double g(double x) {
    //     return Math.cos(x) - Math.pow(x, 3);
    // }
}
