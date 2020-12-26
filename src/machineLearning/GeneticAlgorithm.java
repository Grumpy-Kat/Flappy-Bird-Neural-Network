package machineLearning;

import main.Bird;
import main.Game;

import java.util.ArrayList;

public class GeneticAlgorithm {
	public static final int POPULATION_SIZE = 600;
	
	private int generation = 1;
	
	@SuppressWarnings("unchecked")
	public void nextGeneration(ArrayList<Bird> savedBirds, Game game) {
		savedBirds = (ArrayList<Bird>)savedBirds.clone();
		game.reset();
		calculateFitness(savedBirds);
		game.addBirds(generate(savedBirds));
		generation++;
	}
	
	public void calculateFitness(ArrayList<Bird> birds) {
		double sum = 0;
		for(int i = 0; i < birds.size(); i++) {
			birds.get(i).points = (int)Math.pow(birds.get(i).points, 2);
			sum += birds.get(i).points;
		}
		for(int i = 0; i < birds.size(); i++) {
			birds.get(i).fitness = birds.get(i).points / sum;
		}
	}
	
	public ArrayList<Bird> generate(ArrayList<Bird> birds) {
		ArrayList<Bird> newBirds = new ArrayList<Bird>();
		for(int i = 0; i < birds.size(); i++) {
			newBirds.add(selectFromPool(birds));
		}
		return newBirds;
	}
	
	public Bird selectFromPool(ArrayList<Bird> birds) {
		int i = 0;
		double rand = Math.random();
		while(rand > 0) {
			rand -= birds.get(i).fitness;
			i++;
		}
		i -= 1;
		return birds.get(i).copy();
	}
	
	public int getGeneration() {
		return generation;
	}
}
