package machineLearning;

import java.util.Random;

public class Util {
	public static double map(double num, double orgMin, double orgMax, double newMin, double newMax) {
		return (num - orgMin) / (orgMax - orgMin) * (newMax - newMin) + newMin;
	}
	
	public static double gaussianRandom() {
		Random rand = new Random(System.currentTimeMillis());
		return rand.nextGaussian();
	}
}
