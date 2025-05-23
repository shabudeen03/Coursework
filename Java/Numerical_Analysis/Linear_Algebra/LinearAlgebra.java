package HW2;

public class LinearAlgebra {
    public static double[] rowOperations(double[][] A, double[] b) {
        int n = A.length;
    
        // Perform row reduction on the augmented matrix
        for (int k = 0; k < n; k++) {
            int maxRow = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(A[i][k]) > Math.abs(A[maxRow][k])) {
                    maxRow = i;
                }
            }
            double[] temp = A[k];
            A[k] = A[maxRow];
            A[maxRow] = temp;
            double t = b[k];
            b[k] = b[maxRow];
            b[maxRow] = t;
    
            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                b[i] -= factor * b[k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
        }
    
        // Solve for x by back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

    public static void gaussianElim() {
        // Define the coefficients of the system of linear equations
        double[][] A = {{2, 1, -1}, {-3, -1, 2}, {-2, 1, 2}};
        double[] b = {8, -11, -3};

        // Apply Gaussian elimination
        int n = b.length;
        for (int i = 0; i < n; i++) {
            // Search for maximum in this column
            double maxEl = Math.abs(A[i][i]);
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(A[k][i]) > maxEl) {
                    maxEl = Math.abs(A[k][i]);
                    maxRow = k;
                }
            }

            // Swap maximum row with current row
            for (int k = i; k < n + 1; k++) {
                double tmp = A[maxRow][k];
                A[maxRow][k] = A[i][k];
                A[i][k] = tmp;
            }

            // Make all rows below this one 0 in current column
            for (int k = i + 1; k < n; k++) {
                double c = -A[k][i] / A[i][i];
                for (int j = i; j < n + 1; j++) {
                    if (i == j) {
                        A[k][j] = 0;
                    } else {
                        A[k][j] += c * A[i][j];
                    }
                }
            }
        }

        // Solve the equation Ax = b for x
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = A[i][n] / A[i][i];
            for (int k = i - 1; k >= 0; k--) {
                A[k][n] -= A[k][i] * x[i];
            }
        }

        // Print the solution
        for (int i = 0; i < n; i++) {
            System.out.printf("x%d = %.2f\n", i + 1, x[i]);
        }
    }

    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        double det = 0;
        if (n == 1) {
            det = matrix[0][0];
        } else if (n == 2) {
            det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            for (int j = 0; j < n; j++) {
                double[][] submatrix = new double[n-1][n-1];
                for (int i = 1; i < n; i++) {
                    for (int k = 0; k < n; k++) {
                        if (k < j) {
                            submatrix[i-1][k] = matrix[i][k];
                        } else if (k > j) {
                            submatrix[i-1][k-1] = matrix[i][k];
                        }
                    }
                }
                det += Math.pow(-1, j) * matrix[0][j] * determinant(submatrix);
            }
        }
        return det;
    }

    public static double[] solve(double[][] coefficients, double[] constants) {
        int n = constants.length;
        double[] solution = new double[n];
        
        double detA = determinant(coefficients);
        
        for (int i = 0; i < n; i++) {
            double[][] matrix = new double[n][n];
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    for (int k = 0; k < n; k++) {
                        matrix[k][j] = constants[k];
                    }
                } else {
                    for (int k = 0; k < n; k++) {
                        matrix[k][j] = coefficients[k][j];
                    }
                }
            }
            solution[i] = determinant(matrix) / detA;
        }
        
        return solution;
    }

    public static void cramersRule() {
        double[][] coefficients = {{2, 3, -1}, {4, 1, 2}, {3, 5, -2}};
        double[] constants = {7, 4, 1};
        
        double[] solution = solve(coefficients, constants);
        
        for (int i = 0; i < solution.length; i++) {
            System.out.println("x" + (i+1) + " = " + solution[i]);
        }
    }


    static double[][] L;
    static double[][] U;
    public static void luDecomp(double[][] matrix) {
        int n = matrix.length;
        //double[][] L = new double[n][n];
        //double[][] U = new double[n][n];
        // Initialize L and U matrices
        for (int i = 0; i < n; i++) {
            L[i][i] = 1;
            for (int j = 0; j < n; j++) {
                U[i][j] = matrix[i][j];
            }
        }
        // Perform LU decomposition
        for (int k = 0; k < n; k++) {
            for (int i = k + 1; i < n; i++) {
                L[i][k] = U[i][k] / U[k][k];
                for (int j = k; j < n; j++) {
                    U[i][j] = U[i][j] - L[i][k] * U[k][j];
                }
            }
        }
    }

    public static double[] jacobi(double[][] A, double[] b, double tolerance, int maxIterations) {
        int n = b.length;
        double[] x = new double[n];
        double[] x_new = new double[n];
        double error = Double.POSITIVE_INFINITY;
        int iterations = 0;
        
        while (error > tolerance && iterations < maxIterations) {
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        sum += A[i][j] * x[j];
                    }
                }
                x_new[i] = (b[i] - sum) / A[i][i];
            }
            error = calculateError(x, x_new);
            iterations++;
            System.arraycopy(x_new, 0, x, 0, n);
        }
        System.out.println("Number of iterations: " + iterations);
        return x;
    }
    
    private static double calculateError(double[] x, double[] x_new) {
        double error = 0.0;
        for (int i = 0; i < x.length; i++) {
            error += Math.pow(x[i] - x_new[i], 2);
        }
        return Math.sqrt(error);
    }

    public static void jacobiSolver() {
        double[][] A = {{4, 1, 0}, {1, 4, 1}, {0, 1, 4}};
        double[] b = {5, 6, 5};
        double tolerance = 1e-5;
        int maxIterations = 100;
        
        double[] x = jacobi(A, b, tolerance, maxIterations);
        
        for (int i = 0; i < x.length; i++) {
            System.out.println("x[" + i + "] = " + x[i]);
        }
    }
}
