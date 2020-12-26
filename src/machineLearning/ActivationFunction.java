package machineLearning;

public abstract class ActivationFunction {
	public abstract double activate(double num);
	public abstract double activateDerivative(double num);
	
	public Matrix activate(Matrix matrix) {
		Matrix result = new Matrix(matrix.getRows(), matrix.getCols());
		for(int i = 0; i < matrix.getRows(); i++) {
			for(int j = 0; j < matrix.getCols(); j++) {
				result.matrix[i][j] = activate(matrix.matrix[i][j]);
			}
		}
		return result;
	}
	
	public Matrix activateDerivative(Matrix matrix) {
		Matrix result = new Matrix(matrix.getRows(), matrix.getCols());
		for(int i = 0; i < matrix.getRows(); i++) {
			for(int j = 0; j < matrix.getCols(); j++) {
				result.matrix[i][j] = activateDerivative(matrix.matrix[i][j]);
			}
		}
		return result;
	}
}
