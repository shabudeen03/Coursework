package HW2;

public class p1 {
    static double[][] A;
    static double[][] B;
    static double[][] C;
    static long multCounter = 0;
    static long addCounter = 0;
    static long start, end;

    public static void main(String[] args) {
        //Problem 1.1
        start = System.currentTimeMillis();
        part1(1024);
        end = System.currentTimeMillis();
        System.out.println("Time taken to generate matrices: " + (end - start) + "\n\n");

        //Problem 1.2
        start = System.currentTimeMillis();
        C = naive(A, B);
        end = System.currentTimeMillis();
        System.out.println("Time taken to naively multiply matrices: " + (end - start));
        System.out.println("# of Multiplications Done: " + multCounter);
        System.out.println("# of Additions done: " + addCounter + "\n\n");
        multCounter = 0;
        addCounter = 0;
        
        //Problem 1.3
        start = System.currentTimeMillis();
        //C = strassen(A, B);
        C = strassenNLvls(0, 3, A, B);
        end = System.currentTimeMillis();
        System.out.println("Time taken to use strassen: " + (end - start));
        System.out.println("# of Multiplications Done: " + multCounter);
        System.out.println("# of Additions done: " + addCounter + "\n\n");
        multCounter = 0;
        addCounter = 0;

        //Problem 1.4
        part4();
    }

    public static void printMatrix(double[][] matrix) {
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
    }

    public static void part1(int n) {
        A = new double[n][n];
        B = new double[n][n];
        C = new double[n][n];

        generateMatrices(n, A, B);
    }

    public static void generateMatrices(int n, double[][] mat1, double[][] mat2) {
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                mat1[i][j] = (int) (Math.random() * 2) - 1;
                mat2[i][j] = (int) (Math.random() * 2) - 1;
            }
        }
    }

    public static double[][] naive(double[][] mat1, double[][] mat2) {;
        double[][] mat3 = new double[mat1.length][mat2[0].length];

        for(int i=0; i<mat3.length; i++) {
            for(int j=0; j<mat3[0].length; j++) {
                for(int k=0; k<mat1[0].length; k++) {
                    mat3[i][j] += mat1[i][k] * mat2[k][j];

                    multCounter += 1;
                    addCounter += 1;
                }
            }
        }

        return mat3;
    }

    public static double[][] strassenNLvls(int level, int maxLevel, double[][] a, double[][] b) {
        if(level == maxLevel) {
            return naive(a, b);
        }

        double[][] a11 = new double[a.length / 2][a.length / 2];
        double[][] a12 = new double[a.length / 2][a.length / 2];
        double[][] a21 = new double[a.length / 2][a.length / 2];
        double[][] a22 = new double[a.length / 2][a.length / 2];
        
        double[][] b11 = new double[b.length / 2][b.length / 2];
        double[][] b12 = new double[b.length / 2][b.length / 2];
        double[][] b21 = new double[b.length / 2][b.length / 2];
        double[][] b22 = new double[b.length / 2][b.length / 2];

        for(int i=0; i<a.length / 2; i++) {
            for(int j=0; j<a.length / 2; j++) {
                a11[i][j] = a[i][j];
                a12[i][j] = a[i][j + a.length / 2];
                a21[i][j] = a[i + a.length / 2][j];
                a22[i][j] = a[i + a.length / 2][j + a.length / 2]; 
    
                b11[i][j] = b[i][j];
                b12[i][j] = b[i][j + b.length / 2];
                b21[i][j] = b[i + b.length / 2][j];
                b22[i][j] = b[i + b.length / 2][j + b.length / 2];
            }
        }

        double[][] m1 = strassenNLvls(level + 1, maxLevel, add(a11, a22, 1), add(b11, b22, 1));
        double[][] m2 = strassenNLvls(level + 1, maxLevel, add(a21, a22, 1), b11);
        double[][] m3 = strassenNLvls(level + 1, maxLevel, a11, add(b12, b22, -1));   
        double[][] m4 = strassenNLvls(level + 1, maxLevel, a22, add(b21, b11, -1));
        double[][] m5 = strassenNLvls(level + 1, maxLevel, add(a11, a12, 1), b22);
        double[][] m6 = strassenNLvls(level + 1, maxLevel, add(a21, a11, -1), add(b11, b12, 1));
        double[][] m7 = strassenNLvls(level + 1, maxLevel, add(a12, a22, -1), add(b21, b22, 1));

        double[][] c11 = add(add(m1, add(m4, m5, -1), 1), m7, 1);
        double[][] c12 = add(m3, m5, 1);
        double[][] c21 = add(m2, m4, 1);
        double[][] c22 = add(add(m3, add(m1, m2, -1), 1), m6, 1);

        multCounter += 7;
        addCounter += 18;

        return combine(c11, c12, c21, c22);
    }

    public static double[][] strassen(double[][] a, double[][] b) {
        if(a.length == 1) {
            double[][] cBase = new double[a.length][a[0].length];
            cBase[0][0] = a[0][0] * b[0][0];

            multCounter++;
            return cBase;
        }

        double[][] a11 = new double[a.length / 2][a.length / 2];
        double[][] a12 = new double[a.length / 2][a.length / 2];
        double[][] a21 = new double[a.length / 2][a.length / 2];
        double[][] a22 = new double[a.length / 2][a.length / 2];
        
        double[][] b11 = new double[b.length / 2][b.length / 2];
        double[][] b12 = new double[b.length / 2][b.length / 2];
        double[][] b21 = new double[b.length / 2][b.length / 2];
        double[][] b22 = new double[b.length / 2][b.length / 2];

        for(int i=0; i<a.length / 2; i++) {
            for(int j=0; j<a.length / 2; j++) {
                a11[i][j] = a[i][j];
                a12[i][j] = a[i][j + a.length / 2];
                a21[i][j] = a[i + a.length / 2][j];
                a22[i][j] = a[i + a.length / 2][j + a.length / 2]; 
    
                b11[i][j] = b[i][j];
                b12[i][j] = b[i][j + a.length / 2];
                b21[i][j] = b[i + a.length / 2][j];
                b22[i][j] = b[i + a.length / 2][j + a.length / 2];
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

        multCounter += 7;
        addCounter += 18;

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

    public static void part4() {
        //Problem 1.1 done for 1.4
        start = System.currentTimeMillis();
        part1(4096);
        end = System.currentTimeMillis();
        System.out.println("Time taken to generate part 4 matrices: " + (end - start + "\n\n"));

        //Problem 1.2 done for 1.4
        multCounter = 0;
        addCounter = 0;
        start = System.currentTimeMillis();
        C = naive(A, B);
        end = System.currentTimeMillis();
        System.out.println("# of Multiplications Done: " + multCounter);
        System.out.println("# of Additions done: " + addCounter);
        System.out.println("Time taken to naively multiply part 4 matrices: " + (end - start) + "\n\n");
        
        //Problem 1.3 done for 1.4
        multCounter = 0;
        addCounter = 0;
        start = System.currentTimeMillis();
        //C = strassen(a, b); // --> Actual Full Recursive Definition, took forever so I made 3 levels one
        C = strassenNLvls(0, 3, A, B);
        end = System.currentTimeMillis();
        System.out.println("# of Multiplications Done: " + multCounter);
        System.out.println("# of Additions done: " + addCounter);
        System.out.println("Time taken to use strassen for part 4: " + (end - start) + "\n\n");
    }
}
