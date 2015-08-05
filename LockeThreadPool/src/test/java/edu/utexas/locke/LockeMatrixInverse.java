package test.java.edu.utexas.locke;

import java.util.Random;

import main.java.edu.utexas.locke.LockeTask;
import main.java.edu.utexas.locke.LockeThreadPool;

public class LockeMatrixInverse extends LockeTask<Void> {
	private static class Matrix {

		private int beginRow;
		private int endRow;
		private int beginColumn;
		private int endColumn;

		private double[][] matrix;

		public Matrix(int n) {
			this(new double[n][n], 0, n, 0, n);
		}

		public Matrix(int n, int m) {
			this(new double[n][m], 0, n, 0, m);
		}

		private Matrix(double[][] matrix, int beginRow, int endRow, int beginColumn, int endColumn) {
			this.matrix = matrix;
			this.beginRow = beginRow;
			this.endRow = endRow;
			this.beginColumn = beginColumn;
			this.endColumn = endColumn;
		}

		public int getNumRows() {
			return this.endRow - this.beginRow;
		}

		private int getNumColumns() {
			return this.endColumn - this.beginColumn;
		}

		public double get(int row, int column) {
			return matrix[beginRow + row][beginColumn + column];
		}

		public void set(int row, int column, double value) {
			matrix[beginRow + row][beginColumn + column] = value;
		}

		public Matrix createView(int beginRow, int endRow, int beginColumn, int endColumn) {
			beginRow += this.beginRow;
			endRow += this.beginRow;
			beginColumn += this.beginColumn;
			endColumn += this.beginColumn;
			return new Matrix(this.matrix, beginRow, endRow, beginColumn, endColumn);
		}

		public static Matrix multiply(Matrix matrixA, Matrix matrixB) {
			assert matrixA.getNumColumns() == matrixB.getNumRows();
			Matrix result = new Matrix(matrixA.getNumRows(), matrixB.getNumColumns());

			for (int row = 0; row < matrixA.getNumRows(); row++) {
				for (int column = 0; column < matrixB.getNumColumns(); column++) {
					double sumProduct = 0.0;
					for (int k = 0; k < matrixA.getNumColumns(); k++) {
						sumProduct += (matrixA.get(row, k) * matrixB.get(k, column));
					}
					result.set(row, column, sumProduct);
				}
			}

			return result;
		}

		public static void initializeAsRandomLowerTriangularSquareMatrix(Matrix matrix) {
			assert matrix.getNumRows() == matrix.getNumColumns();

			Random random = new Random();
			for (int row = 0; row < matrix.getNumRows(); row++) {
				for (int column = 0; column < row + 1; column++) {
					matrix.set(row, column, random.nextDouble() * 1000);
				}
			}
		}

		public Matrix copy() {
			Matrix matrix = new Matrix(this.getNumRows(), this.getNumColumns());

			for (int row = 0; row < this.getNumRows(); row++) {
				for (int column = 0; column < this.getNumColumns(); column++) {
					matrix.set(row, column, this.get(row, column));
				}
			}

			return matrix;
		}

		public void assign(Matrix matrix) {
			assert this.getNumRows() == matrix.getNumRows();
			assert this.getNumColumns() == matrix.getNumColumns();

			for (int row = 0; row < this.getNumRows(); row++) {
				for (int column = 0; column < this.getNumColumns(); column++) {
					this.set(row, column, matrix.get(row, column));
				}
			}
		}

		public Matrix negate() {
			Matrix matrix = new Matrix(this.getNumRows(), this.getNumColumns());

			for (int row = 0; row < this.getNumRows(); row++) {
				for (int column = 0; column < this.getNumColumns(); column++) {
					matrix.set(row, column, -this.get(row, column));
				}
			}

			return matrix;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int row = 0; row < this.getNumRows(); row++) {
				for (int column = 0; column < this.getNumColumns(); column++) {
					builder.append(String.format("%.4f", get(row, column)));
					builder.append(" ");
				}
				builder.append("\n");
			}

			return builder.toString();
		}
	}

	private Matrix matrix;

	public LockeMatrixInverse(Matrix matrix) {
		this.matrix = matrix;
	}

	public Void compute() {
		if (matrix.getNumRows() == 1) {
			matrix.set(0, 0, 1 / matrix.get(0, 0));
			return null;
		} else {
			int end = matrix.getNumRows();
			int mid = end / 2;

			Matrix matrix11 = matrix.createView(0, mid, 0, mid);
			Matrix matrix21 = matrix.createView(mid, end, 0, mid);
			Matrix matrix22 = matrix.createView(mid, end, mid, end);

			LockeMatrixInverse forkJoinMatrixInverse11 = new LockeMatrixInverse(matrix11);
			forkJoinMatrixInverse11.fork();

			LockeMatrixInverse forkJoinMatrixInverse22 = new LockeMatrixInverse(matrix22);
			forkJoinMatrixInverse22.compute();

			forkJoinMatrixInverse11.join();

			matrix21.assign(Matrix.multiply(matrix21, matrix11));
			matrix21.assign(Matrix.multiply(matrix22.negate(), matrix21));
			return null;
		}
	}

	public static void main(String[] args) {
		PerformanceMonitor performanceMonitor = new PerformanceMonitor();

		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processors);
		System.out.println();

		int n = 5;
		if (args.length > 0) {
			n = Integer.parseInt(args[0]);
		}

		System.out.println("Empty Matrix");
		Matrix matrix = new Matrix(n);
		System.out.println(matrix);
		System.out.println();

		System.out.println("Randomized Lower Triangular Square Matrix");
		Matrix.initializeAsRandomLowerTriangularSquareMatrix(matrix);
		System.out.println(matrix);
		System.out.println();

		Matrix inverse = matrix.copy();

		LockeMatrixInverse forkJoinMatrixInverse = new LockeMatrixInverse(inverse);
		LockeThreadPool pool = new LockeThreadPool(processors);
		pool.invoke(forkJoinMatrixInverse);

		System.out.println("Inverse Matrix");
		System.out.println(inverse);
		System.out.println();

		System.out.println("Identity Matrix");
		Matrix identity = Matrix.multiply(inverse, matrix);
		System.out.println(identity);
		System.out.println();

		performanceMonitor.gatherMetricsAndPrint();
	}
}
