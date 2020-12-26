package main;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class PipesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int PIPE_WIDTH = 65;
	private static final int MIDDLE_SPACE = 175;
	private static final int SPACE_BETWEEN_PIPES = 100;
	private static final int PIPE_END_HEIGHT = 30;
	private static final int PIPE_END_OFFSET = 2;

	private static final int SENSITIVITY = 8;
	
	private ArrayList<Rectangle> pipes;
	
	private double speed = 1;
	
	private ArrayList<Bird> birds;
	private Game game;
	
	public PipesPanel(ArrayList<Bird> birds, Game game) {
		this.birds = birds;
		this.game = game;
		pipes = new ArrayList<Rectangle>();
		reset();
	}
	
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
		//draw background
		g.drawImage(Game.getBackgroundImg(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);
		try {
			//draw bird
			for(int i = 0; i < birds.size(); i++) {
				Bird bird = birds.get(i);
				if(game.getIsRunning()) {
					g.drawImage(bird.getRotationTransformation().filter(Game.getBirdImg(), null), bird.getPosX(), bird.getPosY(), bird.getWidth(), bird.getHeight(), null);
				}
				if(bird.getPosY() + bird.getWidth() > Game.SCREEN_HEIGHT || bird.getPosY() < 0) {
					bird.die();
				}
			}
			//draw pipes
			for(int i = 0; i < pipes.size(); i++) {
				Rectangle pipe = pipes.get(i);
				if(game.getIsRunning()) {
					pipe.x -= Game.getDeltaTime() * speed;
				}
				if(pipe.x <= 0) {
					pipes.remove(i);
					i--;
				} else {
					g.drawImage(Game.getPipeImg(), (int)pipe.getX(), (int)pipe.getY(), (int)pipes.get(i).getWidth(), (int)pipe.getHeight(), null);
					if(i % 2 == 0) {
						g.drawImage(Game.getPipeEndImg(), (int)pipe.getX() - PIPE_END_OFFSET, (int)pipes.get(i).getY() + (int)pipe.getHeight() - PIPE_END_HEIGHT, (int)pipe.getWidth() + (PIPE_END_OFFSET * 2), PIPE_END_HEIGHT, null);
					} else {
						g.drawImage(Game.getPipeEnd2Img(), (int)pipe.getX() - PIPE_END_OFFSET, (int)pipes.get(i).getY(), (int)pipe.getWidth() + (PIPE_END_OFFSET * 2), PIPE_END_HEIGHT, null);
					}
				}
			}
			if(pipes.isEmpty() || pipes.get(pipes.size()-1).x <= (Game.SCREEN_WIDTH - (PIPE_WIDTH + SPACE_BETWEEN_PIPES))) {
				int pipeHeight = (int)(Math.random() * 200) + 100;
				pipes.add(new Rectangle(Game.SCREEN_WIDTH, 0, PIPE_WIDTH, pipeHeight));
				pipes.add(new Rectangle(Game.SCREEN_WIDTH, pipeHeight + MIDDLE_SPACE, PIPE_WIDTH, Game.SCREEN_HEIGHT - pipeHeight - MIDDLE_SPACE));
			}
			// draw points
			if(birds.size() > 0) {
				Rectangle[] closestPipe = getClosestPipes(birds.get(0).getPosX());
				for(int i = 0; i < birds.size(); i++) {
					Bird bird = birds.get(i);
					Oval birdBounds = new Oval(
							   bird.getPosX() + (bird.getWidth() / 2) - (SENSITIVITY / 2),
							   bird.getPosY() + (bird.getHeight() / 2) - (SENSITIVITY / 2),
							   (bird.getWidth() / 2) + (SENSITIVITY / 2),
							   (bird.getHeight() / 2) + (SENSITIVITY / 2),
							   bird.getRotation()
							  );
					if(birdBounds.intersects(closestPipe[0]) || birdBounds.intersects(closestPipe[1])) {
						bird.die();
					}
					if(closestPipe[0].x + PIPE_WIDTH < birds.get(i).getPosX() + birds.get(i).getWidth()) {
						bird.points++;
					}
				}
			}
			g.setColor(Color.WHITE);
			g.setFont(Main.TEXT_FONT);
			if(Game.mode == 0 || Game.mode == 1) {
				g.drawString("Points: " + birds.get(0).points, 5, 35);
			} else {
				int maxPoints = 0;
				int index = 0;
				for(int i = 0; i < birds.size(); i++) {
					if(birds.get(i).points > maxPoints) {
						maxPoints = birds.get(i).points;
						index = i;
					}
				}
				if(maxPoints > game.getMaxPoints()) {
					game.setMaxPoints(birds.get(index));
				}
				g.drawString("Generation: " + game.getGeneticAlgorithm().getGeneration(), 5, 35);
				g.drawString("Points: " + maxPoints, 5, 75);
				g.drawString("Best Points: " + game.getMaxPoints(), 5, 115);
			}
			if(!game.getIsRunning()) {
				g.setFont(Main.TITLE_FONT);
			    FontMetrics metrics = g.getFontMetrics(g.getFont());
			    String text = "You Lost!";
				g.drawString(text, (Game.SCREEN_WIDTH - metrics.stringWidth(text)) / 2, ((Game.SCREEN_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent());
			}
			game.removeBirds();
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	public void reset() {
		pipes.clear();
		int pipeHeight = (int)(Math.random() * 200) + 100;
		pipes.add(new Rectangle(Game.SCREEN_WIDTH, 0, PIPE_WIDTH, pipeHeight));
		pipes.add(new Rectangle(Game.SCREEN_WIDTH, pipeHeight + MIDDLE_SPACE, PIPE_WIDTH, Game.SCREEN_HEIGHT - pipeHeight - MIDDLE_SPACE));
	}
	
	public Rectangle[] getClosestPipes(int x) {
		int index = 0;
		int minDiff = Integer.MAX_VALUE;
		for(int i = 0; i < pipes.size(); i += 2) {
			int diff = pipes.get(i).x - x;
			if(diff > 0 && diff < minDiff) {
				minDiff = diff;
				index = i;
			}
		}
		return new Rectangle[] { pipes.get(index), pipes.get(index+1) };
	}
}
