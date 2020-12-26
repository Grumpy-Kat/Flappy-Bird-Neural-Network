package machineLearning;

public class Matrix {
	public double[][] matrix;
	
	private int rows = 0;
	private int cols = 0;
	
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		matrix = new double[rows][cols];
	}

	public Matrix copy() {
		Matrix newMatrix = new Matrix(rows, cols);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				newMatrix.matrix[i][j] = matrix[i][j];
			}
		}
		return newMatrix;
	}
	
	public double[] toArray() {
		double[] arr = new double[rows * cols];
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				arr[i * cols + j] = matrix[i][j];
			}
		}
		return arr;
	}

	public static Matrix fromArray(double[] arr) {
		Matrix newMatrix = new Matrix(arr.length, 1);
		for(int i = 0; i < newMatrix.getRows(); i++) {
			newMatrix.matrix[i][0] = arr[i];
		}
		return newMatrix;
	}

	public Matrix randomize() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				matrix[i][j] = (Math.random() * 2) - 1;
			}
		}
		return this;
	}

	public Matrix add(Matrix other) {
		if(rows != other.getRows() || cols != other.getCols()) {
			System.out.println("Error: The rows and columns of the matrices must match.");
			return null;
		}
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				matrix[i][j] += other.matrix[i][j];
			}
		}
		return this;
	}
	
	public Matrix add(double num) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				matrix[i][j] += num;
			}
		}
		return this;
	}

	public static Matrix subtract(Matrix one, Matrix two) {
		if(one.getRows() != two.getRows() || one.getCols() != two.getCols()) {
			System.out.println("Error: The rows and columns of the matrices must match.");
			return null;
		}
		Matrix newMatrix = new Matrix(one.getRows(), one.getCols());
		for(int i = 0; i < one.getRows(); i++) {
			for(int j = 0; j < one.getCols(); j++) {
				newMatrix.matrix[i][j] = one.matrix[i][j] - two.matrix[i][j];
			}
		}
		return newMatrix;
	}
	
	public static Matrix transpose(Matrix matrix) {
		Matrix newMatrix = new Matrix(matrix.getCols(), matrix.getRows());
		for(int i = 0; i < newMatrix.getRows(); i++) {
			for(int j = 0; j < newMatrix.getCols(); j++) {
				newMatrix.matrix[i][j] = matrix.matrix[j][i];
			}
		}
		return newMatrix;
	}
	
	public static Matrix dotProduct(Matrix one, Matrix two) {
		if(one.getCols() != two.getRows()) {
			System.out.println("Error: The columns of One must match the rows of Two.");
			return null;
		}
		Matrix newMatrix = new Matrix(one.getRows(), two.getCols());
		for(int i = 0; i < newMatrix.getRows(); i++) {
			for(int j = 0; j < newMatrix.getCols(); j++) {
				double sum = 0;
				for(int k = 0; k < one.cols; k++) {
					sum += one.matrix[i][k] * two.matrix[k][j];
				}
				newMatrix.matrix[i][j] = sum;
			}
		}
		return newMatrix;
	}

	public Matrix multiply(Matrix other) {
		if(rows != other.getRows() || cols != other.getCols()) {
			System.out.println("Error: The rows and columns of the matrices must match.");
			return null;
		}
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				matrix[i][j] *= other.matrix[i][j];
			}
		}
		return this;
	}
	
	public Matrix multiply(double num) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				matrix[i][j] *= num;
			}
		}
		return this;
	}
	
	public String toString() {
		String print = "";
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				print += matrix[i][j] + " ";
			}
			print += "\n";
		}
		return print;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
}