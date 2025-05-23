public class Unit4Notes {
    //Given a matrix of size A, get determinant
    public static double determinant(double[][] A) {
        if(A.length == 2 && A[0].length == 2) {
            return (A[0][0] * A[1][1]) - (A[0][1] * A[1][0]);
        }

        double det = 0;

        for(int i=0; i<A[0].length; i++) {
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

            return inverseA;
        }

        for(int i=0; i<A.length; i++) {
            for(int j=0; j<A[0].length; j++) {
                inverseA[i][j] = (int) Math.pow(-1, i + j) * determinant(subMatrix(A, i, j)) / det; 
            }
        }

        return inverseA;
    }

    //Print Matrix
    public static void printMatrix(double[][] A) {
        String out = "";

        for(double[] row:A) {
            for(double cell:row) {
                out += cell + " ";
            }

            out += "\n";
        }

        System.out.println(out);
    }

    public static double[] matrixVecterMult(double[][] A, double[] v) {
        double[] output = new double[A.length];

        for(int i=0; i<A.length; i++) {
            output[i] = 0;

            for(int j=0; j<A[i].length; j++) {
                output[i] += (A[i][j] * v[j]);
            }
        }

        return output;
    }

    public static double[][] matrixMatrixMult(double[][] A, double[][] B) {
        double[][] C = new double[A.length][B[0].length];

        for(int i=0; i<A.length; i++) {
            for(int j=0; j<B[0].length; j++) {
                for(int k=0; k<A[0].length; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    public static double[][] strassen(double[][] A, double[][] B) {
        double[][] C = new double[A.length][A[0].length];

        if(A.length == 1) {
            C[0][0] = A[0][0] * B[0][0];
            return C;    
        }

        double[][] a11 = new double[A.length / 2][A.length / 2];
        double[][] a12 = new double[A.length / 2][A.length / 2];
        double[][] a21 = new double[A.length / 2][A.length / 2];
        double[][] a22 = new double[A.length / 2][A.length / 2];
        
        double[][] b11 = new double[B.length / 2][B.length / 2];
        double[][] b12 = new double[B.length / 2][B.length / 2];
        double[][] b21 = new double[B.length / 2][B.length / 2];
        double[][] b22 = new double[B.length / 2][B.length / 2];

        for(int i=0; i<A.length / 2; i++) {
            for(int j=0; j<A.length / 2; j++) {
                a11[i][j] = A[i][j];
                a12[i][j] = A[i][j + A.length / 2];
                a21[i][j] = A[i + A.length / 2][j];
                a22[i][j] = A[i + A.length / 2][j + A.length / 2]; 
    
                b11[i][j] = B[i][j];
                b12[i][j] = B[i][j + B.length / 2];
                b21[i][j] = B[i + B.length / 2][j];
                b22[i][j] = B[i + B.length / 2][j + B.length / 2];
            }
        }

        double[][] m1 = strassen(add(a11, a22, 1), add(b11, b22, 1));
        double[][] m2 = strassen(add(a21, a22, 1), b11);
        double[][] m3 = strassen(a11, add(b12, b22, -1));   
        double[][] m4 = strassen(a22, add(b21, b11, -1));
        double[][] m5 = strassen(add(a11, a12, 1), b22);
        double[][] m6 = strassen(add(a21, a11, -1), add(b11, b12, 1));
        double[][] m7 = strassen(add(a12, a22, -1), add(b21, b22, 1));

        double[][] c11 = add(add(m1, add(m4, m5, -1), 1), m7, 1);
        double[][] c12 = add(m3, m5, 1);
        double[][] c21 = add(m2, m4, 1);
        double[][] c22 = add(add(m3, add(m1, m2, -1), 1), m6, 1);

        return combine(c11, c12, c21, c22);
    }

    //Returns the sum of 2 matrices
    public static double[][] add(double[][] mat1, double[][] mat2, int sign) {
        double[][] mat3 = new double[mat1.length][mat1[0].length];

        for(int i=0; i<mat1.length; i++) {
            for(int j=0; j<mat1[0].length; j++) {
                mat3[i][j] = mat1[i][j] + (sign * mat2[i][j]);
            }
        }

        return mat3;
    }

    //Combines the 4 submatrices into one big matrix
    public static double[][] combine(double[][] a11, double[][] a12, double[][] a21, double[][] a22) {
        double[][] a = new double[a11.length * 2][a11[0].length * 2];

        for(int i=0; i<a11.length; i++) {
            for(int j=0; j<a11[0].length; j++) {
                a[i][j] = a11[i][j];
                a[i][j + a11[0].length] = a12[i][j];
                a[i + a11.length][j] = a21[i][j];
                a[i + a11.length][j + a11[0].length] = a22[i][j];
            }
        }

        return a;
    }

    //Cramers Rule
    public static double[] cramers(double[][] A, double[] b) {
        double[] x = new double[A.length];
        double det = determinant(A);

        for(int i=0; i<x.length; i++) {
            double[][] newA = new double[A.length][A[0].length];
            for(int r=0; r<A.length; r++) {
                for(int c=0; c<A[0].length; c++) {
                    if(c == i) {
                        newA[r][c] = b[r];
                    } else {
                        newA[r][c] = A[r][c];
                    }
                }
            }

            x[i] = determinant(newA) / det;
        }

        return x;
    }

    //Jacobi Iterations
    private static double tolerance;
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
        double absError;
        for(int i=0; i<levels; i++) {
            newX = matrixVecterMult(R, x); //RX_k
            newX = addVectors(b, newX, -1); //b - RX_k
            newX = matrixVecterMult(invD, newX); //D^-1 x (b - RX_k)

            absError = 0;
            for(int j=0; j<x.length; j++) {
                absError += Math.abs(x[j] - newX[j]);
            }

            x = newX;

            if(absError < tolerance) {
                break;
            }
        }

        return x;
    }

    //Add/Subtract 2 vectors
    public static double[] addVectors(double[] a, double[] b, int sign) {
        double[] c = new double[a.length];
        for(int i=0; i<a.length; i++) {
            c[i] = a[i] + (sign * b[i]);
        }

        return c;
    }


    //Transpose a matrix
    public static double[][] transpose(double[][] A) {
        double[][] A_T = new double[A[0].length][A.length];

        for(int i=0; i<A.length; i++) {
            for(int j=0; j<A[0].length; j++) {
                A_T[j][i] = A[i][j];
            }
        }

        return A_T;
    }

    public static double[][] cholesky(double[][] A) {
        double[][] R = new double[A.length][A[0].length];

        for(int k=0; k<A.length; k++) {
            if(A[k][k] < 0) {
                break;
            }

            R[k][k] = Math.sqrt(A[k][k]);

            double[] uT = new double[A.length - k - 1];

            for(int i=0; i<uT.length; i++) {
                uT[i] = A[k][k + i + 1] / R[k][k];
                R[k][k + i + 1] = uT[i];
            }
            
            for(int i=0; i<uT.length; i++) {
                for(int j=0; j<uT.length; j++) {
                    A[k + i + 1][k + j + 1] -= dot(uT, uT);
                }
            }

            printMatrix(A);
        }

        return R;
    }

    public static boolean positiveDefinite(double[][] A, double[] x) {
        
        
        return true;
    }

    public static double dot(double[] a, double[] b) {
        double sum = 0;
        for(int i=0; i<a.length && i<b.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }


    //Need to fix Cholesky Representation
    //Maybe do conjugate method + eigenvalue/eigenvector
    public static void main(String[] args) {
        double[][] A = {{4, -2, 2}, {-2, 2, -4}, {2, -4, 11}};
        double[][] R = cholesky(A);

        printMatrix(R);
    }
}
