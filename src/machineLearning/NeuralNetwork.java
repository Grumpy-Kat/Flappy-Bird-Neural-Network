package machineLearning;

public class NeuralNetwork {
	private static final double LEARNING_RATE = 0.1;
	
	public int inputNodes = 1;
	public int hiddenNodes = 1;
	public int outputNodes = 1;
	
	public Matrix weightsIH;
	public Matrix weightsHO;
	
	public Matrix biasH;
	public Matrix biasO;
	
	public String activationFuncType;
	private ActivationFunction activationFunc;
	
	public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
		this.inputNodes = inputNodes;
		this.hiddenNodes = hiddenNodes;
		this.outputNodes = outputNodes;
		defaultInit();
	}
	
	public NeuralNetwork(NeuralNetwork other) {
		if(other == null) {
			defaultInit();
		} else {
			this.inputNodes = other.inputNodes;
			this.hiddenNodes = other.hiddenNodes;
			this.outputNodes = other.outputNodes;
			this.weightsIH = other.weightsIH.copy();
			this.weightsHO = other.weightsHO.copy();
			this.biasH = other.biasH.copy();
			this.biasO = other.biasO.copy();
			setActivationFunction(other.activationFuncType);
		}
	}
	
	private void defaultInit() {
		weightsIH = new Matrix(this.hiddenNodes, this.inputNodes);
		weightsIH.randomize();
		weightsHO = new Matrix(this.outputNodes, this.hiddenNodes);
		weightsHO.randomize();
		biasH = new Matrix(this.hiddenNodes, 1);
		biasH.randomize();
		biasO = new Matrix(this.outputNodes, 1);
		biasO.randomize();
		setActivationFunction("Sigmoid");
	}

	public double[] predict(double[] inputArr) {
		Matrix inputs = Matrix.fromArray(inputArr);
		Matrix hidden = Matrix.dotProduct(weightsIH, inputs);
		hidden.add(biasH);
		hidden = activationFunc.activate(hidden);
		Matrix outputs = Matrix.dotProduct(weightsHO, hidden);
		outputs.add(biasO);
		outputs = activationFunc.activate(outputs);
		return outputs.toArray();
	}

	public void setActivationFunction(String activationFuncType) {
		this.activationFuncType = activationFuncType;
		switch(activationFuncType) {
			case "Sigmoid":
			case "Sigmoid Activation Func":
			case "Sigmoid Activation Function":
				activationFunc = new SigmoidActivationFunction();
				break;
			default:
				System.out.println(activationFuncType + " is not a valid activation type");
				break;
		}
	}

	public void train(double[] inputArr, double[] targetArr) {
		//predicting value
		Matrix inputs = Matrix.fromArray(inputArr);
		Matrix hidden = Matrix.dotProduct(weightsIH, inputs);
		hidden.add(biasH);
		hidden = activationFunc.activate(hidden);
		Matrix outputs = Matrix.dotProduct(weightsHO, hidden);
		outputs.add(biasO);
		outputs = activationFunc.activate(outputs);
		//retrieving actual value
		Matrix targets = Matrix.fromArray(targetArr);
		//calculating the errors and gradients/deltas
		Matrix outputErr = Matrix.subtract(targets, outputs);
		Matrix gradient = activationFunc.activateDerivative(outputs);
		gradient.multiply(outputErr);
		gradient.multiply(LEARNING_RATE);
		Matrix deltaWeightsHO = Matrix.dotProduct(gradient, Matrix.transpose(hidden));
		//adjusting the weights and bias
		weightsHO.add(deltaWeightsHO);
		biasO.add(gradient);
		//calculating the errors and gradients/delta
		Matrix hiddenErr = Matrix.dotProduct(Matrix.transpose(weightsHO), outputErr);
		Matrix hiddenGradient = activationFunc.activateDerivative(hidden);
		hiddenGradient.multiply(hiddenErr);
		hiddenGradient.multiply(LEARNING_RATE);
		Matrix deltaWeightsIH = Matrix.dotProduct(hiddenGradient, Matrix.transpose(inputs));
		//adjusting the weights and bias
		weightsIH.add(deltaWeightsIH);
		biasH.add(hiddenGradient);
	}
}
