package HW1;
public class p15 {
    private static double[] input = {1, 2, 3, 4, 5};
    private static double[] output = {119.77, 118.85, 123.22, 123.45, 122.44};

    private static double[][] matrix = new double[5][5];
    private static double[][] inverseMatrix = new double[5][5];
    private static double[][] adj = new double[5][5];
    private static double[] coefficients = new double[6];

    private static int numOperations = 0;
    private static String result;
    
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();      

        //finds coefficients a_i for the polynomial
        findCoefficients();

        System.out.println();

        //The below 6 function calls are for getting outputs for P4(t) given an input t
        // usePolynomial(1);
        // usePolynomial(2);
        // usePolynomial(3);
        // usePolynomial(4);
        // usePolynomial(5);
        usePolynomial(6);

        long end = System.currentTimeMillis();
        System.out.println("Prediction: " + result);
        System.out.println("Operation time took " + (end - start) + " Milliseconds.");
        System.out.println("There were " + numOperations + " operations.");
        System.out.println("The estimated operations per second rate is " + (1000 * (numOperations) / (end - start)));
    }

    public static void usePolynomial(int input) {
        double output = 0;

        for(int i=0; i<coefficients.length; i++) {
            output += Math.pow(input, coefficients.length - i - 1) * coefficients[i]; 
            // 4 3 2 1 0 : (3 + 2 + 1 + 1) + (2 + 2 + 1 + 1) + 2(1 + 2 + 1 + 1) + (2 + 1 + 1)= 7 + 6 + 10 + 4 = 27 + 5 loops from 4 increments
        }

        numOperations += 31;
        result = "(" + input + ", " + output + ")";
    }

    public static double[] findCoefficients() {
        //sets up the matrix with given data point inputs
        initializeMatrix();

        //finds the matrix to help calculate inverse matrix
        initializeAdjMatrix();

        //calculates the inverse matrix using Adj matrix and determinant of matrix
        initializeInverseMatrix();

        //The calculate matrices were actually transposed due to programming error but I had no time to correct so I transposed them so they are formatted
        correctMatrix();

        //coefficients vector a, inputs forming matrix B, outputs forming vector c such that its Ba = c, this finds a by a = B^-1 c
        coefficients = matrixByVectorMultiplication(inverseMatrix, output);

        //For printing out polynomial, not formatted ideally due to time constraints (commented out)
        for(int i=0; i<coefficients.length - 1; i++) {
            System.out.print(coefficients[i] + "x^" + (coefficients.length - i - 1) + " + ");
        }
        System.out.println(coefficients[4]);

        return coefficients;
    }

    public static void initializeMatrix() { //Use points t=1 to t=5 to create matrix
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                matrix[i][j] = Math.pow(input[i], matrix.length - 1 - j);
                numOperations += matrix.length - j + 2;
            }

            numOperations ++;
        }
    }

    //calculates determinant of a matrix
    public static double getDet(double[][] A) {
        //base case: 2 by 2 matrix which is easy formula below
        if(A.length == 2 && A[0].length == 2) {
            numOperations += 3;
            return (A[0][0] * A[1][1]) - (A[0][1] * A[1][0]);
        }

        //recursive case to recursively calculate using sub matrices
        double determinant = 0;
        for(int j=0; j<A[0].length; j++) {
            determinant += ((int) Math.pow(-1, j) * A[0][j] * getDet(initializeSubMatrix(0, j, A)));
            numOperations += j + 3;
        }

        return determinant;
    }

    //for helping calculate determinant and inverse matrix
    public static double[][] initializeSubMatrix(int row, int col, double[][] A) {
        double[][] subMatrix = new double[A.length - 1][A[0].length - 1]; 
        numOperations += 2;
        int r = 0, c = 0;

        for(int i=0; i<A.length; i++) {
            if(i != row) {
                c = 0;

                for(int j=0; j<A[i].length; j++) {
                    if(j != col) {
                        subMatrix[r][c] = A[i][j];
                        c++;
                        numOperations ++;
                    }

                    numOperations ++;

                }

                r++;

                numOperations ++;
            } 

            numOperations ++;
        }

        return subMatrix;
    }

    //calculates Adj matrix which is 1 step away from being scaled to inverse matrix
    public static void initializeAdjMatrix() {
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                adj[i][j] = getDet(initializeSubMatrix(i, j, matrix));
                numOperations ++;
            }
        }
    }

    //Transpose the transposed matrix cus I forgot to program correctly so this fixes the errors at cost of extra operations
    public static void correctMatrix() {
        double[][] inv = new double[inverseMatrix.length][inverseMatrix[0].length];
        for(int i=0; i<inverseMatrix[0].length; i++) {
            for(int j=0; j<inverseMatrix.length; j++) {
                inv[j][i] = inverseMatrix[i][j];
                numOperations ++;
            }
        }

        inverseMatrix = inv;
    }

    //Configures inverse matrix
    public static void initializeInverseMatrix() {
        double determinant = getDet(matrix);

        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                inverseMatrix[i][j] = (int) Math.pow(-1, i + j) * adj[i][j] / determinant;
                numOperations += (i+j) + 2;
            }
        }
    }

    //To help find coefficients vector by multiplying inverse matrix to output vector
    public static double[] matrixByVectorMultiplication(double[][] A, double[] x) {
        double[] vector = new double[A.length];

        for(int i=0; i<A.length; i++) {
            vector[i] = 0;

            for(int j=0; j<A[i].length; j++) {
                vector[i] += (A[i][j] * x[j]);
                numOperations += 3;
            }
        }

        return vector;
    }
}
