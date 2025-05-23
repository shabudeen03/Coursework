public class p1 {
    private static long flops = 0;

    public static void main(String[] args) {
        double[][] A = {{4, -1, 0, -1, 0, 0, 0, 0, 0},
                        {-1, 4, -1, 0, -1, 0, 0, 0, 0}, 
                        {0, -1, 4, 0, 0, -1, 0, 0, 0},
                        {-1, 0, 0, 4, -1, 0, -1, 0, 0},
                        {0, -1, 0, -1, 4, -1, 0, -1, 0},
                        {0, 0, -1, 0, -1, 4, 0, 0, -1},
                        {0, 0, 0, -1, 0, 0, 4, -1, 0},
                        {0, 0, 0, 0, -1, 0, -1, 4, -1},
                        {0, 0, 0, 0, 0, -1, 0, -1, 4}};
        double[] x = new double[9];
        double[] b = {1, 1, 1, 1, 1, 1, 1, 1, 1};

        flops = 0;
        long start = System.nanoTime();

        x = gaussianElimination(A, b);
        //x = jacobi(A, b, 25);
        // x = gaussSeidel(A, b, 10);
        // x = sor(A, b, 10, 1);

        long end = System.nanoTime();

        System.out.print("Solution Vector: ");
        displayVector(x);
        System.out.println("Estimated Floating Point Operations: " + flops);
        System.out.println("Estimated Program runtime (In Nano Seconds): " + (end - start));

        // double[] bCheck = matrixVecterMult(A, x);
        // displayVector(bCheck);
    }

    public static double[] gaussianElimination(double[][] matrix, double[] out) {
        //Get augmented Matrix
        double[][] augmentedMatrix = new double[matrix.length][matrix[0].length + 1];
        for(int i=0; i<matrix.length; i++) {
            //Typical Matrix setting 
            for(int j=0; j<matrix[0].length; j++) {
                augmentedMatrix[i][j] = matrix[i][j];
            }

            //Set b vector in Ax = b to last column of augmented Matrix
            augmentedMatrix[i][matrix[0].length] = out[i];
        }

        //forward elimination
        for (int i = 0; i < augmentedMatrix.length - 1; i++) {
            for (int j = i + 1; j < augmentedMatrix.length; j++) {
                double factor = augmentedMatrix[j][i] / augmentedMatrix[i][i];
                flops++;

                for (int k = i; k < augmentedMatrix[0].length; k++) {
                    augmentedMatrix[j][k] -= factor * augmentedMatrix[i][k];
                    flops += 2;
                }
            }
        }

        //back-substitution
        double[] x = new double[augmentedMatrix.length];
        for (int i = augmentedMatrix.length - 1; i >= 0; i--) {
            double val = 0;

            for (int j = i + 1; j < augmentedMatrix.length; j++) {
                val += augmentedMatrix[i][j] * x[j];
                flops += 2;
            }

            x[i] = (augmentedMatrix[i][augmentedMatrix[0].length - 1] - val) / augmentedMatrix[i][i];
            flops += 2;
        }

        return x;
    }

    //Jacobi Iterations
    public static double[] jacobi(double[][] A, double[] b, int levels) {
        double[] x = new double[A.length];
        double[] newX = new double[A.length];

        //Set up so you have D, R, D^-1
        double[][] D = new double[A.length][A[0].length];
        double[][] invD = new double[A.length][A[0].length];
        double[][] R = new double[A.length][A[0].length];

        for(int i=0; i<A.length; i++) {
            for(int j=0; j<A[0].length; j++) {
                if(i == j) {
                    D[i][j] = A[i][j];
                    invD[i][j] = 1 / A[i][j];
                    R[i][j] = 0;
                    flops++;
                } else {
                    D[i][j] = 0;
                    invD[i][j] = 0;
                    R[i][j] = A[i][j];
                }
            }
        }

        //Set X
        for(int i=0; i<x.length; i++) {
            x[i] = 0;
        }
        
        //Iterate levels times or until convergence within tolerance
        for(int i=0; i<levels; i++) {
            newX = matrixVecterMult(R, x); //RX_k
            newX = add(b, newX, -1); //b - RX_k
            newX = matrixVecterMult(invD, newX); //D^-1 x (b - RX_k)
            x = newX;
        }

        return x;
    }

    //Gauss Seidel Method, Different from lecture notes, Wikipedia version
    public static double[] gaussSeidel(double[][] matrix, double[] output, int levels) {
        double[] x = new double[matrix.length];
        double[] prevX = new double[matrix.length];

        for(int i=0; i<levels; i++) {
            for (int j = 0; j < matrix.length; j++) {
                prevX[j] = x[j];
            }

            for (int j = 0; j < matrix.length; j++) {
                double sum = 0.0;
                for (int k = 0; k < matrix.length; k++) {
                    if (k != j) {
                        sum += matrix[j][k] * x[k];
                        flops += 2;
                    }
                }
                x[j] = (output[j] - sum) / matrix[j][j];
                flops += 2;
            }
        }

        return x;
    }

    //Successive Over-relaxation Algorithm, Different from lecture notes, Wikipedia version
    public static double[] sor(double[][] matrix, double[] output, int levels, double w) {
        double[] x = new double[matrix.length]; 
        double[] newX = new double[matrix.length]; 

        for(int i=0; i<levels; i++) {
            for (int j = 0; j < matrix.length; j++) {
                double sigma = 0.0; 

                for (int k = 0; k < matrix.length; k++) {
                    if (k != j) {
                        sigma += matrix[j][k] * newX[k];
                        flops += 2;
                    }
                }

                newX[j] = (1 - w) * x[j] + (w / matrix[j][j]) * (output[j] - sigma);
                flops += 6;
            }

            for(int j=0; j<matrix.length; j++) {
                x[j] = newX[j];
            }
        }

        return x;
    }

    /**
     * Helper functions below
    */
    public static double determinant(double[][] A) {
        if(A.length == 2 && A[0].length == 2) {
            flops += 3;
            return (A[0][0] * A[1][1]) - (A[0][1] * A[1][0]);
        }

        double det = 0;

        for(int i=0; i<A[0].length; i++) {
            flops += 12;
            det += (int) Math.pow(-1, i) * A[0][i] * determinant(subMatrix(A, 0, i));
        }

        return det;
    }

    //Generate Submatrix excluding the given row & column
    public static double[][] subMatrix(double[][] A, int r, int c) {
        int row = 0;
        int col;
        double[][] subA = new double[A.length - 1][A[0].length - 1];

        for(int i=0; i<A.length; i++) {
            if(i != r) {
                col = 0;

                for(int j=0; j<A[i].length; j++) {
                    if(j != c) {
                        subA[row][col++] = A[i][j];
                    }
                }

                row++;
            }
        }

        return subA;
    }

    //Generate Inverse Matrix
    //Have to return transpose of this for matrices n by n s.t. n > 2
    public static double[][] inverse(double[][] A) {
        double[][] inverseA = new double[A.length][A[0].length];
        double det = determinant(A);

        if(A.length == 2) {
            inverseA[0][0] = A[1][1] / det;
            inverseA[0][1] = -1 * A[0][1] / det;
            inverseA[1][0] = -1 * A[1][0] / det;
            inverseA[1][1] = A[0][0] / det;

            flops += 6;

            return inverseA;
        }

        for(int i=0; i<A.length; i++) {
            for(int j=0; j<A[0].length; j++) {
                flops += 11;
                inverseA[i][j] = (int) Math.pow(-1, i + j) * determinant(subMatrix(A, i, j)) / det; 
            }
        }

        return inverseA;
    }

    public static double[] matrixVecterMult(double[][] A, double[] v) {
        double[] output = new double[A.length];

        for(int i=0; i<A.length; i++) {
            output[i] = 0;

            for(int j=0; j<A[i].length; j++) {
                flops += 2;
                output[i] += (A[i][j] * v[j]);
            }
        }

        return output;
    }

    public static double[] add(double[] a, double[] b, int sign) {
        double[] c = new double[a.length];

        for(int i=0; i<c.length; i++) {
            flops += 2;
            c[i] = a[i] + (sign * b[i]);
        }

        return c;
    }

    public static void displayVector(double[] x) {
        for(int i=0; i<x.length; i++) {
            System.out.print(x[i] + " ");
        }

        System.out.println();
    }
}