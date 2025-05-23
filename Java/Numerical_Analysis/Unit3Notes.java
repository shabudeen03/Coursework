public class Unit3Notes {
    static int flops = 0;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("Outputs below of form (x, y):\n");
        for(int i=-3; i<=3; i++) {
            System.out.println("(" + i + ", " + airy(i) + ")");
        }
        long end = System.currentTimeMillis();
        System.out.println("The process took " + (end - start) + " milliseconds.");
        System.out.println("Estimated floating point operations: " + flops);
    }

    //Central Difference (Derivative)
    public static double centralDiff(double x, double h) {
        return (f(x + h) - f(x - h)) / (2 * h);
    }

    //Forward Difference
    public static double forwardDiff(double x, double h) {
        return (f(x + h) - f(x)) / h;
    }

    //Backward Diff
    public static double backwardDiff(double x, double h) {
        return ((f(x) - f(x - h)) / h);
    }

    static double f(double x) {
        return x;
    }

    static double f(double t, double x) {
        flops += 15; //10 for cos, 2 for pow, 1 + 1 + 1
        return Math.cos((Math.pow(t, 3) / 3) + (x * t));
    }

    static double airy(double x) {
        //double ouptut = integrateMidpoint(0, 25, 25000000, x) / Math.PI;
        //double output = integrateTrapezoid(0, 25, 25000, x) / Math.PI;

        //double output = integrateSimponThird(0, 25, 50000, x) / Math.PI;
        double output = monteCarlo(0, 25, 250000000, x) / Math.PI;
        return output;
    }

    /** Simpson's 1/3 Rule */   
    //a, b represent interval [a, b]
    //n represents # of equidistant points, and x is input from airy(x)
    static double integrateSimponThird(double a, double b, double n, double x) {
        double stepSize = (a + b) / n;
        double output = 0;

        flops += 2;

        for(int i=0; i<n-1; i++) {
            output += f(a, x);
            output += 4 * f(a + stepSize, x);
            output += f(a + 2 * stepSize, x);

            a += 2 * stepSize;
            flops += 9;
        }

        flops += 2;
        return output * stepSize / 3;
    }        

    /** Trapezoid Method */
    //a, b represent interval [a, b]
    //n represents # of trapezoids, and x is input from airy(x)
    static double integrateTrapezoid(double a, double b, double n, double x) {
        double stepSize = (a + b) / n;
        double output = 0;

        flops += 2;

        for(int i=0; i<n; i++) {
            output += f(a, x);
            output += f(a + stepSize, x);

            a += stepSize;
            flops += 4;
        }

        flops += 2;
        return output * stepSize / 2;
    }    

    /** Midpoint Method */
    //a, b represent interval [a, b]
    //n represents number of midpoints, and x is input from airy(x)
    static double integrateMidpoint(double a, double b, int n, double x) {
        double stepSize = (a + b) / n;
        double midpoint;
        double output = 0;

        flops += 2;

        for(int i=0; i<n; i++) {
            midpoint = (2* a + stepSize) / 2;
            a += stepSize;

            output += f(midpoint, x);
            flops += 5;
        }

        flops++;
        return output * stepSize;
    }

    static double monteCarlo(double a, double b, int n, double x) {
        double output = 0;
        double x_n;

        for(int i=0; i<n; i++) {
            x_n = (Math.random() * (b + 1 - a)) + a;
            output += f(x_n, x);
        }

        return (b - a) * output / n;
    }
}
