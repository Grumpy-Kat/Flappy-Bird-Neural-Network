package main;

import machineLearning.Matrix;
import machineLearning.NeuralNetwork;
import machineLearning.Util;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class Bird {
	private static final double FALLING_SPEED = 5;
	private static final double JUMP_HEIGHT = 2;
	private static final int START_OFFSET = 100;
	private static final double SIZE_MULTIPLIER = 1.2;
	
	private double velocityY = 0;
	private int posX = 0;
	private int posY = 0;
	
	private int width = 50;
	private int height = 50;
	
	private boolean isAlive = true;
	public int points = 0;
	
	private NeuralNetwork brain;
	public double fitness = 0;
	
	public Bird(NeuralNetwork brain, boolean mutate) {
		posX = (Game.SCREEN_WIDTH / 2) - (width / 2) - START_OFFSET;
		posY = (Game.SCREEN_HEIGHT / 2) - (height / 2);
		width = (int)(Game.getBirdImg().getWidth() * SIZE_MULTIPLIER);
		height = (int)(Game.getBirdImg().getHeight() * SIZE_MULTIPLIER);
		if(brain == null) {
			this.brain = new NeuralNetwork(5, 8, 2);
		} else {
			this.brain = new NeuralNetwork(brain);
			if(mutate) {
				this.brain.weightsIH = mutate(this.brain.weightsIH);
				this.brain.weightsHO = mutate(this.brain.weightsHO);
				this.brain.biasH = mutate(this.brain.biasH);
				this.brain.biasO = mutate(this.brain.biasO);
			}
		}
	}
	
	public Bird copy() {
	    return new Bird(brain, true);
	}
	
	public Matrix mutate(Matrix matrix) {
		Matrix newMatrix = matrix.copy();
		for(int i = 0; i < matrix.getRows(); i++) {
			for(int j = 0; j < matrix.getCols(); j++) {
				newMatrix.matrix[i][j] = mutate(matrix.matrix[i][j]);
			}
		}
		return newMatrix;
	}
	
	public double mutate(double val) {
		if(Math.random() < 0.1) {
			double mutation = Util.gaussianRandom() * 0.5;
			return val + mutation;
		} else {
			return val;
		}
	}
	
	public void think(PipesPanel pipes) {
		points++;
		Rectangle[] closestPipes = pipes.getClosestPipes(posX);
		if(closestPipes != null) {
			double[] inputs = new double[5];
			inputs[0] = Util.map(closestPipes[0].x, posX, width, 0, 1);
			inputs[1] = Util.map(closestPipes[0].height, 0, Game.SCREEN_HEIGHT, 0, 1);
			inputs[2] = Util.map(closestPipes[1].y, 0, Game.SCREEN_HEIGHT, 0, 1);
			inputs[3] = Util.map(posY, 0, Game.SCREEN_HEIGHT, 0, 1);
			inputs[4] = Util.map(velocityY, -15, 15, 0, 1);
			double[] outputs = brain.predict(inputs);
			if(outputs[1] > outputs[0]) {
				jump();
			}
		}

	}
	
	public void update() {
		posY += velocityY;
		velocityY += FALLING_SPEED * Game.getDeltaTime();
	}
	
	public void die() {
		isAlive = false;
	}
	
	public void jump() {
		velocityY = -JUMP_HEIGHT;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public double getVelocityY() {
		return velocityY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getRotation() {
		return Math.toRadians(velocityY > 0 ? 25 : -15);
	}
	
	public AffineTransformOp getRotationTransformation() {
		AffineTransform transform = AffineTransform.getRotateInstance(getRotation(), width / 2, height / 2);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		return op;
	}
	
	public boolean getIsAlive() {
		return isAlive;
	}
	
	public NeuralNetwork getBrain() {
		return brain;
	}
}
