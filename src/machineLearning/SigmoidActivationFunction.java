package machineLearning;

public class SigmoidActivationFunction extends ActivationFunction {
	@Override
	public double activate(double num) {
		return 1 / (1 + Math.exp(-num));
	}

	@Override
	public double activateDerivative(double num) {
		return num * (1 - num);
	}
}
